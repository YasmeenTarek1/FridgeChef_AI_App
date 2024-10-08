package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFregment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentRecipeDetailsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.AiRecipe
import com.example.recipeapp.room_DB.model.CookedRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var repository: Repository
    private lateinit var recipeDetailsViewModel: RecipeDetailsViewModel
    private val args: RecipeDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecipeDetailsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val recipeId: Int = args.recipeID
        val aiRecipe = args.aiRecipe

        val factory = RecipeDetailsViewModelFactory(recipeId , repository)
        recipeDetailsViewModel = ViewModelProvider(this, factory).get(RecipeDetailsViewModel::class.java)

        var cookedrecipe:CookedRecipe? = null
        var recipe:DetailedRecipeResponse? = null

        if(aiRecipe == null) {
            lifecycleScope.launch{
                recipe = recipeDetailsViewModel.getRecipeInfo()
                displayRecipeInfo(recipe!!)
                cookedrecipe = CookedRecipe(
                    recipe!!.id,
                    recipe!!.title,
                    recipe!!.image,
                    recipe!!.readyInMinutes,
                    recipe!!.servings,
                    recipe!!.healthScore,
                    createdAt = System.currentTimeMillis()
                )
            }
        }
        else{
            displayAiRecipeInfo(aiRecipe!!)
            cookedrecipe = CookedRecipe(
                aiRecipe.id,
                aiRecipe.title,
                aiRecipe.image,
                aiRecipe.readyInMinutes,aiRecipe.servings,2 , createdAt = System.currentTimeMillis())
        }

        binding.stepsButton.setOnClickListener {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToRecipeStepsFragment(cookedrecipe!!.id , cookedrecipe)
            findNavController().navigate(action)
        }

        binding.aiOpinionButton.setOnClickListener {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToChatBotServiceFragment(null , recipe, 2)
            findNavController().navigate(action)
        }
    }

    private fun displayRecipeInfo(recipe: DetailedRecipeResponse) {
        Glide.with(binding.root)
            .load(recipe.image)
            .into(binding.recipeImage)
    }

    private fun displayAiRecipeInfo(recipe: AiRecipe) {
        Glide.with(binding.root)
            .load(recipe.image)
            .into(binding.recipeImage)
    }
}