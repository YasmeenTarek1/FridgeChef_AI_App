package com.example.recipeapp.ui.toBuyIngredientsFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ItemToBuyIngredientBinding
import com.example.recipeapp.room_DB.model.ToBuyIngredient


class ToBuyIngredientsAdapter : RecyclerView.Adapter<ToBuyIngredientsAdapter.toBuyIngredientViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ToBuyIngredient>() {
        override fun areItemsTheSame(oldItem: ToBuyIngredient, newItem: ToBuyIngredient): Boolean {
            return oldItem.id == newItem.id
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

        holder.itemView.setOnClickListener { view ->
            val action = ToBuyIngredientsFragmentDirections.actionToBuyIngredientsFragmentToToBuyIngredientDetailsFragment(ingredient.id)
            view.findNavController().navigate(action)
        }
    }

    inner class toBuyIngredientViewHolder(val itemBinding: ItemToBuyIngredientBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
