package com.example.recipeapp.ui.specialRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.databinding.FavRecipeOuterItemBinding
import com.example.recipeapp.room_DB.model.FavoriteRecipe


class FavRecipesAdapter : RecyclerView.Adapter<FavRecipesAdapter.FavRecipeViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<FavoriteRecipe>() {
        override fun areItemsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavRecipeViewHolder {
        return FavRecipeViewHolder(FavRecipeOuterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavRecipeViewHolder, position: Int) {
        val favRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(favRecipe.image)
            .into(binding.recipeImage)

        binding.textView.text = favRecipe.title

        holder.itemView.setOnClickListener { view ->
            val action = SpecialRecipesFragmentDirections.actionSpecialRecipesFragmentToRecipeDetailsFragment(favRecipe.id , null)
            view.findNavController().navigate(action)
        }
    }

    inner class FavRecipeViewHolder(val itemBinding: FavRecipeOuterItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
