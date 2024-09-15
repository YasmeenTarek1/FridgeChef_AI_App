package com.example.recipeapp.ui.recipeFragments.RecipeStepsFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Step
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentRecipeStepsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch

class RecipeStepsFragment : Fragment(R.layout.fragment_recipe_steps) {
    private lateinit var binding: FragmentRecipeStepsBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: RecipeStepsViewModel
    private val args: RecipeStepsFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecipeStepsBinding.bind(view)
        binding.lifecycleOwner = this

        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val recipeId: Int = args.recipeID

        val factory = RecipeStepsViewModelFactory(recipeId, repository)
        viewModel = ViewModelProvider(this, factory).get(RecipeStepsViewModel::class.java)


        val db = AppDatabase.getInstance(requireContext())
        var steps: List<Step> = emptyList()
        var idx = 0

        lifecycleScope.launch {
            steps = viewModel.getSteps()
            binding.stepText.text = steps[idx++].step
            TTS(requireActivity(), binding.stepText.text.toString())
        }

        binding.nextButton.setOnClickListener {
            if (idx < steps.size) {
                binding.stepText.text = steps[idx++].step
                TTS(requireActivity(), binding.stepText.text.toString())
            } else {
                viewModel.addToCookedRecipes(args.cookedRecipe, db)

                binding.stepText.text = "Congrats, Recipe is Finished"
                TTS(requireActivity(), binding.stepText.text.toString())
                binding.nextButton.text = "Return to Feed"
                binding.nextButton.setOnClickListener {
                    findNavController().navigate(RecipeStepsFragmentDirections.actionRecipeStepsFragmentToFeedFragment())
                }
            }
        }
    }

}