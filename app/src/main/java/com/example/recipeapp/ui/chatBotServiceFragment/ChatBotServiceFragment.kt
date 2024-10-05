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
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentChatBotBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.room_DB.model.AiRecipe
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch

class ChatBotServiceFragment : Fragment(R.layout.fragment_chat_bot) {
    private lateinit var binding: FragmentChatBotBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: ChatBotServiceViewModel
    private val args: ChatBotServiceFragmentArgs by navArgs()
    private var classification:Int? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentChatBotBinding.bind(view)
        binding.lifecycleOwner = this

        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = ChatBotServiceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ChatBotServiceViewModel::class.java)

        classification = args.classification

        when(classification){
            0 -> {
                showRecipe(args.ingredients)
            }
            2 ->{
                binding.enteredIngredientsButton.visibility = View.INVISIBLE
                binding.otherIngredientsButton.visibility = View.INVISIBLE
                binding.scrollView3.visibility = View.VISIBLE
                binding.chatbotResponse.text = "Go Crazy Bot: Thinking..."

                showRecipeOpinion(args.recipe!!)
            }
        }

    }

    private fun showRecipe(ingredients: String?) {
        if(ingredients == null){
            useNewIngredients()
        }
        else{ // i have the 2 options
            binding.otherIngredientsButton.setOnClickListener {
                useNewIngredients()
            }
            binding.enteredIngredientsButton.setOnClickListener {
                binding.otherIngredientsButton.visibility = View.INVISIBLE
                binding.enteredIngredientsButton.visibility = View.INVISIBLE

                displayRecipe(ingredients)
            }
        }
    }

    private fun useNewIngredients() {
        binding.enteredIngredientsButton.visibility = View.INVISIBLE
        binding.otherIngredientsButton.visibility = View.INVISIBLE

        binding.scrollView3.visibility = View.VISIBLE
        binding.ingredientsInput.visibility = View.VISIBLE
        binding.goCrazyButton.visibility = View.VISIBLE

        binding.goCrazyButton.setOnClickListener {
            displayRecipe(binding.ingredientsInput.text.toString())
        }
    }

    private fun displayRecipe(ingredients: String?){
        binding.scrollView3.visibility = View.VISIBLE
        binding.chatbotResponse.text = "Go Crazy Bot: Thinking..."

        lifecycleScope.launch {
            val recipe:AiRecipe = viewModel.getCrazyRecipe(ingredients!!)
            val action = ChatBotServiceFragmentDirections.actionChatBotServiceFragmentToRecipeDetailsFragment(0 , recipe)
            findNavController().navigate(action)
        }
    }

    private fun showRecipeOpinion(recipe: DetailedRecipeResponse) {
        lifecycleScope.launch {
            val recipe:String = viewModel.getRecipeOpinion(recipe)
            binding.chatbotResponse.text = recipe
        }
    }
}