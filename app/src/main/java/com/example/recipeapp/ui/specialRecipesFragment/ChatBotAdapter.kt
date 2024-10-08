package com.example.recipeapp.ui.specialRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.databinding.ChatBotRecipeOuterItemBinding
import com.example.recipeapp.room_DB.model.AiRecipe

class ChatBotAdapter : RecyclerView.Adapter<ChatBotAdapter.ChatBotRecipeViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<AiRecipe>() {
        override fun areItemsTheSame(oldItem: AiRecipe, newItem: AiRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AiRecipe, newItem: AiRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatBotRecipeViewHolder {
        return ChatBotRecipeViewHolder(ChatBotRecipeOuterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChatBotRecipeViewHolder, position: Int) {
        val aiRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(aiRecipe.image)
            .into(binding.recipeImage)

        binding.textView.text = aiRecipe.title

        holder.itemView.setOnClickListener { view ->
            val action = SpecialRecipesFragmentDirections.actionSpecialRecipesFragmentToRecipeDetailsFragment(0 , aiRecipe)
            view.findNavController().navigate(action)
        }
    }

    inner class ChatBotRecipeViewHolder(val itemBinding: ChatBotRecipeOuterItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
