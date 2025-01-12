package com.example.fridgeChefAIApp.ui.toBuyIngredientsFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.Repository
import com.example.fridgeChefAIApp.api.service.RetrofitInstance
import com.example.fridgeChefAIApp.databinding.FragmentToBuyIngredientsBinding
import com.example.fridgeChefAIApp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch


class ToBuyIngredientsFragment : Fragment(R.layout.fragment_to_buy_ingredients) {

    private lateinit var binding: FragmentToBuyIngredientsBinding
    private lateinit var repository: Repository
    private lateinit var viewModel: ToBuyIngredientsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var toBuyIngredientsAdapter: ToBuyIngredientsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentToBuyIngredientsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = ToBuyIngredientsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ToBuyIngredientsViewModel::class.java)

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