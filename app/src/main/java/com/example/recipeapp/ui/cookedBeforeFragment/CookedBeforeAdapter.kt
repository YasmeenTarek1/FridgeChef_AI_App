package com.example.recipeapp.ui.cookedBeforeFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.room_DB.model.CookedRecipe
import com.example.recipeapp.databinding.CookedRecipeItemBinding

class CookedBeforeAdapter : RecyclerView.Adapter<CookedBeforeAdapter.CookedRecipeViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<CookedRecipe>() {
        override fun areItemsTheSame(oldItem: CookedRecipe, newItem: CookedRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CookedRecipe, newItem: CookedRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookedRecipeViewHolder {
        return CookedRecipeViewHolder(CookedRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CookedRecipeViewHolder, position: Int) {
        val cookedRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(cookedRecipe.image)
            .into(binding.recipeImage)

        binding.servings.text = "Servings: " + cookedRecipe.servings.toString()

        holder.itemView.setOnClickListener { view ->
            val action = CookedRecipesFragmentDirections.actionCookedRecipesFragmentToRecipeDetailsFragment(cookedRecipe.id , null)
            view.findNavController().navigate(action)
        }
    }

    inner class CookedRecipeViewHolder(val itemBinding: CookedRecipeItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
