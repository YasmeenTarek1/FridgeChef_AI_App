package com.example.recipeapp.ui.chatBotServiceFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentChatBotBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.sharedPreferences.SharedPreferences
import kotlinx.coroutines.launch

class ChatBotServiceFragment : Fragment(R.layout.fragment_chat_bot) {
    private lateinit var binding: FragmentChatBotBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: ChatBotServiceViewModel
    private val args: ChatBotServiceFragmentArgs by navArgs()
    private var classification:Int? = null // 0 -> Suggest a new Recipe      1 -> Give an Opinion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentChatBotBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = ChatBotServiceViewModelFactory(repository , SharedPreferences(requireContext()))
        viewModel = ViewModelProvider(this, factory).get(ChatBotServiceViewModel::class.java)

        classification = args.classification

        when(classification){
            0 -> {
                displayRecipe(args.ingredients)
            }
            1 -> {
                showRecipeOpinion(args.recipe!!)
            }
        }
    }

    private fun displayRecipe(ingredients: String?){
        binding.lottieAnimationView.visibility = View.VISIBLE

        lifecycleScope.launch {
            val aiRecipe = viewModel.getCrazyRecipe(ingredients!!)
            val recipe = Recipe(
                id = aiRecipe.id,
                title = aiRecipe.title,
                image = aiRecipe.image,
                readyInMinutes = aiRecipe.readyInMinutes,
                servings = aiRecipe.servings,
                summary = aiRecipe.summary
            )
            val action = ChatBotServiceFragmentDirections.actionChatBotServiceFragmentToRecipeDetailsFragment(recipe)
            findNavController().navigate(action)
            viewModel.updateFirestore()
        }
    }

    private fun showRecipeOpinion(recipe: Recipe) {
        binding.scrollView3.visibility = View.VISIBLE
        binding.lottieAnimationView.visibility = View.VISIBLE

        lifecycleScope.launch {
            val recipeOpinion = viewModel.getRecipeOpinion(recipe)
            binding.chatbotResponse.text = recipeOpinion
        }
    }
}