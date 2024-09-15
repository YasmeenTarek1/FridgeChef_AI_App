package com.example.recipeapp.ui.searchFragment

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.R
import com.example.recipeapp.databinding.FragmentSearchCategoryBinding

class SearchCategoryFragment : Fragment(R.layout.fragment_search_category) {

    private lateinit var binding: FragmentSearchCategoryBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchCategoryBinding.bind(view)
        binding.lifecycleOwner = this

        val radioGroup: RadioGroup = binding.radioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.SearchByIngredientsRadioButton -> {
                    findNavController().navigate(R.id.action_searchCategoryFragment_to_searchByIngredientsFragment)
                }
                R.id.SearchByNutrientsRadioButton -> {
                    findNavController().navigate(R.id.action_searchCategoryFragment_to_searchByNutrientsFragment)
                }
                R.id.SearchByRecipeNameRadioButton -> {
                    findNavController().navigate(R.id.action_searchCategoryFragment_to_searchByNameFragment)
                }
            }
        }

    }
}