package com.example.fridgeChefAIApp.ui.recipeFragments.recipeDetailsFragment

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.api.model.ExtraDetailsResponse
import com.example.fridgeChefAIApp.api.model.Ingredient
import com.example.fridgeChefAIApp.api.model.Recipe
import com.example.fridgeChefAIApp.databinding.DialogRecipeOpinionBinding
import com.example.fridgeChefAIApp.databinding.FragmentRecipeDetailsBinding
import com.example.fridgeChefAIApp.ui.chatBotServiceFragment.ChatBotServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private val viewModel: RecipeDetailsViewModel by viewModels()
    private val args: RecipeDetailsFragmentArgs by navArgs()

    private var tooltipWindow: PopupWindow? = null
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        tooltipWindow?.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRecipeDetailsBinding.bind(view)

        ViewCompat.setTooltipText(binding.aiOpinionButton, "Take a Cooking Tip")

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                // Show tooltip automatically when the fragment is first displayed
                showCustomTooltip()
            }
        }, 100)

        val currentRecipe = args.recipe
        val classification = args.classification

        if(currentRecipe.wellWrittenSummary == 0) {
            binding.descriptionLoadingProgressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val detailedRecipe: ExtraDetailsResponse =
                        viewModel.getRecipeInfo(currentRecipe.id)

                    if (currentRecipe.servings == 0)
                        currentRecipe.servings = detailedRecipe.servings
                    if (currentRecipe.readyInMinutes == 0)
                        currentRecipe.readyInMinutes = detailedRecipe.readyInMinutes

                    currentRecipe.summary = viewModel.summarizeSummary(detailedRecipe.summary)
                }

                withContext(Dispatchers.Main) {
                    binding.summaryTV.text = currentRecipe.summary
                    if (currentRecipe.servings in 2..9)
                        binding.servings.text = "${currentRecipe.servings} plates"
                    else
                        binding.servings.text = "${currentRecipe.servings} plate"

                    binding.readyInMinutes.text = "${currentRecipe.readyInMinutes} mins"

                    binding.descriptionLoadingProgressBar.visibility = View.GONE
                }

                withContext(Dispatchers.IO){
                    val isFav = viewModel.checkFavorite(currentRecipe.id).first()
                    if (isFav)
                        viewModel.updateFavRecipe(currentRecipe)
                }
            }
        }
        else{
            binding.summaryTV.text = currentRecipe.summary
            if (currentRecipe.servings in 2..9)
                binding.servings.text = "${currentRecipe.servings} plates"
            else
                binding.servings.text = "${currentRecipe.servings} plate"

            binding.readyInMinutes.text = "${currentRecipe.readyInMinutes} mins"
        }

        val adapter = IngredientsListAdapter(onAddToCartClick = { ingredient ->
            viewModel.onAddToCartClick(ingredient)
        } )

        val recyclerView = binding.ingredientsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            displayRecipeImage(currentRecipe)
            binding.recipe = currentRecipe
            Log.d("Recipe" , currentRecipe.toString())
        }

        if(adapter.itemCount == 0)
            binding.ingredientsLoadingProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            if(classification == 0){
                val ingredientsResponse = viewModel.getAiRecipeIngredients(currentRecipe.id)
                val ingredientStrings = ingredientsResponse.split(",")
                val ingredientsList = mutableListOf<Ingredient>()

                ingredientStrings.forEach{ ingredientName ->
                    val trimmedName = ingredientName.trim(' ', '"')
                    ingredientsList.add(Ingredient(name = trimmedName , image = viewModel.getAiIngredientImage(trimmedName)))
                }

                adapter.differ.submitList(ingredientsList)
                binding.ingredientsLoadingProgressBar.visibility = View.GONE
            }
            else {
                val ingredients = viewModel.getRecipeIngredients(currentRecipe.id)
                ingredients.forEach{ ingredient ->
                    ingredient.image = "https://img.spoonacular.com/ingredients_100x100/"+ ingredient.image
                }
                adapter.differ.submitList(ingredients)
                binding.ingredientsLoadingProgressBar.visibility = View.GONE
            }
        }

        binding.stepsButton.setOnClickListener {
            val action = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToRecipeStepsFragment(currentRecipe , classification)
            findNavController().navigate(action)
        }

        binding.aiOpinionButton.setOnClickListener {
            showRecipeOpinionDialog(currentRecipe)
        }

        lifecycleScope.launch {
            viewModel.checkCooked(currentRecipe.id)
                .combine(viewModel.checkFavorite(currentRecipe.id)) { isCooked, isFavorite ->
                    Pair(isCooked, isFavorite)
                }
                .collectLatest { (isCooked, isFavorite) ->
                    if (isCooked) {
                        binding.fullLove.visibility = View.INVISIBLE
                        binding.love.visibility = View.INVISIBLE
                        binding.cookedRecipe.visibility = View.VISIBLE
                    } else {
                        binding.cookedRecipe.visibility = View.INVISIBLE
                        if (isFavorite) {
                            binding.fullLove.visibility = View.VISIBLE
                            binding.love.visibility = View.INVISIBLE
                        } else {
                            binding.fullLove.visibility = View.INVISIBLE
                            binding.love.visibility = View.VISIBLE
                        }
                    }
                }
        }

        binding.love.setOnClickListener{
            binding.fullLove.visibility = View.VISIBLE
            binding.love.visibility = View.INVISIBLE
            viewModel.onLoveClick(currentRecipe)
        }

        binding.fullLove.setOnClickListener{
            binding.love.visibility = View.VISIBLE
            binding.fullLove.visibility = View.INVISIBLE
            viewModel.onDislikeClick(currentRecipe)
        }
    }

    private fun displayRecipeImage(recipe: Recipe) {
        Glide.with(binding.root)
            .load(recipe.image)
            .error(R.drawable.dish)
            .into(binding.recipeImage)
    }

    private fun showRecipeOpinionDialog(recipe: Recipe) {
        val chatBotServiceViewModel: ChatBotServiceViewModel by viewModels()

        val dialogViewBinding = DialogRecipeOpinionBinding.inflate(LayoutInflater.from(requireContext()))

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogViewBinding.root)
            .create()
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBuilder.setOnShowListener {
            dialogBuilder.window?.decorView?.translationX = 4f // Center X
        }
        dialogBuilder.show()

        // Set a delayed task to hide the animation after 10 seconds
        val animationDelayMillis = 10000L
        dialogViewBinding.lottieAnimationView.postDelayed({
            dialogViewBinding.lottieAnimationView.visibility = View.GONE
        }, animationDelayMillis)


        dialogViewBinding.lottieAnimationView.visibility = View.VISIBLE
        dialogViewBinding.closeButton.visibility = View.GONE
        dialogViewBinding.opinionTV.visibility = View.GONE

        lifecycleScope.launch {
            val recipeOpinion = chatBotServiceViewModel.getRecipeOpinion(recipe)

            delay(animationDelayMillis)

            val markwon = Markwon.create(requireContext())
            markwon.setMarkdown(dialogViewBinding.opinionTV , recipeOpinion)

            dialogViewBinding.opinionTV.visibility = View.VISIBLE
            dialogViewBinding.lottieAnimationView.visibility = View.GONE
            dialogViewBinding.closeButton.visibility = View.VISIBLE
        }

        dialogViewBinding.closeButton.setOnClickListener {
            dialogBuilder.dismiss()
        }
    }

    private fun showCustomTooltip() {
        // Dismiss any existing tooltip if present
        tooltipWindow?.dismiss()

        val inflater = LayoutInflater.from(binding.aiOpinionButton.context)
        val tooltipView = inflater.inflate(R.layout.tooltip, null)
        val tooltipText = tooltipView.findViewById<TextView>(R.id.tooltip_text)
        tooltipText.text = "Will this recipe work for you?\n                 Let’s find out!"

        tooltipWindow = PopupWindow(tooltipView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        tooltipWindow?.isOutsideTouchable = true
        tooltipWindow?.isFocusable = true

        // Get the location of the view on screen
        val location = IntArray(2)
        binding.aiOpinionButton.getLocationOnScreen(location)

        val xOffset = -415 // Horizontal offset
        tooltipWindow?.showAtLocation(view, 0, location[0] + xOffset, location[1] + binding.aiOpinionButton.height)

        // Schedule dismissal of the tooltip
        handler.postDelayed(runnable, 4000)
    }

}