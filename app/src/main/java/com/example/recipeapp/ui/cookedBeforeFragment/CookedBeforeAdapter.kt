package com.example.recipeapp.ui.cookedBeforeFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.api.model.Recipe
import com.example.recipeapp.databinding.ItemCookedRecipeBinding
import com.example.recipeapp.room_DB.model.CookedRecipe
import io.github.rexmtorres.android.swipereveallayout.ViewBinderHelper
import kotlinx.coroutines.runBlocking

class CookedBeforeAdapter(private val viewModel: CookedBeforeViewModel) : RecyclerView.Adapter<CookedBeforeAdapter.CookedRecipeViewHolder>() {

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

        Glide.with(binding.root)
            .load(cookedRecipe.image)
            .into(binding.recipeImage)

        binding.recipe = cookedRecipe

        binding.delete.setOnClickListener {
            runBlocking {
                viewModel.deleteRecipe(cookedRecipe)
            }
            removeItem(position)
        }
        holder.itemView.setOnClickListener { view ->
            val recipe = Recipe(
                id = cookedRecipe.id,
                title = cookedRecipe.title,
                image = cookedRecipe.image,
                readyInMinutes = cookedRecipe.readyInMinutes,
                servings = cookedRecipe.servings,
                ingredients = cookedRecipe.ingredients,
                steps = cookedRecipe.steps,
                summary = cookedRecipe.summary
            )
            val action = CookedRecipesFragmentDirections.actionCookedRecipesFragmentToRecipeDetailsFragment(recipe)
            view.findNavController().navigate(action)
        }
    }

    private fun removeItem(position: Int) {
        val currentList = differ.currentList.toMutableList()
        currentList.removeAt(position)
        differ.submitList(currentList)
    }


    inner class CookedRecipeViewHolder(val itemBinding: ItemCookedRecipeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
