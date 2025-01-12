package com.example.fridgeChefAIApp.ui.specialRecipesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.api.model.Recipe
import com.example.fridgeChefAIApp.databinding.ItemFavRecipeOuterBinding
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe


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
        return FavRecipeViewHolder(ItemFavRecipeOuterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavRecipeViewHolder, position: Int) {
        val favRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        Glide.with(binding.root)
            .load(favRecipe.image)
            .error(R.drawable.dish_smaller) // Fallback image in case of an error
            .into(binding.recipeImage)


        binding.textView.text = favRecipe.title

        holder.itemView.setOnClickListener { view ->
            val recipe = Recipe(
                id = favRecipe.id,
                title = favRecipe.title,
                image = favRecipe.image,
                readyInMinutes = favRecipe.readyInMinutes,
                servings = favRecipe.servings,
                summary = favRecipe.summary,
                wellWrittenSummary = favRecipe.wellWrittenSummary
            )
            val action = SpecialRecipesFragmentDirections.actionSpecialRecipesFragmentToRecipeDetailsFragment(recipe , 1)
            view.findNavController().navigate(action)
        }
    }

    inner class FavRecipeViewHolder(val itemBinding: ItemFavRecipeOuterBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
