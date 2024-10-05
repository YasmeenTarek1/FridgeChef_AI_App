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

    private var tts: TTS? = null // Create a variable to hold the TTS instance
    private var steps: List<Step> = emptyList()
    private var idx = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecipeStepsBinding.bind(view)
        binding.lifecycleOwner = this

        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val recipeId: Int = args.recipeID
        val factory = RecipeStepsViewModelFactory(recipeId, repository)
        viewModel = ViewModelProvider(this, factory).get(RecipeStepsViewModel::class.java)

        lifecycleScope.launch {
            steps = viewModel.getSteps()
            showStep() // Display the first step
        }

        binding.nextButton.setOnClickListener {
            if (idx < steps.size) {
                showStep()
            } else {
                viewModel.addToCookedRecipes(args.cookedRecipe)
                binding.stepText.text = "Congrats, Recipe is Finished"
                stopAndSpeak(binding.stepText.text.toString()) // TTS for final message
                binding.nextButton.text = "Return to Feed"
                binding.nextButton.setOnClickListener {
                    findNavController().navigate(RecipeStepsFragmentDirections.actionRecipeStepsFragmentToFeedFragment())
                }
            }
        }
    }

    private fun showStep() {
        // Stop any ongoing TTS playback
        tts?.stopTTS()

        binding.stepText.text = steps[idx++].step
        stopAndSpeak(binding.stepText.text.toString())
    }

    private fun stopAndSpeak(text: String) {
        // Stop the current TTS instance and create a new one
        tts?.stopTTS()
        tts = TTS(requireActivity(), text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.release() // Ensure that TTS is released when the fragment is destroyed
    }
}
