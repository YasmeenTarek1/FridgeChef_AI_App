package com.example.recipeapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentLoginBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.example.recipeapp.room_DB.model.ToBuyIngredient
import com.example.recipeapp.room_DB.model.UserInfo
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var callbackManager: CallbackManager
    private lateinit var repository: Repository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        auth = FirebaseAuth.getInstance()
        AppUser.instance!!.userId = auth.currentUser?.uid

        if (AppUser.instance!!.userId != null) {
            // User is already logged in, navigate to home fragment
            fetchUserInfoFromFirestoreAndSyncWithRoom()
            goToHomePage()
        }

        credentialManager = CredentialManager.create(requireContext())
        callbackManager = CallbackManager.Factory.create()

        // Initialize Facebook login
        initializeFacebookLogin()

        // Set up buttons
        setupButtons()
    }

    private fun setupButtons() {
        binding.SignUpbutton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.Loginbutton.setOnClickListener {
            loginWithEmail()
        }

        binding.Gmailbutton.setOnClickListener {
            loginWithGmail()
        }
    }

    private fun loginWithEmail() {
        val email = binding.EmailEditText.text.toString().trim()
        val password = binding.PasswordEditText.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            AppUser.instance!!.userId = auth.currentUser?.uid
                            val userName = repository.getUserById(AppUser.instance!!.userId!!)!!.name
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    "Welcome Back, $userName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            fetchUserInfoFromFirestoreAndSyncWithRoom() // It's already collected before
                        }
                        goToHomePage()
                    } else {
                        Log.w("LoginFragment", "signInWithEmail:failure", task.exception)
                        Toast.makeText(requireContext(), "Email or Password is incorrect", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_LONG).show()
        }
    }

    private fun loginWithGmail() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("6638631419-bmrnqsggi7jvd0rm40ihpaqisv05pppo.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val response = credentialManager.getCredential(
                    request = request,
                    context = requireContext(),
                )
                handleGoogleSignIn(response)
            } catch (e: GetCredentialException) {
                Log.e("LoginFragment", "Credential request failed", e)
            }
        }
    }

    private fun handleGoogleSignIn(response: GetCredentialResponse) {
        val credential = response.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val id = googleIdTokenCredential.idToken
                        firebaseAuthWithGoogle(id)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("LoginFragment", "Received an invalid google id token response", e)
                    }
                }
            }
            else -> {
                Log.e("LoginFragment", "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    AppUser.instance!!.userId = auth.currentUser?.uid
                    Toast.makeText(requireContext(), "Google sign-in successful!", Toast.LENGTH_SHORT).show()
                    fetchUserInfoFromFirestoreAndSyncWithRoom() // I have to check if this is the fist sign in with this gmail
                    goToHomePage()
                } else {
                    Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Google authentication failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun initializeFacebookLogin() {
        binding.facebutton.setPermissions("email")

        binding.facebutton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken.token)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(requireContext(), "Facebook authentication failed", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun handleFacebookAccessToken(token: String) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                AppUser.instance!!.userId = auth.currentUser?.uid
                fetchUserInfoFromFirestoreAndSyncWithRoom()
                goToHomePage()
            } else {
                Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                Toast.makeText(requireContext(), "Email or Password is incorrect", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToHomePage() {
        findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
    }

    private fun fetchUserInfoFromFirestoreAndSyncWithRoom() {
        val userId: String = AppUser.instance!!.userId!!
        val firestore = FirebaseFirestore.getInstance()


        if(firestore.collection("users").document(userId) == null){
            findNavController().navigate(R.id.action_loginFragment_to_userInfoFragment)
        } else {

            // fetching UserInfo
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userInfo: UserInfo? = documentSnapshot.toObject(UserInfo::class.java)
                        userInfo?.let {
                            // Sync data with local Room database
                            lifecycleScope.launch {
                                if (repository.getUserById(userId) == null) {
                                    // NOT saved before to my Room DB so insert it
                                    repository.insertUser(userInfo)
                                } else {
                                    // just update the one saved in the Room DB
                                    repository.updateUser(userInfo)
                                }
                            }
                        }
                    }
                }

            // fetching Favorite Recipes
            firestore.collection("users").document(userId)
                .collection("Favorite Recipes") // sub Collection
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Convert the documents to FavoriteRecipe objects
                    val favoriteRecipes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject<FavoriteRecipe>()
                    }

                    lifecycleScope.launch {
                        repository.insertFavoriteRecipes(favoriteRecipes)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("Error" , "Error in fetching fav Recipes from Firestore" , exception)
                }


            // fetching Cooked Recipes
            firestore.collection("users").document(userId)
                .collection("Cooked Recipes") // sub Collection
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Convert the documents to FavoriteRecipe objects
                    val cookedRecipes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject<CookedRecipe>()
                    }

                    lifecycleScope.launch {
                        repository.insertCookedRecipes(cookedRecipes)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("Error" , "Error in fetching cooked Recipes from Firestore" , exception)
                }


            // fetching To-Buy Ingredients
            firestore.collection("users").document(userId)
                .collection("To-Buy Ingredients") // sub Collection
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Convert the documents to FavoriteRecipe objects
                    val ingredients = querySnapshot.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject<ToBuyIngredient>()
                    }

                    lifecycleScope.launch {
                        repository.insertToBuyIngredients(ingredients)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("Error" , "Error in fetching To-Buy Ingredients from Firestore" , exception)
                }

            // fetching Ai Recipes
            firestore.collection("users").document(userId)
                .collection("Ai Recipes") // sub Collection
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Convert the documents to Ai Recipes objects
                    val aiRecipes = querySnapshot.documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject<AiRecipe>()
                    }

                    lifecycleScope.launch {
                        repository.insertAiRecipes(aiRecipes)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("Error" , "Error in fetching Ai Recipes from Firestore" , exception)
                }

        }

    }

}
