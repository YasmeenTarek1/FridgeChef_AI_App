package com.example.fridgeChefAIApp.ui.searchFragment.searchByNutrients

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.DialogErrorNutrientsBinding
import com.example.fridgeChefAIApp.databinding.DialogNutrientsBinding
import com.example.fridgeChefAIApp.databinding.FragmentSearchByNutrientsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchByNutrientsFragment : Fragment(R.layout.fragment_search_by_nutrients) {

    private lateinit var binding: FragmentSearchByNutrientsBinding
    private lateinit var recyclerView: RecyclerView
    private val viewModel: SearchByNutrientsViewModel by viewModels()
    private lateinit var adapter: SearchByNutrientsAdapter

    private lateinit var carbSeekBar: SeekBar
    private lateinit var proteinSeekBar: SeekBar
    private lateinit var fatSeekBar: SeekBar
    private lateinit var sugarSeekBar: SeekBar
    private lateinit var caloriesSeekBar: SeekBar

    private var carb:Int = 0
    private var protein: Int = 0
    private var fat: Int = 0
    private var sugar: Int = 0
    private var calories: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchByNutrientsBinding.bind(view)
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
        carb = carbSeekBar.progress*10
        protein = proteinSeekBar.progress*10
        fat = fatSeekBar.progress*10
        sugar = sugarSeekBar.progress*10
        calories = caloriesSeekBar.progress*10

        lifecycleScope.launch {
            binding.loadingProgressBar.visibility = View.VISIBLE
            adapter.differ.submitList(viewModel.performSearch(maxCarbs = if (carb == 0) null else carb, maxProtein = if(protein == 0) null else protein, maxFat = if(fat == 0) null else fat, maxSugar = if(sugar == 0) null else sugar, maxCalories = if(calories == 0) null else calories))
            binding.loadingProgressBar.visibility = View.GONE
        }
    }

    private fun showNutrientsDialog() {
        val dialogViewBinding = DialogNutrientsBinding.inflate(LayoutInflater.from(requireContext()))

        carbSeekBar = dialogViewBinding.carbSeekBar
        proteinSeekBar = dialogViewBinding.proteinSeekBar
        fatSeekBar = dialogViewBinding.fatSeekBar
        sugarSeekBar = dialogViewBinding.sugarSeekBar
        caloriesSeekBar = dialogViewBinding.caloriesSeekBar

        carbSeekBar.progress = (carb/10)
        updateSeekBarVisibilityAndValue(carbSeekBar.progress, dialogViewBinding.cardView1, dialogViewBinding.cardView11, dialogViewBinding.carbsCancelButton, dialogViewBinding.carbValueTextView)
        proteinSeekBar.progress = (protein/10)
        updateSeekBarVisibilityAndValue(proteinSeekBar.progress, dialogViewBinding.cardView2, dialogViewBinding.cardView22, dialogViewBinding.proteinCancelButton, dialogViewBinding.proteinValueTextView)
        fatSeekBar.progress = (fat/10)
        updateSeekBarVisibilityAndValue(fatSeekBar.progress, dialogViewBinding.cardView3, dialogViewBinding.cardView33, dialogViewBinding.fatCancelButton, dialogViewBinding.fatValueTextView)
        sugarSeekBar.progress = (sugar/10)
        updateSeekBarVisibilityAndValue(sugarSeekBar.progress, dialogViewBinding.cardView4, dialogViewBinding.cardView44, dialogViewBinding.sugarCancelButton, dialogViewBinding.sugarValueTextView)
        caloriesSeekBar.progress = (calories/10)
        updateSeekBarVisibilityAndValue(caloriesSeekBar.progress, dialogViewBinding.cardView5, dialogViewBinding.cardView55, dialogViewBinding.caloriesCancelButton, dialogViewBinding.caloriesValueTextView)

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
                updateSeekBarVisibilityAndValue(
                    progress,
                    dialogViewBinding.cardView1,
                    dialogViewBinding.cardView11,
                    dialogViewBinding.carbsCancelButton,
                    carbValueTextView
                )
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        proteinSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSeekBarVisibilityAndValue(
                    progress,
                    dialogViewBinding.cardView2,
                    dialogViewBinding.cardView22,
                    dialogViewBinding.proteinCancelButton,
                    proteinValueTextView
                )
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        fatSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSeekBarVisibilityAndValue(
                    progress,
                    dialogViewBinding.cardView3,
                    dialogViewBinding.cardView33,
                    dialogViewBinding.fatCancelButton,
                    fatValueTextView
                )
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sugarSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSeekBarVisibilityAndValue(
                    progress,
                    dialogViewBinding.cardView4,
                    dialogViewBinding.cardView44,
                    dialogViewBinding.sugarCancelButton,
                    sugarValueTextView
                )
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        caloriesSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSeekBarVisibilityAndValue(
                    progress,
                    dialogViewBinding.cardView5,
                    dialogViewBinding.cardView55,
                    dialogViewBinding.caloriesCancelButton,
                    caloriesValueTextView
                )
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateSeekBarVisibilityAndValue(progress: Int, cardView1: View, cardView11: View, cancelButton: View, valueTextView: TextView) {
        if (progress == 0) {
            cardView1.visibility = View.GONE
            cardView11.visibility = View.GONE
            cancelButton.visibility = View.VISIBLE
        } else {
            cardView1.visibility = View.VISIBLE
            cardView11.visibility = View.VISIBLE
            cancelButton.visibility = View.GONE
            valueTextView.text = (progress * 10).toString()
        }
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