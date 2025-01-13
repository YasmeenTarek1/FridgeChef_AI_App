package com.example.fridgeChefAIApp.ui.userFragments.userProfileFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.fridgeChefAIApp.AppUser
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentUserProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var binding: FragmentUserProfileBinding
    private val viewModel: UserProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentUserProfileBinding.bind(view)
        lifecycleScope.launch {
            binding.user = viewModel.getUserById(AppUser.instance!!.userId!!)
            if (binding.user!!.image != null) {
                binding.userAvatar.scaleX = 1.0f
                binding.userAvatar.scaleY = 1.0f
                Glide.with(requireContext())
                    .load(binding.user!!.image)
                    .into(binding.userAvatar)
            } else {
                binding.userAvatar.scaleX = 1.35f
                binding.userAvatar.scaleY = 1.35f
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