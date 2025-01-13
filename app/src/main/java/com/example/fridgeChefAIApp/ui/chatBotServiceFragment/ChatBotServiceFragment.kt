package com.example.fridgeChefAIApp.ui.chatBotServiceFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentChatBotBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatBotServiceFragment : Fragment(R.layout.fragment_chat_bot) {
    private lateinit var binding: FragmentChatBotBinding
    private val viewModel: ChatBotServiceViewModel by viewModels()
    private val args: ChatBotServiceFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentChatBotBinding.bind(view)
        displayRecipe(args.ingredients)
    }

    private fun displayRecipe(ingredients: String?){
        binding.lottieAnimationView.visibility = View.VISIBLE

        lifecycleScope.launch {
            val recipe = viewModel.getCrazyRecipe(ingredients!!)
            val action = ChatBotServiceFragmentDirections.actionChatBotServiceFragmentToRecipeDetailsFragment(recipe , 0)
            findNavController().navigate(action)
        }
    }
}