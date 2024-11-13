package com.example.recipeapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.databinding.FragmentIntroBinding

class IntroFragment : Fragment(R.layout.fragment_intro) {

    private lateinit var binding: FragmentIntroBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIntroBinding.bind(view)

        if (AppUser.instance!!.userId != null) {
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
        binding.getStartedTV.setOnClickListener{
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
        binding.getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
    }
}