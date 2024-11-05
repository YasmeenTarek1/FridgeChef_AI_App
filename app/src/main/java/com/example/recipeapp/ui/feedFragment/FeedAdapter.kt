package com.example.recipeapp.ui.feedFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemFeedBinding

class FeedAdapter (private val checkFavorite: (Int) -> Boolean, private val onLoveClick: (Recipe) -> Unit , private val onDislikeClick: (Recipe) -> Unit) : PagingDataAdapter<Recipe, FeedAdapter.RecipeViewHolder>(RECIPE_COMPARATOR) {


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
        return RecipeViewHolder(ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false) )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        val binding = holder.itemBinding

        binding.love.setOnClickListener{
            binding.fullLove.visibility = View.VISIBLE
            binding.love.visibility = View.INVISIBLE
            onLoveClick(recipe!!)
        }
        binding.fullLove.setOnClickListener{
            binding.love.visibility = View.VISIBLE
            binding.fullLove.visibility = View.INVISIBLE
            onDislikeClick(recipe!!)
        }

        binding.recipe = recipe
        binding.executePendingBindings()

        Glide.with(binding.root)
            .load(recipe!!.image)
            .into(binding.recipeImage)


        if(checkFavorite(recipe.id)) {
            binding.fullLove.visibility = View.VISIBLE
            binding.love.visibility = View.INVISIBLE
        }
        else{
            binding.fullLove.visibility = View.INVISIBLE
            binding.love.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener { view ->
            val action = FeedFragmentDirections.actionFeedFragmentToRecipeDetailsFragment(recipe)
            view.findNavController().navigate(action)
        }


    }

    inner class RecipeViewHolder(val itemBinding: ItemFeedBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
