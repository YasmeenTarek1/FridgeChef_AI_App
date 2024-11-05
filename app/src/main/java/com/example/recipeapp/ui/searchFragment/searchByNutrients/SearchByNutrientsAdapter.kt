package com.example.recipeapp.ui.searchFragment.searchByNutrients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemSearchResultBinding


class SearchByNutrientsAdapter : RecyclerView.Adapter<SearchByNutrientsAdapter.SearchByNutrientsViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByNutrientsViewHolder {
        return SearchByNutrientsViewHolder(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SearchByNutrientsViewHolder, position: Int) {
        val recipe = differ.currentList[position]

        val binding = holder.itemBinding

        binding.recipe = recipe

        Glide.with(binding.root)
            .load(recipe.image)
            .into(binding.recipeImage)

        holder.itemView.setOnClickListener { view ->
            val action = SearchByNutrientsFragmentDirections.actionSearchByNutrientsFragmentToRecipeDetailsFragment(recipe.id , null)
            view.findNavController().navigate(action)
        }
    }

    inner class SearchByNutrientsViewHolder(val itemBinding: ItemSearchResultBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
