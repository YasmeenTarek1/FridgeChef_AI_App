package com.example.fridgeChefAIApp.ui.favoriteRecipesFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentFavoriteRecipesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment(R.layout.fragment_favorite_recipes) {

    private lateinit var binding: FragmentFavoriteRecipesBinding
    private val favViewModel: FavoriteRecipesViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteRecipesAdapter: FavoriteRecipesAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFavoriteRecipesBinding.bind(view)
        favoriteRecipesAdapter = FavoriteRecipesAdapter(
            onDeleteClick = { favRecipe ->
            favViewModel.deleteRecipe(favRecipe)
        })

        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoriteRecipesAdapter

        lifecycleScope.launch {
            favViewModel.recipes.collect { recipes ->
                favoriteRecipesAdapter.differ.submitList(recipes)
                if(favoriteRecipesAdapter.itemCount == 0){
                    binding.emptyImage.visibility = View.VISIBLE
                }
                else{
                    binding.emptyImage.visibility = View.GONE
                }
            }
        }
    }

}
