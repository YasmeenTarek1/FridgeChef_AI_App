package com.example.recipeapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
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
                    // Create NavOptions with launchSingleTop
                    val navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true) // Prevents reloading if already on feedFragment
                        .build()

                    // Navigate only if the current destination is not the feedFragment
                    if (navController.currentDestination?.id != R.id.feedFragment) {
                        navController.navigate(R.id.feedFragment, null, navOptions)
                    }
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
                R.id.searchFragment -> {
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestination = navController.currentDestination?.id
                when (currentDestination) {
                    R.id.recipeDetailsFragment -> {
                        navController.navigate(R.id.feedFragment)
                    }
                    R.id.feedFragment -> {
                        finish()
                    }
                    else -> {
                        // Pop the back stack
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        })

        FirebaseApp.initializeApp(this)
    }

    private fun showSearchOptionsDialog() {
        val dialogViewBinding = DialogSearchOptionsBinding.inflate(LayoutInflater.from(this))

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogViewBinding.root)
            .create()
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogBuilder.setOnShowListener {
            dialogBuilder.window?.decorView?.translationX = 60f // Center X, adjust if needed
        }

        var checked:Int = -1
        dialogViewBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.SearchByIngredientsRadioButton -> {
                    checked = checkedId
                    navController.navigate(R.id.searchByIngredientsFragment)
                    dialogBuilder.dismiss()
                }

                R.id.SearchByNutrientsRadioButton -> {
                    checked = checkedId
                    navController.navigate(R.id.searchByNutrientsFragment)
                    dialogBuilder.dismiss()
                }

                R.id.SearchByRecipeNameRadioButton -> {
                    checked = checkedId
                    navController.navigate(R.id.searchByNameFragment)
                    dialogBuilder.dismiss()
                }
            }
        }

        dialogBuilder.setOnDismissListener {
            if(checked == -1){
                binding.bottomNavigation.selectedItemId = R.id.feedFragment
            }
        }

        dialogBuilder.show()
    }
}