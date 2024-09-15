package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFregment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.CookedRecipe


class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var repository: Repository
    private lateinit var recipeViewModel: RecipeViewModel
    private val args: RecipeDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecipeDetailsBinding.bind(view)
        binding.lifecycleOwner = this

        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val recipeId: Int = args.recipeID

        val factory = RecipeViewModelFactory(recipeId , repository)
        recipeViewModel = ViewModelProvider(this, factory).get(RecipeViewModel::class.java)

        var recipe:DetailedRecipeResponse = recipeViewModel.getRecipeInfo()
        displayRecipeInfo(recipe)

        val cookedrecipe = CookedRecipe(recipe.id, recipe.title, recipe.image, recipe.readyInMinutes, recipe.servings, recipe.healthScore)

        binding.stepsButton.setOnClickListener {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToRecipeStepsFragment(cookedrecipe.id , cookedrecipe)
            findNavController().navigate(action)
        }

        binding.aiOpinionButton.setOnClickListener {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToChatBotFragment(null , recipe, 2)
            findNavController().navigate(action)
        }
    }

    private fun displayRecipeInfo(recipe: DetailedRecipeResponse) {
        Glide.with(binding.root)
            .load(recipe!!.image)
            .into(binding.recipeImage)
    }
}