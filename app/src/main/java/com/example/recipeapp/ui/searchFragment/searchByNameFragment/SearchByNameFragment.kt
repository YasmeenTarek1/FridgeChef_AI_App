package com.example.recipeapp.ui.searchFragment.searchByNameFragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.databinding.FragmentSearchByNameBinding

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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchByNameAdapter()
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    adapter.differ.submitList(viewModel.searchRecipesByName(query))

                    searchView.clearFocus() // Remove focus from the SearchView
                    searchView.onActionViewCollapsed() // Collapse the SearchView
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

}