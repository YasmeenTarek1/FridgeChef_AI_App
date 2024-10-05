package com.example.recipeapp.ui.chatBotRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.databinding.AiRecipeItemBinding
import com.example.recipeapp.room_DB.model.AiRecipe

class ChatBotRecipesAdapter : RecyclerView.Adapter<ChatBotRecipesAdapter.ChatBotViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<AiRecipe>() {
        override fun areItemsTheSame(oldItem: AiRecipe, newItem: AiRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AiRecipe, newItem: AiRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBotViewHolder {
        return ChatBotViewHolder(AiRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChatBotViewHolder, position: Int) {
        val aiRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(aiRecipe.image)
            .into(binding.recipeImage)

        binding.recipe = aiRecipe

        holder.itemView.setOnClickListener { view ->
            val action = ChatBotRecipesFragmentDirections.actionChatBotRecipesFragmentToRecipeDetailsFragment(aiRecipe.id , null)
            view.findNavController().navigate(action)
        }
    }

    inner class ChatBotViewHolder(val itemBinding: AiRecipeItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
