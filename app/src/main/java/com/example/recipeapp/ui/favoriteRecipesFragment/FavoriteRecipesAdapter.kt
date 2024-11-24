package com.example.recipeapp.ui.favoriteRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemFavoriteRecipeBinding
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import io.github.rexmtorres.android.swipereveallayout.ViewBinderHelper

class FavoriteRecipesAdapter(private val onDeleteClick: (FavoriteRecipe) -> Unit) :
    RecyclerView.Adapter<FavoriteRecipesAdapter.FavoriteRecipeViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<FavoriteRecipe>() {
        override fun areItemsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipeViewHolder {
        val binding = ItemFavoriteRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        viewBinderHelper.setOpenOnlyOne(true) // Ensure only one swipe action is open at a time
        return FavoriteRecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavoriteRecipeViewHolder, position: Int) {
        val favRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        // Close any open swipe layouts before binding (avoids animations)
        viewBinderHelper.closeLayout(favRecipe.id.toString())

        // Bind the swipe layout to ensure that each item has a unique identifier for swipe actions
        viewBinderHelper.bind(binding.swipeLayout, favRecipe.id.toString())

        if(favRecipe.image != null) {
            binding.recipeImage.scaleX = 1.0f
            binding.recipeImage.scaleY = 1.0f
            Glide.with(binding.root)
                .load(favRecipe.image)
                .into(binding.recipeImage)
        }
        else{
            binding.recipeImage.scaleX = 0.75f
            binding.recipeImage.scaleY = 0.75f
            Glide.with(binding.root)
                .load(R.drawable.dish) // Fallback image in case of an error
                .into(binding.recipeImage)
        }

        binding.recipe = favRecipe

        binding.delete.setOnClickListener {
            onDeleteClick(favRecipe)
            removeItem(position)
        }

        holder.itemView.setOnClickListener { view ->
            val recipe = Recipe(
                id = favRecipe.id,
                title = favRecipe.title,
                image = favRecipe.image,
                readyInMinutes = favRecipe.readyInMinutes,
                servings = favRecipe.servings,
                summary = favRecipe.summary
            )
            val action = FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToRecipeDetailsFragment(recipe , 1)
            view.findNavController().navigate(action)
        }
    }

    private fun removeItem(position: Int) {
        val currentList = differ.currentList.toMutableList()
        currentList.removeAt(position)
        differ.submitList(currentList)
    }

    inner class FavoriteRecipeViewHolder(val itemBinding: ItemFavoriteRecipeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
