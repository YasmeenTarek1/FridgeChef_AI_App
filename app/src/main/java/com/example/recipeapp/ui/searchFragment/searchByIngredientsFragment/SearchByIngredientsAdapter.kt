package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.SearchResultItemBinding


class SearchByIngredientsAdapter : RecyclerView.Adapter<SearchByIngredientsAdapter.SearchByIngredientsViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByIngredientsViewHolder {
        return SearchByIngredientsViewHolder(SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SearchByIngredientsViewHolder, position: Int) {
        val recipe = differ.currentList[position]

        val binding = holder.itemBinding

        binding.recipe = recipe

        Glide.with(binding.root)
            .load(recipe.image)
            .into(binding.recipeImage)

        holder.itemView.setOnClickListener { view ->
            val action = SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToRecipeDetailsFragment(recipe.id , null)
            view.findNavController().navigate(action)
        }
    }

    inner class SearchByIngredientsViewHolder(val itemBinding: SearchResultItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
