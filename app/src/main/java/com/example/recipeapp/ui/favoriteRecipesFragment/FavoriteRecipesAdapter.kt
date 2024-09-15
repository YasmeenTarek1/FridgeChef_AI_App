package com.example.recipeapp.ui.favoriteRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.room_DB.model.FavoriteRecipe
import com.example.recipeapp.databinding.FavoriteRecipeItemBinding


class FavoriteRecipesAdapter : RecyclerView.Adapter<FavoriteRecipesAdapter.FavoriteRecipeViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<FavoriteRecipe>() {
        override fun areItemsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipeViewHolder {
        return FavoriteRecipeViewHolder(FavoriteRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavoriteRecipeViewHolder, position: Int) {
        val favRecipe = differ.currentList[position]

        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(favRecipe.image)
            .into(binding.recipeImage)

        binding.servings.text = "Servings: " + favRecipe.servings.toString()

        holder.itemView.setOnClickListener { view ->
            val action = FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToRecipeDetailsFragment(favRecipe.id)
            view.findNavController().navigate(action)
        }
    }

    inner class FavoriteRecipeViewHolder(val itemBinding: FavoriteRecipeItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
