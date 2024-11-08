package com.example.recipeapp.ui.toBuyIngredientsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.databinding.ItemToBuyIngredientBinding
import com.example.recipeapp.room_DB.model.ToBuyIngredient


class ToBuyIngredientsAdapter(private val onDeleteClick: (ToBuyIngredient) -> Unit) : RecyclerView.Adapter<ToBuyIngredientsAdapter.toBuyIngredientViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ToBuyIngredient>() {
        override fun areItemsTheSame(oldItem: ToBuyIngredient, newItem: ToBuyIngredient): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ToBuyIngredient, newItem: ToBuyIngredient): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): toBuyIngredientViewHolder {
        return toBuyIngredientViewHolder(ItemToBuyIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: toBuyIngredientViewHolder, position: Int) {
        val ingredient = differ.currentList[position]
        val binding = holder.itemBinding

        binding.ingredient = ingredient

        binding.delete.setOnClickListener {
            onDeleteClick(ingredient)
            removeItem(position)
        }

        Glide.with(binding.root)
            .load(ingredient.image)
            .into(binding.ingredientImage)

    }

    private fun removeItem(position: Int) {
        val currentList = differ.currentList.toMutableList()
        currentList.removeAt(position)
        differ.submitList(currentList)
    }


    inner class toBuyIngredientViewHolder(val itemBinding: ItemToBuyIngredientBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
