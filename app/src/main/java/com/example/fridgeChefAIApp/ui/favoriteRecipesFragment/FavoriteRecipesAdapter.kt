package com.example.fridgeChefAIApp.ui.favoriteRecipesFragment

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.api.model.Recipe
import com.example.fridgeChefAIApp.databinding.ItemFavoriteRecipeBinding
import com.example.fridgeChefAIApp.room_DB.model.FavoriteRecipe
import io.github.rexmtorres.android.swipereveallayout.ViewBinderHelper

class FavoriteRecipesAdapter(private val onDeleteClick: (FavoriteRecipe) -> Unit) :
    RecyclerView.Adapter<FavoriteRecipesAdapter.FavoriteRecipeViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<FavoriteRecipe>() {
        override fun areItemsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteRecipe, newItem: FavoriteRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipeViewHolder {
        val binding = ItemFavoriteRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        viewBinderHelper.setOpenOnlyOne(true) // Ensure only one swipe action is open at a time
        return FavoriteRecipeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavoriteRecipeViewHolder, position: Int) {
        val favRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        // Close any open swipe layouts before binding (avoids animations)
        viewBinderHelper.closeLayout(favRecipe.id.toString())

        // Bind the swipe layout to ensure that each item has a unique identifier for swipe actions
        viewBinderHelper.bind(binding.swipeLayout, favRecipe.id.toString())

        binding.recipe = favRecipe

        Glide.with(binding.root)
            .load(binding.recipe!!.image)
            .error(R.drawable.dish_smaller) // Fallback image
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    p0: Drawable,
                    p1: Any,
                    p2: Target<Drawable>?,
                    p3: DataSource,
                    p4: Boolean,
                ): Boolean {
                    // Reset margin
                    val layoutParams = binding.recipeImage.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.marginEnd = dpToPx(224)
                    binding.recipeImage.layoutParams = layoutParams
                    return false
                }
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<Drawable>,
                    p3: Boolean,
                ): Boolean {
                    // error case
                    val layoutParams = binding.recipeImage.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.marginEnd = dpToPx(210)
                    binding.recipeImage.layoutParams = layoutParams
                    return false
                }
            })
            .into(binding.recipeImage)

        binding.delete.setOnClickListener {
            onDeleteClick(favRecipe)
            removeItem(position)
        }

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
            val action = FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToRecipeDetailsFragment(recipe , 1)
            view.findNavController().navigate(action)
        }
    }

    // Function to convert dp to px
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun removeItem(position: Int) {
        val currentList = differ.currentList.toMutableList()
        currentList.removeAt(position)
        differ.submitList(currentList)
    }

    inner class FavoriteRecipeViewHolder(val itemBinding: ItemFavoriteRecipeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
