package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.model.Ingredient
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentSearchByIngredientsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.ui.searchFragment.searchByNameFragment.IngredientSuggestionsAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

import java.util.Timer
import java.util.TimerTask

class SearchByIngredientsFragment : Fragment(R.layout.fragment_search_by_ingredients) {

    private lateinit var binding: FragmentSearchByIngredientsBinding
    private lateinit var chipGroup: ChipGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SearchByIngredientsViewModel
    private lateinit var adapter: SearchByIngredientsAdapter
    private val ingredients = mutableListOf<String>()
    private lateinit var repository: Repository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchByIngredientsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        chipGroup = binding.chipGroup

        binding.addIngredientButton.setOnClickListener {
            showAddIngredientDialog()
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val factory = SearchByIngredientsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SearchByIngredientsViewModel::class.java)

        adapter = SearchByIngredientsAdapter()
        recyclerView.adapter = adapter

        binding.search.setOnClickListener {
            lifecycleScope.launch {
                adapter.differ.submitList(viewModel.searchRecipesByIngredients(ingredients))
            }
        }

        binding.chatBot.setOnClickListener {
            if (ingredients.isEmpty()) {
                findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotServiceFragment(null, null, 0))
            } else {
                findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotServiceFragment(ingredients.toString(), null, 0))
            }
        }
    }


    private fun showAddIngredientDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_ingredient, null)
        val builder = AlertDialog.Builder(requireContext())
        val ingredientInput = dialogView.findViewById<EditText>(R.id.ingredientInput)

        val optionRecyclerView:RecyclerView = dialogView.findViewById(R.id.ingredientSuggestionsRecyclerView)
        optionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        optionRecyclerView.setHasFixedSize(true)

        val optionsAdapter = IngredientSuggestionsAdapter(onOptionClick = { chosenOption ->
            ingredientInput.text = Editable.Factory.getInstance().newEditable(chosenOption)
        })
        optionRecyclerView.adapter = optionsAdapter

        // Use Flows to handle ingredient input changes and fetch predictions
        lifecycleScope.launch {
            ingredientInput.textChanges()
                .debounce(300)  // Wait for 300ms to avoid excessive API calls
                .filter { it?.length != 0 }  // Only proceed if input is not empty
                .flatMapLatest { query ->
                    viewModel.autocompleteIngredientSearch(query.toString())
                }
                .collect { options ->
                    Log.d("Options", options.toString())
                    optionsAdapter.differ.submitList(options)
                }
        }

        builder.setView(dialogView)
            .setTitle("Add Ingredient")
            .setPositiveButton("Add") { dialog, _ ->
                val ingredient = ingredientInput.text.toString().trim()
                if (ingredient.isNotEmpty()) {
                    addChip(ingredient)
                    ingredients.add(ingredient)
                    ingredientInput.text.clear()  // Clear input for next ingredient
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Extension function for EditText to convert text changes to Flow
    fun EditText.textChanges(): Flow<CharSequence?> = callbackFlow {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                trySend(s)  // Send the latest text input to the flow
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        addTextChangedListener(watcher)
        awaitClose { removeTextChangedListener(watcher) }  // Remove listener when the Flow is closed
    }

    private fun addChip(ingredient: String) {
        val chip = Chip(requireContext()).apply {
            text = ingredient
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
                ingredients.remove(ingredient)
            }
        }
        chipGroup.addView(chip)
    }

}
