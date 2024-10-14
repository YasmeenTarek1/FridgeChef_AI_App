package com.example.recipeapp.ui.favoriteRecipesFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentFavoriteRecipesBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch

class FavoriteRecipesFragment : Fragment(R.layout.fragment_favorite_recipes) {

    private lateinit var binding: FragmentFavoriteRecipesBinding
    private lateinit var repository: Repository
    private lateinit var favViewModel: FavoriteRecipesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteRecipesAdapter: FavoriteRecipesAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFavoriteRecipesBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = FavoriteRecipesViewModelFactory(repository)
        favViewModel = ViewModelProvider(this, factory).get(FavoriteRecipesViewModel::class.java)

        favoriteRecipesAdapter = FavoriteRecipesAdapter(favViewModel)

        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoriteRecipesAdapter

        lifecycleScope.launch {
            favViewModel.recipes.collect { recipes ->
                favoriteRecipesAdapter.differ.submitList(recipes)
            }
        }
    }

}
