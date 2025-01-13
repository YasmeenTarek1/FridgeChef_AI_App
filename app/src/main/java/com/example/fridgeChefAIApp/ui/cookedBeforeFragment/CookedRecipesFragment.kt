package com.example.fridgeChefAIApp.ui.cookedBeforeFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentCookedRecipesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CookedRecipesFragment : Fragment(R.layout.fragment_cooked_recipes) {

    private lateinit var binding: FragmentCookedRecipesBinding
    private val cookedViewModel: CookedBeforeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var cookedBeforeAdapter: CookedBeforeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCookedRecipesBinding.bind(view)
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