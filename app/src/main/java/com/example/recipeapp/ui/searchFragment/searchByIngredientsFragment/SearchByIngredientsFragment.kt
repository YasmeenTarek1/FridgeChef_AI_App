package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentSearchByIngredientsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.ui.searchFragment.searchByNameFragment.IngredientSuggestionsAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchByIngredientsFragment : Fragment(R.layout.fragment_search_by_ingredients) {

    private lateinit var binding: FragmentSearchByIngredientsBinding
    private lateinit var chipGroup: ChipGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SearchByIngredientsViewModel
    private lateinit var adapter: SearchByIngredientsAdapter
    private val ingredients = mutableListOf<String>()
    private lateinit var repository: Repository
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchByIngredientsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = SearchByIngredientsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SearchByIngredientsViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchByIngredientsAdapter()
        recyclerView.adapter = adapter

        chipGroup = binding.chipGroup
        searchView = binding.searchView
        searchView.isIconified = false
        searchView.clearFocus()

        lifecycleScope.launch {
            binding.hello.text = "Hello, ${viewModel.getUserName()}"
            withContext(Dispatchers.Main) {
                Glide.with(requireContext())
                    .load(viewModel.getUserImage())
                    .into(binding.userAvatar)
            }
        }

        binding.chatBotButton.setOnClickListener {
            if (ingredients.isEmpty()) {
                findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotServiceFragment(null, null, 0))
            } else {
                findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotServiceFragment(ingredients.toString(), null, 0))
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (!ingredients.contains(query)) {  // Check if the ingredient is already in the list
                        lifecycleScope.launch {
                            binding.optionsRecyclerView.visibility = View.INVISIBLE
                            addChip(query)
                            ingredients.add(query)
                        }
                        searchView.setQuery("", false)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.optionsRecyclerView.visibility = View.VISIBLE

                val optionRecyclerView = binding.optionsRecyclerView
                optionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                optionRecyclerView.setHasFixedSize(true)

                val optionsAdapter = IngredientSuggestionsAdapter(onOptionClick = { chosenOption ->
                    searchView.setQuery(chosenOption, true)
                })
                optionRecyclerView.adapter = optionsAdapter

                // Use Flows to handle ingredient input changes and fetch predictions
                lifecycleScope.launch {
                    viewModel.autocompleteIngredientSearch(newText.toString()).collect { options ->
                        optionsAdapter.differ.submitList(options)
                    }
                }
                return true
            }
        })

        binding.submit.setOnClickListener {
            lifecycleScope.launch {
                adapter.differ.submitList(viewModel.searchRecipesByIngredients(ingredients))
            }
        }
    }

    private fun addChip(ingredient: String) {
        val chip = LayoutInflater.from(requireContext()).inflate(R.layout.custom_chip, null) as Chip
        chip.text = ingredient
        chip.isCloseIconVisible = true

        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
            ingredients.remove(ingredient)
        }
        chipGroup.addView(chip)
    }

}
