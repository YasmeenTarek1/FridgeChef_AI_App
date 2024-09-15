package com.example.recipeapp.ui.userFragments.userInfoFragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentUserInfoBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Locale

class UserInfoFragment : Fragment(R.layout.fragment_user_info) {

    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var viewModel: UserInfoViewModel
    private lateinit var repository: Repository


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentUserInfoBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = UserInfoViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserInfoViewModel::class.java)

        binding.buttonSave.setOnClickListener {
            val name = binding.usernameEditText.text.toString()
            val weight = binding.editTextWeight.text.toString().toInt()
            val height = binding.editTextHeight.text.toString().toInt()
            val goal = binding.spinnerGoal.selectedItem.toString()
            val dietType = binding.spinnerDiet.selectedItem.toString()
            val age = binding.editTextAge.text.toString().toInt()
            var gender = ""

            binding.radioGroup2.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId){
                    R.id.female -> gender = "Female"
                    R.id.male -> gender = "Male"
                }
            }

            var userInfo:UserInfo? = null

            if (weight != null && height != null) {
                userInfo = UserInfo(id = AppUser.instance!!.userId!! , name = name, weight = weight, height = height, goal = goal , age = age , gender = gender , dietType = dietType , bmi = calculateBMI(weight,height))
                viewModel.insertUser(userInfo)
            } else {
                Toast.makeText(context, "Please enter valid data", Toast.LENGTH_SHORT).show()
            }


            saveUserInfoInFirestoreAndRoom(userInfo!!)
            binding.buttonSave.setOnClickListener {
                findNavController().navigate(R.id.action_userInfoFragment_to_homeFragment)
            }
        }
    }

    private fun saveUserInfoInFirestoreAndRoom(userinfo:UserInfo) {
        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext())!!.userDao().insertUser(userinfo)
        }

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(userinfo.id)
            .set(userinfo)
            .addOnSuccessListener {
                println("User profile added to Firestore successfully!")
            }
            .addOnFailureListener { exception ->
                println("Error adding user profile to Firestore: ${exception.message}")
            }
    }

    private fun calculateBMI(weight: Int, height: Int): Double {
        val heightInMeters = height / 100.0
        val bmi = weight / (heightInMeters * heightInMeters)
        return String.format(Locale.US, "%.1f", bmi).toDouble()
    }

}