package com.example.fridgeChefAIApp.ui.recipeFragments.recipeStepsFragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.model.Recipe
import com.example.fridgeChefAIApp.api.model.Step
import com.example.fridgeChefAIApp.api.service.RetrofitInstance
import com.example.fridgeChefAIApp.databinding.DialogRecipeFinishedBinding
import com.example.fridgeChefAIApp.databinding.FragmentRecipeStepsBinding
import com.example.fridgeChefAIApp.room_DB.database.AppDatabase
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecipeStepsFragment : Fragment(R.layout.fragment_recipe_steps) {

    private lateinit var binding: FragmentRecipeStepsBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: RecipeStepsViewModel
    private val args: RecipeStepsFragmentArgs by navArgs()

    private var tts: TTS? = null // TTS instance
    private var steps: List<Step> = emptyList()
    private var numOfSteps: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecipeStepsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val recipe = args.recipe
        val classification = args.classification

        val factory = RecipeStepsViewModelFactory(recipe.id, repository)
        viewModel = ViewModelProvider(this, factory).get(RecipeStepsViewModel::class.java)

        var idx = 0

        // First step

        lifecycleScope.launch {
            if(classification == 0){
                val stepsResponse = viewModel.getAiRecipeSteps(recipe.id)
                val stepsList = stepsResponse.split("\", \"")
                    .mapIndexed { index, stepText ->
                        Step(
                            number = index + 1,
                            step = stepText.replace("\"", "").replace(".", "").trim()
                        )
                    }

                steps = stepsList
                numOfSteps = stepsList.size
                displayStep(recipe, idx)
            }
            else {
                steps = viewModel.getSteps()
                numOfSteps = steps.size
                displayStep(recipe, idx)
            }
        }


        binding.nextButton.setOnClickListener {
            displayStep(recipe, ++idx)
        }
        binding.nextStepTV.setOnClickListener{
            displayStep(recipe, ++idx)
        }
        binding.previousButton.setOnClickListener {
            displayStep(recipe, --idx)
        }
        binding.previousStepTV.setOnClickListener{
            displayStep(recipe, --idx)
        }
    }

    private fun displayStep(recipe: Recipe, index: Int){
        if (index < steps.size) {
            showStep(index)
            changeProgress((((index+1.0) / numOfSteps) * 100).toInt())
            binding.stepsNumberTV.text = "${index+1} / $numOfSteps"
        }
        else if(index == steps.size){
            // Recipe is Finished
            lifecycleScope.launch {
                stopAndSpeak("Good job, ${viewModel.getUserName()}")
                showFinishDialog()

                val cookedRecipe = CookedRecipe(
                    id = recipe.id,
                    title = recipe.title,
                    image = recipe.image!!,
                    readyInMinutes = recipe.readyInMinutes,
                    servings = recipe.servings,
                    summary = recipe.summary,
                    createdAt = System.currentTimeMillis()
                )
                viewModel.addToCookedRecipes(cookedRecipe)
            }

        }
    }

    private fun showStep(index: Int) {
        // Stop any ongoing TTS playback
        tts?.stopTTS()

        binding.stepText.text = steps[index].step
        binding.stepNumber.text = "Step ${index+1}:"
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

    private fun changeProgress(targetValue: Int) {
        Log.d("progress" , targetValue.toString())
        lifecycleScope.launch{

            val currentProgress = binding.progressBar.progress

            if (targetValue > currentProgress) {
                // Increasing progress
                for (i in currentProgress..targetValue) {
                    delay(30)
                    binding.progressBar.progress = i
                }
            } else {
                // Decreasing progress
                for (i in currentProgress downTo targetValue) {
                    delay(30)
                    binding.progressBar.progress = i
                }
            }

        }
    }

    private fun showFinishDialog() {
        val dialogViewBinding = DialogRecipeFinishedBinding.inflate(LayoutInflater.from(requireContext()))

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogViewBinding.root)
            .create()
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBuilder.setOnShowListener {
            dialogBuilder.window?.decorView?.translationX = 70f // Center X
        }
        dialogBuilder.show()

        lifecycleScope.launch {
            dialogViewBinding.nameTV.text = "${viewModel.getUserName()}"
        }

        dialogViewBinding.returnToFeedButton.setOnClickListener {
            dialogBuilder.dismiss()
            findNavController().navigate(RecipeStepsFragmentDirections.actionRecipeStepsFragmentToFeedFragment())
        }
    }
}
