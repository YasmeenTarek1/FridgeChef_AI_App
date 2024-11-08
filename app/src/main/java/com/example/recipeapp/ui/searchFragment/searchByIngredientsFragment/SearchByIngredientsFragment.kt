package com.example.recipeapp.ui.searchFragment.searchByIngredientsFragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
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
import com.example.recipeapp.databinding.DialogChatBotIngredientsBinding
import com.example.recipeapp.databinding.DialogNewIngredientsBinding
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SearchByIngredientsViewModel
    private lateinit var adapter: SearchByIngredientsAdapter
    private lateinit var repository: Repository
    private lateinit var searchView: SearchView
    private var ingredients1: MutableList<String> = mutableListOf()


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

        binding.submitButton.visibility = View.INVISIBLE

        val chipGroup: ChipGroup = binding.chipGroup

        searchView = binding.searchView
        searchView.setQuery("", false)
        searchView.isIconified = false
        searchView.clearFocus()

        lifecycleScope.launch {
            binding.hello.text = "Hello, ${viewModel.getUserName()}"
            val profileImage = viewModel.getUserImage()
            if(profileImage != null) {
                withContext(Dispatchers.Main) {
                    Glide.with(requireContext())
                        .load(viewModel.getUserImage())
                        .into(binding.userAvatar)
                }
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (!ingredients1.contains(query) && query.isNotEmpty()) {  // Check if the ingredient is already in the list
                        lifecycleScope.launch {
                            addChip(query, chipGroup, ingredients1, binding.submitButton)
                            ingredients1.add(query)
                        }
                        searchView.setQuery("", false)
                        searchView.clearFocus()
                        binding.optionsRecyclerView.visibility = View.GONE
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

                // Using Flows to handle ingredient input changes and fetch predictions
                lifecycleScope.launch {
                    viewModel.autocompleteIngredientSearch(newText.toString()).collect { options ->
                        optionsAdapter.differ.submitList(options)
                    }
                }
                return true
            }
        })

        binding.submitButton.setOnClickListener {
            lifecycleScope.launch {
                Log.d("ingredients" , ingredients1.toString())
                adapter.differ.submitList(viewModel.searchRecipesByIngredients(ingredients1))
            }
        }

        binding.chatBotButton.setOnClickListener {
            if(ingredients1.isEmpty())
                showNewIngredientsDialog()
            else
                showIngredientOptionsDialog()
        }
    }

    private fun addChip(query: String , chipGroup: ChipGroup , ingredients: MutableList<String> , submit: ImageButton) {
        val chip = LayoutInflater.from(requireContext()).inflate(R.layout.custom_chip, null) as Chip
        chip.isCloseIconVisible = true
        chip.text = query

        chip.setOnCloseIconClickListener {
            ingredients.remove(query)
            chipGroup.removeView(chip)
            if (chipGroup.isEmpty())
                submit.visibility = View.INVISIBLE
        }

        chipGroup.addView(chip)
        submit.visibility = View.VISIBLE
    }

    private fun showIngredientOptionsDialog() {
        val dialogViewBinding = DialogChatBotIngredientsBinding.inflate(LayoutInflater.from(requireContext()))

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogViewBinding.root)
            .create()
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBuilder.setOnShowListener {
            dialogBuilder.window?.decorView?.translationX = 140f // Center X
        }
        dialogBuilder.show()

        dialogViewBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.SearchByOtherIngredientsRadioButton -> {
                    showNewIngredientsDialog()
                    dialogBuilder.dismiss()
                }

                R.id.SearchByEnteredIngredientsRadioButton -> {
                    findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotServiceFragment(ingredients1.toString()))
                    dialogBuilder.dismiss()
                }
            }
        }
    }

    private fun showNewIngredientsDialog() {
        searchView.setQuery("", false)
        searchView.clearFocus()
        searchView.isEnabled = false
        searchView.isFocusable = false
        searchView.isClickable = false

        val ingredients2: MutableList<String> = mutableListOf()

        val dialogViewBinding = DialogNewIngredientsBinding.inflate(LayoutInflater.from(requireContext()))

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogViewBinding.root)
            .create()
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBuilder.setOnShowListener {
            dialogBuilder.window?.decorView?.translationX = 60f // Center X
        }
        dialogBuilder.show()

        dialogViewBinding.editText.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = dialogViewBinding.editText.text.toString()

                if(!ingredients2.contains(query) && query.isNotEmpty()) {
                    lifecycleScope.launch {
                        addChip(query, dialogViewBinding.chipGroup , ingredients2, dialogViewBinding.submit)
                        ingredients2.add(query)
                    }
                }

                dialogViewBinding.editText.text.clear()
                dialogViewBinding.editText.clearFocus()

                true
            } else {
                false
            }
        }

        dialogViewBinding.submit.setOnClickListener {
            findNavController().navigate(SearchByIngredientsFragmentDirections.actionSearchByIngredientsFragmentToChatBotServiceFragment(ingredients2.toString()))
            dialogBuilder.dismiss()
        }
    }
}