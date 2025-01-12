package com.example.fridgeChefAIApp.ui.toBuyIngredientsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fridgeChefAIApp.databinding.ItemToBuyIngredientBinding
import com.example.fridgeChefAIApp.room_DB.model.ToBuyIngredient
import io.github.rexmtorres.android.swipereveallayout.ViewBinderHelper


class ToBuyIngredientsAdapter(private val onDeleteClick: (ToBuyIngredient) -> Unit) : RecyclerView.Adapter<ToBuyIngredientsAdapter.toBuyIngredientViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ToBuyIngredient>() {
        override fun areItemsTheSame(oldItem: ToBuyIngredient, newItem: ToBuyIngredient): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ToBuyIngredient, newItem: ToBuyIngredient): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): toBuyIngredientViewHolder {
        val binding = ItemToBuyIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        viewBinderHelper.setOpenOnlyOne(true) // Ensure only one swipe action is open at a time
        return toBuyIngredientViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: toBuyIngredientViewHolder, position: Int) {
        val ingredient = differ.currentList[position]
        val binding = holder.itemBinding

        binding.ingredient = ingredient

        // Bind the swipe layout to ensure that each item has a unique identifier for swipe actions
        viewBinderHelper.bind(binding.swipeLayout, ingredient.name)

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
