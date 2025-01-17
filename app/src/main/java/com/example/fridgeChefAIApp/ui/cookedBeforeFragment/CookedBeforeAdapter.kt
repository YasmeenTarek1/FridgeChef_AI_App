package com.example.fridgeChefAIApp.ui.cookedBeforeFragment

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
import com.example.fridgeChefAIApp.databinding.ItemCookedRecipeBinding
import com.example.fridgeChefAIApp.room_DB.model.CookedRecipe
import io.github.rexmtorres.android.swipereveallayout.ViewBinderHelper

class CookedBeforeAdapter(private val  onDeleteClick: (CookedRecipe) -> Unit) :
    RecyclerView.Adapter<CookedBeforeAdapter.CookedRecipeViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<CookedRecipe>() {
        override fun areItemsTheSame(oldItem: CookedRecipe, newItem: CookedRecipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CookedRecipe, newItem: CookedRecipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookedRecipeViewHolder {
        viewBinderHelper.setOpenOnlyOne(true) // Ensure only one swipe action is open at a time
        return CookedRecipeViewHolder(ItemCookedRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CookedRecipeViewHolder, position: Int) {
        val cookedRecipe = differ.currentList[position]
        val binding = holder.itemBinding

        // Bind the swipe layout to ensure that each item has a unique identifier for swipe actions
        viewBinderHelper.bind(binding.swipeLayout, cookedRecipe.id.toString())

        binding.recipe = cookedRecipe

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
            onDeleteClick(cookedRecipe)
            removeItem(position)
        }

        holder.itemView.setOnClickListener { view ->
            val recipe = Recipe(
                id = cookedRecipe.id,
                title = cookedRecipe.title,
                readyInMinutes = cookedRecipe.readyInMinutes,
                servings = cookedRecipe.servings,
                image = cookedRecipe.image,
                summary = cookedRecipe.summary,
                wellWrittenSummary = cookedRecipe.wellWrittenSummary
            )
            val action = CookedRecipesFragmentDirections.actionCookedRecipesFragmentToRecipeDetailsFragment(recipe , 1)
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

    inner class CookedRecipeViewHolder(val itemBinding: ItemCookedRecipeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
