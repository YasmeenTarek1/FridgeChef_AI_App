package com.example.fridgeChefAIApp.ui.searchFragment.searchByNameFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.api.model.Recipe
import com.example.fridgeChefAIApp.databinding.ItemSuggestionBinding


class RecipeSuggestionsAdapter(private val onOptionClick: (String) -> Unit) : RecyclerView.Adapter<RecipeSuggestionsAdapter.RecipeSuggestionsViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeSuggestionsViewHolder {
        return RecipeSuggestionsViewHolder(ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecipeSuggestionsViewHolder, position: Int) {
        val option = differ.currentList[position]
        holder.itemBinding.suggestionTextView.text = option.title
        holder.itemView.setOnClickListener{
            onOptionClick(option.title)
        }
    }

    inner class RecipeSuggestionsViewHolder(val itemBinding: ItemSuggestionBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
