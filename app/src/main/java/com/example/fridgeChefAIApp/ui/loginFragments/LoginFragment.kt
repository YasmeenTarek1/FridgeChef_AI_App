package com.example.fridgeChefAIApp.ui.loginFragments

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
import com.example.fridgeChefAIApp.AppUser
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.service.RetrofitInstance
import com.example.fridgeChefAIApp.databinding.FragmentLoginBinding
import com.example.fridgeChefAIApp.room_DB.database.AppDatabase
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var callbackManager: CallbackManager
    private lateinit var repository: Repository
    private lateinit var viewModel: FetchingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))
        viewModel = FetchingViewModel(repository)
        auth = FirebaseAuth.getInstance()

        credentialManager = CredentialManager.create(requireContext())
        callbackManager = CallbackManager.Factory.create()

        // Initialize Facebook login
        initializeFacebookLogin()

        // Set up buttons
        setupButtons()
    }

    private fun setupButtons() {
        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.loginButton.setOnClickListener {
            loginWithEmail()
        }

        binding.gmailButton.setOnClickListener {
            loginWithGmail()
        }
    }

    private fun loginWithEmail() {
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            AppUser.instance!!.userId = auth.currentUser?.uid
                            viewModel.fetchUserInfoFromFirestoreAndInitializeRoom() // It's already collected before
                            val userName = viewModel.getUserName()
                            withContext(Dispatchers.Main) {
                                goToHomePage()
                                Toast.makeText(requireContext(), "Welcome Back, $userName", Toast.LENGTH_SHORT).show()
                            }
                        }
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
            .setFilterByAuthorizedAccounts(false)
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
            Log.e("LoginFragment", "Credential request failed with exception type: ${e::class.java.simpleName}", e)
            }

        }
    }

    private fun handleGoogleSignIn(response: GetCredentialResponse) {
        when (val credential = response.credential) {
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
                Log.e("LoginFragment", "Unexpected credential type: ${credential::class.java.simpleName}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lifecycleScope.launch{
                        AppUser.instance!!.userId = auth.currentUser?.uid
                        val success = viewModel.fetchUserInfoFromFirestoreAndInitializeRoom()
                        if (success) {
                            val userName = viewModel.getUserName()
                            withContext(Dispatchers.Main) {
                                goToHomePage()
                                Toast.makeText(requireContext(), "Welcome Back, $userName", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else collectInfo()

                    }
                } else {
                    Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Google authentication failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun initializeFacebookLogin() {
        binding.faceButton.setPermissions("email")

        binding.faceButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken.token)
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {
                    Toast.makeText(requireContext(), "Facebook authentication failed", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun handleFacebookAccessToken(token: String) {
        val credential = FacebookAuthProvider.getCredential(token)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                lifecycleScope.launch (Dispatchers.IO) {
                    AppUser.instance!!.userId = auth.currentUser?.uid
                    val success = viewModel.fetchUserInfoFromFirestoreAndInitializeRoom()
                    if (success) {
                        val userName = viewModel.getUserName()
                        withContext(Dispatchers.Main) {
                            goToHomePage()
                            Toast.makeText(requireContext(), "Welcome Back, $userName", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else collectInfo()
                }
            } else {
                Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                Toast.makeText(requireContext(), "Email or Password is incorrect", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToHomePage(){
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFeedFragment())
    }

    private fun collectInfo(){
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUserInfoFragment())
    }
}
