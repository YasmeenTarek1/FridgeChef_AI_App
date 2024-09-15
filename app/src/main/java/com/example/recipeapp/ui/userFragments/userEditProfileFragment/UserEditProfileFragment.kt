package com.example.recipeapp.ui.userFragments.userEditProfileFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentUserEditProfileBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.UserInfo
import kotlinx.coroutines.launch
import java.util.Locale

class UserEditProfileFragment : Fragment(R.layout.fragment_user_edit_profile) {

    private lateinit var binding: FragmentUserEditProfileBinding
    private lateinit var viewModel: UserEditProfileViewModel
    private lateinit var repository: Repository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserEditProfileBinding.bind(view)
        binding.lifecycleOwner = this

        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))
        val factory = UserEditProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserEditProfileViewModel::class.java)

        val db:AppDatabase = AppDatabase.getInstance(requireContext())!!
        var oldUser: UserInfo? = null

        lifecycleScope.launch {
            oldUser = db.userDao().getUserById(AppUser.instance!!.userId!!)!!
            binding.user = oldUser
        }


        binding.buttonSave.setOnClickListener {
            val weight = binding.editTextWeight.text.toString().toInt()
            val height = binding.editTextHeight.text.toString().toInt()

            lifecycleScope.launch {
                db.userDao().updateUser(
                    UserInfo(
                        oldUser!!.id,
                        name = binding.editTextName.text.toString(),
                        height = height,
                        weight = weight,
                        goal = binding.editTextGoal.text.toString(),
                        dietType = binding.editTextDietType.text.toString(),
                        age = oldUser!!.age,
                        gender = oldUser!!.gender,
                        bmi = calculateBMI(weight, height)
                    )
                )
            }
            findNavController().navigate(R.id.action_userEditProfileFragment_to_userProfileFragment)
        }

    }

    private fun calculateBMI(weight: Int, height: Int): Double {
        val heightInMeters = height / 100.0
        val bmi = weight / (heightInMeters * heightInMeters)
        return String.format(Locale.US, "%.1f", bmi).toDouble()
    }
}