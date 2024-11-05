package com.example.recipeapp.ui.searchFragment.searchByNutrients

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.DialogErrorNutrientsBinding
import com.example.recipeapp.databinding.DialogNutrientsBinding
import com.example.recipeapp.databinding.FragmentSearchByNutrientsBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.launch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchByNutrientsBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = SearchByNutrientsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SearchByNutrientsViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = SearchByNutrientsAdapter()
        recyclerView.adapter = adapter

        showNutrientsDialog()

        binding.settingsButton.setOnClickListener{
            showNutrientsDialog()
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

        lifecycleScope.launch {
            adapter.differ.submitList(viewModel.performSearch(maxCarbs = carb, maxProtein = protein, maxFat = fat, maxSugar = sugar, maxCalories = calories))
        }
    }

    private fun showNutrientsDialog() {
        val dialogViewBinding = DialogNutrientsBinding.inflate(LayoutInflater.from(requireContext()))

        carbSeekBar = dialogViewBinding.carbSeekBar
        proteinSeekBar = dialogViewBinding.proteinSeekBar
        fatSeekBar = dialogViewBinding.fatSeekBar
        sugarSeekBar = dialogViewBinding.sugarSeekBar
        caloriesSeekBar = dialogViewBinding.caloriesSeekBar

        createSeekBarListeners(dialogViewBinding)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogViewBinding.root)
            .create()
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBuilder.show()

        dialogViewBinding.saveButton.setOnClickListener{
            recyclerView.visibility = View.VISIBLE
            if(carbSeekBar.progress != 0 || proteinSeekBar.progress != 0 || fatSeekBar.progress != 0 || sugarSeekBar.progress != 0 || caloriesSeekBar.progress != 0)
                performSearch()
            else
                showErrorDialog()
            dialogBuilder.dismiss()
        }
    }

    private fun createSeekBarListeners(dialogViewBinding: DialogNutrientsBinding) {

        val carbValueTextView = dialogViewBinding.carbValueTextView
        val proteinValueTextView = dialogViewBinding.proteinValueTextView
        val fatValueTextView = dialogViewBinding.fatValueTextView
        val sugarValueTextView = dialogViewBinding.sugarValueTextView
        val caloriesValueTextView = dialogViewBinding.caloriesValueTextView

        dialogViewBinding.carbsCancelButton.setOnClickListener {
            dialogViewBinding.carbSeekBar.visibility = View.GONE
            dialogViewBinding.linearLayout1.visibility = View.GONE
            dialogViewBinding.carbsCancelButton.visibility = View.GONE
        }
        dialogViewBinding.proteinCancelButton.setOnClickListener {
            dialogViewBinding.proteinSeekBar.visibility = View.GONE
            dialogViewBinding.linearLayout2.visibility = View.GONE
            dialogViewBinding.proteinCancelButton.visibility = View.GONE
        }
        dialogViewBinding.fatCancelButton.setOnClickListener {
            dialogViewBinding.fatSeekBar.visibility = View.GONE
            dialogViewBinding.linearLayout3.visibility = View.GONE
            dialogViewBinding.fatCancelButton.visibility = View.GONE
        }
        dialogViewBinding.sugarCancelButton.setOnClickListener {
            dialogViewBinding.sugarSeekBar.visibility = View.GONE
            dialogViewBinding.linearLayout4.visibility = View.GONE
            dialogViewBinding.sugarCancelButton.visibility = View.GONE
        }
        dialogViewBinding.caloriesCancelButton.setOnClickListener {
            dialogViewBinding.caloriesSeekBar.visibility = View.GONE
            dialogViewBinding.linearLayout5.visibility = View.GONE
            dialogViewBinding.caloriesCancelButton.visibility = View.GONE
        }

        // Set listeners for each SeekBar to update the TextView next to it
        carbSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    dialogViewBinding.cardView1.visibility = View.GONE
                    dialogViewBinding.cardView11.visibility = View.GONE
                    dialogViewBinding.carbsCancelButton.visibility = View.VISIBLE
                }
                else{
                    dialogViewBinding.cardView1.visibility = View.VISIBLE
                    dialogViewBinding.cardView11.visibility = View.VISIBLE
                    dialogViewBinding.carbsCancelButton.visibility = View.GONE
                    carbValueTextView.text = (progress * 10).toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        proteinSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    dialogViewBinding.cardView2.visibility = View.GONE
                    dialogViewBinding.cardView22.visibility = View.GONE
                    dialogViewBinding.proteinCancelButton.visibility = View.VISIBLE
                }
                else{
                    dialogViewBinding.cardView2.visibility = View.VISIBLE
                    dialogViewBinding.cardView22.visibility = View.VISIBLE
                    dialogViewBinding.proteinCancelButton.visibility = View.GONE
                    proteinValueTextView.text = (progress * 10).toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        fatSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    dialogViewBinding.cardView3.visibility = View.GONE
                    dialogViewBinding.cardView33.visibility = View.GONE
                    dialogViewBinding.fatCancelButton.visibility = View.VISIBLE
                }
                else{
                    dialogViewBinding.cardView3.visibility = View.VISIBLE
                    dialogViewBinding.cardView33.visibility = View.VISIBLE
                    dialogViewBinding.fatCancelButton.visibility = View.GONE
                    fatValueTextView.text = (progress * 10).toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sugarSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    dialogViewBinding.cardView4.visibility = View.GONE
                    dialogViewBinding.cardView44.visibility = View.GONE
                    dialogViewBinding.sugarCancelButton.visibility = View.VISIBLE
                }
                else{
                    dialogViewBinding.cardView4.visibility = View.VISIBLE
                    dialogViewBinding.cardView44.visibility = View.VISIBLE
                    dialogViewBinding.sugarCancelButton.visibility = View.GONE
                    sugarValueTextView.text = (progress * 10).toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        caloriesSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0){
                    dialogViewBinding.cardView5.visibility = View.GONE
                    dialogViewBinding.cardView55.visibility = View.GONE
                    dialogViewBinding.caloriesCancelButton.visibility = View.VISIBLE
                }
                else{
                    dialogViewBinding.cardView5.visibility = View.VISIBLE
                    dialogViewBinding.cardView55.visibility = View.VISIBLE
                    dialogViewBinding.caloriesCancelButton.visibility = View.GONE
                    caloriesValueTextView.text = (progress * 10).toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun showErrorDialog() {
        val dialogViewBinding = DialogErrorNutrientsBinding.inflate(LayoutInflater.from(requireContext()))

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogViewBinding.root)
            .create()
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBuilder.setOnShowListener {
            dialogBuilder.window?.decorView?.translationX = 90f
        }
        dialogBuilder.show()

        dialogViewBinding.retryButton.setOnClickListener{
            dialogBuilder.dismiss()
            showNutrientsDialog()
        }
    }
}