package com.example.recipeapp.ui.feedFragment

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.recipeapp.R
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemFeedBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedAdapter(private val lifecycleOwner: LifecycleOwner,
                  private val checkFavorite: (Int) -> Flow<Boolean>,
                  private val onLoveClick: (Recipe) -> Unit,
                  private val onDislikeClick: (Recipe) -> Unit) : PagingDataAdapter<Recipe, FeedAdapter.RecipeViewHolder>(RECIPE_COMPARATOR) {


    companion object {
        private val RECIPE_COMPARATOR = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem.id == newItem.id && oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false) )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        val binding = holder.itemBinding

        lifecycleOwner.lifecycleScope.launch {
            checkFavorite(recipe!!.id).collect { isFavorite ->
                if (isFavorite) {
                    binding.fullLove.visibility = View.VISIBLE
                    binding.love.visibility = View.INVISIBLE
                } else {
                    binding.fullLove.visibility = View.INVISIBLE
                    binding.love.visibility = View.VISIBLE
                }
            }
        }

        binding.recipe = recipe!!
        binding.executePendingBindings()

        Glide.with(binding.root)
            .load(binding.recipe!!.image)
            .error(R.drawable.dish_smaller2) // Fallback image
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
                    layoutParams.bottomMargin = dpToPx(40)
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
                    layoutParams.bottomMargin = dpToPx(50)
                    binding.recipeImage.layoutParams = layoutParams
                    return false
                }
            })
            .into(binding.recipeImage)

        binding.love.setOnClickListener{
            binding.fullLove.visibility = View.VISIBLE
            binding.love.visibility = View.INVISIBLE
            onLoveClick(recipe)
        }
        binding.fullLove.setOnClickListener{
            binding.love.visibility = View.VISIBLE
            binding.fullLove.visibility = View.INVISIBLE
            onDislikeClick(recipe)
        }

        holder.itemView.setOnClickListener { view ->
            val action = FeedFragmentDirections.actionFeedFragmentToRecipeDetailsFragment(recipe , 1)
            view.findNavController().navigate(action)
        }
    }

    // Function to convert dp to px
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    inner class RecipeViewHolder(val itemBinding: ItemFeedBinding) : RecyclerView.ViewHolder(itemBinding.root){
        init {
            itemBinding.lifecycleOwner = lifecycleOwner
        }
    }
}
