package com.example.recipeapp.ui.searchFragment.searchByNameFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentSearchByNameBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchByNameFragment : Fragment(R.layout.fragment_search_by_name) {

    private lateinit var binding: FragmentSearchByNameBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SearchByNameViewModel
    private lateinit var adapter: SearchByNameAdapter
    private lateinit var repository: Repository
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentSearchByNameBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = SearchByNameViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SearchByNameViewModel::class.java)

        searchView = binding.searchView
        recyclerView = binding.recyclerView

        searchView.isIconified = false
        searchView.clearFocus()

        lifecycleScope.launch {
            binding.hello.text = "Hello, ${viewModel.getUserName()}"
            withContext(Dispatchers.Main) {
                if(viewModel.getUserImage() != null) {
                    binding.userAvatar.scaleX = 1.0f
                    binding.userAvatar.scaleY = 1.0f
                    Glide.with(requireContext())
                        .load(viewModel.getUserImage())
                        .into(binding.userAvatar)
                }
                else{
                    binding.userAvatar.scaleX = 1.25f
                    binding.userAvatar.scaleY = 1.25f
                    Glide.with(requireContext())
                        .load(R.drawable.no_avatar)
                        .into(binding.userAvatar)
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchByNameAdapter()
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    lifecycleScope.launch {
                        binding.optionsRecyclerView.visibility = View.INVISIBLE
                        adapter.differ.submitList(viewModel.searchRecipesByName(query))
                        if(adapter.itemCount == 0){
                            binding.emptyImage.visibility = View.VISIBLE
                        }
                        else{
                            binding.emptyImage.visibility = View.GONE
                        }
                    }
                    searchView.setQuery("", false)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.optionsRecyclerView.visibility = View.VISIBLE

                val optionRecyclerView = binding.optionsRecyclerView
                optionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                optionRecyclerView.setHasFixedSize(true)

                val optionsAdapter = RecipeSuggestionsAdapter(onOptionClick = { chosenOption ->
                    searchView.setQuery(chosenOption, true)
                })
                optionRecyclerView.adapter = optionsAdapter

                // Use Flows to handle ingredient input changes and fetch predictions
                lifecycleScope.launch {
                    viewModel.autocompleteRecipeSearch(newText.toString()).collect { options ->
                        optionsAdapter.differ.submitList(options)
                        Log.d("Error" , options.toString())
                    }
                }
                return true
            }
        })
    }

}