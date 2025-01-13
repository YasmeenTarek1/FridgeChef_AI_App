package com.example.fridgeChefAIApp.ui.toBuyIngredientsFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentToBuyIngredientsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ToBuyIngredientsFragment : Fragment(R.layout.fragment_to_buy_ingredients) {

    private lateinit var binding: FragmentToBuyIngredientsBinding
    private val viewModel: ToBuyIngredientsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var toBuyIngredientsAdapter: ToBuyIngredientsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentToBuyIngredientsBinding.bind(view)
        toBuyIngredientsAdapter = ToBuyIngredientsAdapter(
            onDeleteClick = { ingredient ->
                viewModel.deleteIngredient(ingredient)
            }
        )

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = toBuyIngredientsAdapter


        lifecycleScope.launch {
            viewModel.ingredients.collect { ingredients ->
                toBuyIngredientsAdapter.differ.submitList(ingredients)
                if(toBuyIngredientsAdapter.itemCount == 0)
                    binding.emptyImage.visibility = View.VISIBLE
                else
                    binding.emptyImage.visibility = View.GONE
            }
        }
    }
}