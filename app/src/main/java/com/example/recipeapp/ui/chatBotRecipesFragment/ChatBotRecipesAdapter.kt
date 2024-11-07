package com.example.recipeapp.ui.chatBotRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemAiRecipeBinding
import com.example.recipeapp.room_DB.model.AiRecipe
import io.github.rexmtorres.android.swipereveallayout.ViewBinderHelper
import kotlinx.coroutines.runBlocking

class ChatBotRecipesAdapter(private val viewModel: ChatBotRecipesViewModel): RecyclerView.Adapter<ChatBotRecipesAdapter.ChatBotViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<AiRecipe>() {
        override fun areItemsTheSame(oldItem: AiRecipe, newItem: AiRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AiRecipe, newItem: AiRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBotViewHolder {
        viewBinderHelper.setOpenOnlyOne(true) // Ensure only one swipe action is open at a time
        return ChatBotViewHolder(ItemAiRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChatBotViewHolder, position: Int) {
        val aiRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        // Bind the swipe layout to ensure that each item has a unique identifier for swipe actions
        viewBinderHelper.bind(binding.swipeLayout, aiRecipe.id.toString())

        Glide.with(binding.root)
            .load(aiRecipe.image)
            .into(binding.recipeImage)

        binding.recipe = aiRecipe

        binding.delete.setOnClickListener {
            runBlocking {
                viewModel.deleteRecipe(aiRecipe)
            }
            removeItem(position)
        }


        holder.itemView.setOnClickListener { view ->
            val recipe = Recipe(
                id = aiRecipe.id,
                title = aiRecipe.title,
                image = aiRecipe.image,
                readyInMinutes = aiRecipe.readyInMinutes,
                servings = aiRecipe.servings,
                summary = aiRecipe.summary
            )
            val action = ChatBotRecipesFragmentDirections.actionChatBotRecipesFragmentToRecipeDetailsFragment(recipe)
            view.findNavController().navigate(action)
        }
    }

    private fun removeItem(position: Int) {
        val currentList = differ.currentList.toMutableList()
        currentList.removeAt(position)
        differ.submitList(currentList)
    }

    inner class ChatBotViewHolder(val itemBinding: ItemAiRecipeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
