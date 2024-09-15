package com.example.recipeapp.ui.searchFragment.searchByNutrients

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.databinding.FragmentSearchByNutrientsBinding

class SearchByNutrientsFragment : Fragment(R.layout.fragment_search_by_nutrients) {

    private lateinit var binding: FragmentSearchByNutrientsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SearchByNutrientsViewModel
    private lateinit var adapter: SearchByNutrientsAdapter
    private lateinit var repository: Repository

    private lateinit var carbSeekBar: SeekBar
    private lateinit var proteinSeekBar: SeekBar
    private lateinit var fatSeekBar: SeekBar
    private lateinit var sugarSeekBar: SeekBar
    private lateinit var caloriesSeekBar: SeekBar

    private lateinit var carbProgressBar: ProgressBar
    private lateinit var proteinProgressBar: ProgressBar
    private lateinit var fatProgressBar: ProgressBar
    private lateinit var sugarProgressBar: ProgressBar
    private lateinit var caloriesProgressBar: ProgressBar

    private lateinit var summaryTextView: TextView
    private lateinit var searchButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchByNutrientsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = SearchByNutrientsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SearchByNutrientsViewModel::class.java)

        carbSeekBar = binding.carbSeekBar
        proteinSeekBar = binding.proteinSeekBar
        fatSeekBar = binding.fatSeekBar
        sugarSeekBar = binding.sugarSeekBar
        caloriesSeekBar = binding.caloriesSeekBar

        carbProgressBar = binding.carbProgressBar
        proteinProgressBar = binding.proteinProgressBar
        fatProgressBar = binding.fatProgressBar
        sugarProgressBar = binding.sugarProgressBar
        caloriesProgressBar = binding.caloriesProgressBar

        summaryTextView = binding.summaryTextView
        searchButton = binding.searchButton

        // Setup SeekBars and ProgressBars
        setupSeekBars()

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = SearchByNutrientsAdapter()
        recyclerView.adapter = adapter

        searchButton.setOnClickListener {
            recyclerView.visibility = View.VISIBLE
            binding.LinearLayout.visibility = View.GONE
            performSearch()
        }
    }

    private fun performSearch() {
        // Collect nutrient values
        var carb:Int? = null
        var protein: Int? = null
        var fat: Int? = null
        var sugar: Int? = null
        var calories: Int? = null

        if(carbSeekBar.progress != 0)
            carb = carbSeekBar.progress*10
        if(proteinSeekBar.progress != 0)
            protein = proteinSeekBar.progress*10
        if(fatSeekBar.progress != 0)
            fat = fatSeekBar.progress*10
        if(sugarSeekBar.progress != 0)
            sugar = sugarSeekBar.progress*10
        if(caloriesSeekBar.progress != 0)
            calories = caloriesSeekBar.progress*10

        adapter.differ.submitList(viewModel.searchRecipesByNutrients(maxCarbs = carb, maxProtein = protein, maxFat = fat, maxSugar = sugar, maxCalories = calories))
    }

    private fun setupSeekBars() {
        carbSeekBar.setOnSeekBarChangeListener(createSeekBarListener(carbProgressBar, "Carbs"))
        proteinSeekBar.setOnSeekBarChangeListener(createSeekBarListener(proteinProgressBar, "Protein"))
        fatSeekBar.setOnSeekBarChangeListener(createSeekBarListener(fatProgressBar, "Fat"))
        sugarSeekBar.setOnSeekBarChangeListener(createSeekBarListener(sugarProgressBar, "Sugar"))
        caloriesSeekBar.setOnSeekBarChangeListener(createSeekBarListener(caloriesProgressBar, "Calories"))
    }

    private fun createSeekBarListener(progressBar: ProgressBar, nutrient: String): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressBar.progress = progress
                val carb = carbSeekBar.progress*10
                val protein = proteinSeekBar.progress*10
                val fat = fatSeekBar.progress*10
                val sugar = sugarSeekBar.progress*10
                val calories = caloriesSeekBar.progress*10

                summaryTextView.text = "Carbs: $carb, Protein: $protein, Fat: $fat, Sugar: $sugar, Calories: $calories"            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
    }


}