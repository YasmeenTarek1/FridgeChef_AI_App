package com.example.recipeapp.ui.specialRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemChatBotRecipeOuterBinding
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
        return ChatBotRecipeViewHolder(ItemChatBotRecipeOuterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChatBotRecipeViewHolder, position: Int) {
        val aiRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(aiRecipe.image)
            .error(R.drawable.dish_smaller) // Fallback image in case of an error
            .into(binding.recipeImage)

        binding.textView.text = aiRecipe.title

        holder.itemView.setOnClickListener { view ->
            val recipe = Recipe(
                id = aiRecipe.id,
                title = aiRecipe.title,
                image = aiRecipe.image,
                readyInMinutes = aiRecipe.readyInMinutes,
                servings = aiRecipe.servings,
                summary = aiRecipe.summary
            )
            val action = SpecialRecipesFragmentDirections.actionSpecialRecipesFragmentToRecipeDetailsFragment(recipe , 0)
            view.findNavController().navigate(action)
        }
    }

    inner class ChatBotRecipeViewHolder(val itemBinding: ItemChatBotRecipeOuterBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
