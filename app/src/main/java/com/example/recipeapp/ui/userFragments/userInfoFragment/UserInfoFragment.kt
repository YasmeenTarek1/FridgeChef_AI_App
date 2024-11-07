package com.example.recipeapp.ui.userFragments.userInfoFragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentUserInfoBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

class UserInfoFragment : Fragment(R.layout.fragment_user_info) {

    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var viewModel: UserInfoViewModel
    private lateinit var repository: Repository
    private lateinit var storageReference: StorageReference
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var uriImage:String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentUserInfoBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        storageReference = FirebaseStorage.getInstance().reference

        val factory = UserInfoViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserInfoViewModel::class.java)

        lifecycleScope.launch {
            binding.user = viewModel.getUserById(AppUser.instance!!.userId!!)
            if(binding.user != null) {
                withContext(Dispatchers.Main) {
                    Glide.with(requireContext())
                        .load(binding.user!!.image)
                        .into(binding.userAvatar)
                    uriImage = binding.user!!.image!!
                }

                if (binding.user!!.age != 0) { // Not a new Customer (Existing one)
                    val spinnerDiet: Spinner = binding.spinnerDiet
                    var adapter = spinnerDiet.adapter

                    for (i in 0 until adapter.count) {
                        if (adapter.getItem(i) == binding.user!!.dietType) {
                            spinnerDiet.setSelection(i)
                            break
                        }
                    }

                    val spinnerGoal: Spinner = binding.spinnerGoal
                    adapter = spinnerGoal.adapter

                    for (i in 0 until adapter.count) {
                        if (adapter.getItem(i) == binding.user!!.goal) {
                            spinnerGoal.setSelection(i)
                            break
                        }
                    }
                }
            }
        }

        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                uploadImageToFirebaseAndReturnDownloadURL(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.editProfileImage.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.usernameEditText.text.toString()
            val weight = binding.editTextWeight.text.toString().toInt()
            val height = binding.editTextHeight.text.toString().toInt()
            val goal = binding.spinnerGoal.selectedItem.toString()
            val dietType = binding.spinnerDiet.selectedItem.toString()
            var age = 0
            var gender = ""

            if (binding.user == null || binding.user!!.age == 0) { // new User or gender and age aren't tracked before
                age = binding.editTextAge.text.toString().toInt()
                binding.radioGroup2.setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        R.id.female -> gender = "Female"
                        R.id.male -> gender = "Male"
                    }
                }
            } else { // I already have the info
                age = binding.user!!.age
                gender = binding.user!!.gender
            }

            val userInfo = UserInfo(
                id = AppUser.instance!!.userId!!,
                name = name,
                weight = weight,
                height = height,
                goal = goal,
                age = age,
                gender = gender,
                dietType = dietType,
                bmi = calculateBMI(weight, height),
                image = uriImage
            )

            saveUserInfoInFirestoreAndRoom(userInfo)
            findNavController().navigate(R.id.action_userInfoFragment_to_userProfileFragment)
        }
    }

    private fun uploadImageToFirebaseAndReturnDownloadURL(imageUri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val ref = storageReference.child("avatars/$fileName")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    uriImage = uri.toString()

                    // Load the new image into ImageView after getting the download URL
                    Glide.with(requireContext())
                        .load(uriImage)
                        .into(binding.userAvatar)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("User", "Error uploading image: ${exception.message}")
            }
    }

    private fun saveUserInfoInFirestoreAndRoom(userinfo:UserInfo) {
        viewModel.insertUser(userinfo)
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(userinfo.id)
            .set(userinfo)
            .addOnSuccessListener {
                Log.d("User" , "User profile added to Firestore successfully!")
            }
            .addOnFailureListener { exception ->
                Log.d("User" , "Error adding user profile to Firestore: ${exception.message}")
            }
    }

    private fun calculateBMI(weight: Int, height: Int): Double {
        val heightInMeters = height / 100.0
        val bmi = weight / (heightInMeters * heightInMeters)
        return String.format(Locale.US, "%.1f", bmi).toDouble()
    }

}