package com.example.recipeapp.ui.userFragments.userProfileFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.databinding.FragmentUserProfileBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.UserInfo
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var binding: FragmentUserProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserProfileBinding.bind(view)
        binding.lifecycleOwner = this

        var userInfo: UserInfo?

        lifecycleScope.launch {
            userInfo = AppDatabase.getInstance(requireContext())!!.userDao().getUserById(AppUser.instance!!.userId!!)
            binding.user = userInfo!!
        }


        binding.editProfileInfo.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_userEditProfileFragment)
        }

    }
}