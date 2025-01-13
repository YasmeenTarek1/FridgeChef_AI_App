package com.example.fridgeChefAIApp.ui.chatBotRecipesFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentChatBotRecipesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatBotRecipesFragment : Fragment(R.layout.fragment_chat_bot_recipes) {

    private lateinit var binding: FragmentChatBotRecipesBinding
    private val viewModel: ChatBotRecipesViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatBotRecipesAdapter: ChatBotRecipesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatBotRecipesBinding.bind(view)

        chatBotRecipesAdapter = ChatBotRecipesAdapter(
            onDeleteClick = { recipe ->
            viewModel.deleteRecipe(recipe)
        })

        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatBotRecipesAdapter

        lifecycleScope.launch {
            viewModel.recipes.collect { recipes ->
                chatBotRecipesAdapter.differ.submitList(recipes)
                if (chatBotRecipesAdapter.itemCount == 0) {
                    binding.emptyImage.visibility = View.VISIBLE
                } else {
                    binding.emptyImage.visibility = View.GONE
                }
            }
        }
    }
}