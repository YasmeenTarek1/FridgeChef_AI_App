package com.example.recipeapp.ui.chatBotRecipesFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentChatBotRecipesBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch

class ChatBotRecipesFragment : Fragment(R.layout.fragment_chat_bot_recipes) {

    private lateinit var binding: FragmentChatBotRecipesBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: ChatBotRecipesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatBotRecipesAdapter: ChatBotRecipesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChatBotRecipesBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = ChatBotRecipesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ChatBotRecipesViewModel::class.java)

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