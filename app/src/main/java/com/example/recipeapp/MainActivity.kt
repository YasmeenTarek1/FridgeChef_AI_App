package com.example.recipeapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.recipeapp.databinding.ActivityMainBinding
import com.example.recipeapp.databinding.DialogSearchOptionsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        // Hook up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Add a listener for destination changes to hide or show the BottomNavigationView
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.feedFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.VISIBLE
                }
                R.id.userProfileFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.VISIBLE
                }
                R.id.specialRecipesFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.VISIBLE
                }
                R.id.toBuyIngredientsFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.INVISIBLE
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feedFragment -> {
                    navController.navigate(R.id.feedFragment)
                    true
                }
                R.id.specialRecipesFragment -> {
                    navController.navigate(R.id.specialRecipesFragment)
                    true
                }
                R.id.userProfileFragment -> {
                    navController.navigate(R.id.userProfileFragment)
                    true
                }
                R.id.searchCategoryFragment -> {
                    showSearchOptionsDialog()
                    true
                }
                R.id.toBuyIngredientsFragment -> {
                    navController.navigate(R.id.toBuyIngredientsFragment)
                    true
                }
                else -> false
            }
        }
        FirebaseApp.initializeApp(this)
    }

    private fun showSearchOptionsDialog() {
        val dialogViewBinding = DialogSearchOptionsBinding.inflate(LayoutInflater.from(this))

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogViewBinding.root)
            .create()

        dialogViewBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.SearchByIngredientsRadioButton -> {
                    navController.navigate(R.id.searchByIngredientsFragment)
                    dialogBuilder.dismiss()
                }

                R.id.SearchByNutrientsRadioButton -> {
                    navController.navigate(R.id.searchByNutrientsFragment)
                    dialogBuilder.dismiss()
                }

                R.id.SearchByRecipeNameRadioButton -> {
                    navController.navigate(R.id.searchByNameFragment)
                    dialogBuilder.dismiss()
                }
            }
        }

        dialogBuilder.show()
    }

}