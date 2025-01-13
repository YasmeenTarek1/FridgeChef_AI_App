package com.example.fridgeChefAIApp.ui.loginFragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fridgeChefAIApp.AppUser
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentIntroBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroFragment : Fragment(R.layout.fragment_intro) {

    private lateinit var binding: FragmentIntroBinding
    private val viewModel: FetchingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIntroBinding.bind(view)
        if (FirebaseAuth.getInstance().currentUser != null) {
            lifecycleScope.launch{
                AppUser.instance!!.userId = FirebaseAuth.getInstance().currentUser!!.uid
                findNavController().navigate(R.id.action_introFragment_to_feedFragment)
                viewModel.fetchUserInfoFromFirestoreAndInitializeRoom()
            }
        }

        binding.getStartedTV.setOnClickListener{
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
        binding.getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
    }
}