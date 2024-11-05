package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch

class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var repository: Repository
    private lateinit var recipeDetailsViewModel: RecipeDetailsViewModel
    private val args: RecipeDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecipeDetailsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = RecipeDetailsViewModelFactory(repository)
        recipeDetailsViewModel = ViewModelProvider(this, factory).get(RecipeDetailsViewModel::class.java)

        val currentRecipe = args.recipe

        lifecycleScope.launch {
            // Retrieving the image & summary if not there (because Recipe has no image & no summary)
            if (currentRecipe.image == null) {
                currentRecipe.image = recipeDetailsViewModel.getRecipeInfo(currentRecipe.id).image
                currentRecipe.summary = recipeDetailsViewModel.getRecipeInfo(currentRecipe.id).summary
            }
            displayRecipeImage(currentRecipe)
            binding.recipe = currentRecipe
            Log.d("Recipe" , currentRecipe.toString())
        }

        binding.stepsButton.setOnClickListener {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToRecipeStepsFragment(currentRecipe!!)
            findNavController().navigate(action)
        }

        binding.aiOpinionButton.setOnClickListener {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToChatBotServiceFragment(null, currentRecipe, 1)
            findNavController().navigate(action)
        }
    }

    private fun displayRecipeImage(recipe: Recipe) {
        Glide.with(binding.root)
            .load(recipe.image)
            .into(binding.recipeImage)
    }
}

