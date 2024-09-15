package com.example.recipeapp.ui.feedFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.FeedItemBinding

class FeedAdapter (private val onLoveClick: (Recipe) -> Unit) : PagingDataAdapter<Recipe, FeedAdapter.RecipeViewHolder>(RECIPE_COMPARATOR) {

    companion object {
        private val RECIPE_COMPARATOR = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem.id == newItem.id && oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(FeedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)

        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(recipe!!.image)
            .into(binding.recipeImage)

        binding.readyInMinutes.text = "Ready in " + recipe.readyInMinutes.toString()
        binding.servings.text = "Servings: " + recipe.servings.toString()

        holder.itemView.setOnClickListener { view ->
            val action = FeedFragmentDirections.actionFeedFragmentToRecipeDetailsFragment(recipe.id)
            view.findNavController().navigate(action)
        }

        binding.love.setOnClickListener{
            onLoveClick(recipe)
        }
    }

    inner class RecipeViewHolder(val itemBinding: FeedItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
