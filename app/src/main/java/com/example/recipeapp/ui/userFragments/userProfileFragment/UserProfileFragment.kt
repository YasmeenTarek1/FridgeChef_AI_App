package com.example.recipeapp.ui.userFragments.userProfileFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentUserProfileBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: UserProfileViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserProfileBinding.bind(view)
        repository = Repository(RetrofitInstance() , AppDatabase.getInstance(requireContext()))

        val factory = UserProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(UserProfileViewModel::class.java)

        lifecycleScope.launch {
            binding.user = viewModel.getUserById(AppUser.instance!!.userId!!)
            if (binding.user!!.image != null) {
                binding.userAvatar.scaleX = 1.0f
                binding.userAvatar.scaleY = 1.0f
                Glide.with(requireContext())
                    .load(binding.user!!.image)
                    .into(binding.userAvatar)
            } else {
                binding.userAvatar.scaleX = 1.3f
                binding.userAvatar.scaleY = 1.3f
                Glide.with(requireContext())
                    .load(R.drawable.no_avatar)
                    .into(binding.userAvatar)
            }
        }

        binding.editProfileInfo.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_userInfoFragment)
        }
    }
}