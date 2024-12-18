package com.example.recipeapp.ui.cookedBeforeFragment

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
import com.example.recipeapp.databinding.FragmentCookedRecipesBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch

class CookedRecipesFragment : Fragment(R.layout.fragment_cooked_recipes) {

    private lateinit var binding: FragmentCookedRecipesBinding
    private lateinit var repository: Repository
    private lateinit var cookedViewModel: CookedBeforeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var cookedBeforeAdapter: CookedBeforeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCookedRecipesBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = CookedBeforeViewModelFactory(repository)
        cookedViewModel = ViewModelProvider(this, factory).get(CookedBeforeViewModel::class.java)

        cookedBeforeAdapter = CookedBeforeAdapter(
            onDeleteClick = { recipe ->
                cookedViewModel.deleteRecipe(recipe)
            }
        )

        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cookedBeforeAdapter

        lifecycleScope.launch {
            cookedViewModel.recipes.collect { recipes ->
                cookedBeforeAdapter.differ.submitList(recipes)
                if(cookedBeforeAdapter.itemCount == 0){
                    binding.emptyImage.visibility = View.VISIBLE
                }
                else{
                    binding.emptyImage.visibility = View.GONE
                }
            }
        }

    }

}