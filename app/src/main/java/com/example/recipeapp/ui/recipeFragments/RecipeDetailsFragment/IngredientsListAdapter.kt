package com.example.recipeapp.ui.recipeFragments.RecipeDetailsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.databinding.ItemIngredientSmallBinding
import io.github.rexmtorres.android.swipereveallayout.ViewBinderHelper

class IngredientsListAdapter(private val onAddToCartClick: (Ingredient) -> Unit) : RecyclerView.Adapter<IngredientsListAdapter.IngredientsListAdapterViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Ingredient>() {
        override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsListAdapterViewHolder {
        val binding = ItemIngredientSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        viewBinderHelper.setOpenOnlyOne(true) // Ensure only one swipe action is open at a time
        return IngredientsListAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: IngredientsListAdapterViewHolder, position: Int) {
        val ingredient = differ.currentList[position]

        val binding = holder.itemBinding
        binding.ingredient = ingredient

        // Bind the swipe layout to ensure that each item has a unique identifier for swipe actions
        viewBinderHelper.bind(binding.swipeLayout, ingredient.name)

        binding.addToCartButton.setOnClickListener {
            onAddToCartClick(ingredient)
            viewBinderHelper.closeLayout(ingredient.name)
        }

        ingredient.image = "https://img.spoonacular.com/ingredients_100x100/"+ingredient.image

        Glide.with(binding.root)
            .load(ingredient.image)
            .into(binding.ingredientImage)
    }

    inner class IngredientsListAdapterViewHolder(val itemBinding: ItemIngredientSmallBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
