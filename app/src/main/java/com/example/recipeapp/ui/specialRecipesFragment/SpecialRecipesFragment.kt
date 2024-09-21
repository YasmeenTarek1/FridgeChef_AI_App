package com.example.recipeapp.ui.specialRecipesFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.R
import com.example.recipeapp.databinding.FragmentSpecialRecipesBinding

class SpecialRecipesFragment : Fragment(R.layout.fragment_special_recipes) {

    private lateinit var binding: FragmentSpecialRecipesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSpecialRecipesBinding.bind(view)
        binding.lifecycleOwner = this

        binding.favoriteRecipesButton.setOnClickListener {
            findNavController().navigate(R.id.action_specialRecipesFragment_to_favoriteRecipesFragment)
        }

        binding.cookedRecipesButton.setOnClickListener {
            findNavController().navigate(R.id.action_specialRecipesFragment_to_cookedRecipesFragment)
        }

    }
}