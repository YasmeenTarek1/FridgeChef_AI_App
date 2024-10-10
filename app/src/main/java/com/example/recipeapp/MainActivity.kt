package com.example.recipeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.recipeapp.databinding.ActivityMainBinding
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
                R.id.searchCategoryFragment -> {
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
                R.id.searchCategoryFragment -> {
                    navController.navigate(R.id.searchCategoryFragment)
                    true
                }
                R.id.userProfileFragment -> {
                    navController.navigate(R.id.userProfileFragment)
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
}