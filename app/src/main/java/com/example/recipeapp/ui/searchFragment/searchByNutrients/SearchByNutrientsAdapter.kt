package com.example.recipeapp.ui.searchFragment.searchByNutrients

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
import com.example.recipeapp.R
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemSearchResultBinding


class SearchByNutrientsAdapter : RecyclerView.Adapter<SearchByNutrientsAdapter.SearchByNutrientsViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByNutrientsViewHolder {
        return SearchByNutrientsViewHolder(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SearchByNutrientsViewHolder, position: Int) {
        val recipe = differ.currentList[position]

        val binding = holder.itemBinding

        binding.recipe = recipe

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

        holder.itemView.setOnClickListener { view ->
            val action = SearchByNutrientsFragmentDirections.actionSearchByNutrientsFragmentToRecipeDetailsFragment(recipe , 1)
            view.findNavController().navigate(action)
        }
    }

    // Function to convert dp to px
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    inner class SearchByNutrientsViewHolder(val itemBinding: ItemSearchResultBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
