package com.example.recipeapp.ui.chatBotFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.DetailedRecipeResponse
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentChatBotBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch

class ChatBotFragment : Fragment(R.layout.fragment_chat_bot) {
    private lateinit var binding: FragmentChatBotBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: ChatBotViewModel
    private val args: ChatBotFragmentArgs by navArgs()
    private var classification:Int? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentChatBotBinding.bind(view)
        binding.lifecycleOwner = this

        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = ChatBotViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ChatBotViewModel::class.java)

        classification = args.classification

        when(classification){
            0 -> {
                showRecipe(args.ingredients)
            }
            1 -> {
                binding.enteredIngredientsButton.visibility = View.INVISIBLE
                binding.otherIngredientsButton.visibility = View.INVISIBLE
                binding.scrollView3.visibility = View.VISIBLE
                binding.chatbotResponse.text = "Go Crazy Bot: Thinking..."

                showCookingTip()
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
            val recipe:String = viewModel.getCrazyRecipe(ingredients!!)

            binding.ingredientsInput.text.clear()
            binding.ingredientsInput.clearFocus()

            val markwon = Markwon.create(requireContext())
            markwon.setMarkdown(binding.chatbotResponse, recipe)

            binding.goCrazyButton.visibility = View.INVISIBLE
            binding.ingredientsInput.visibility = View.INVISIBLE
        }
    }

    private fun showCookingTip() {
        lifecycleScope.launch {
            val recipe:String = viewModel.getCookingTip()
            val markwon = Markwon.create(requireContext())
            markwon.setMarkdown(binding.chatbotResponse, recipe)
        }
    }


    private fun showRecipeOpinion(recipe: DetailedRecipeResponse) {
        lifecycleScope.launch {
            val recipe:String = viewModel.getRecipeOpinion(recipe)

            val markwon = Markwon.create(requireContext())
            markwon.setMarkdown(binding.chatbotResponse, recipe)
        }
    }
}