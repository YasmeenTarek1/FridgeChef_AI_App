package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentSearchByIngredientsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

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
            adapter.differ.submitList(viewModel.searchRecipesByIngredients(ingredients))
        }

        binding.chatBot.setOnClickListener {
            if (ingredients.isEmpty()) {
                findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotFragment(null, null, 0))
            } else {
                findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotFragment(ingredients.toString(), null, 0))
            }
        }

        binding.previewView.visibility = View.INVISIBLE

    }

    private fun showAddIngredientDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_ingredient, null)
        val ingredientInput = dialogView.findViewById<EditText>(R.id.ingredientInput)

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
