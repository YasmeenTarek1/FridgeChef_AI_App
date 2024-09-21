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
        binding.lifecycleOwner = this

        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        // Hook up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Add a listener for destination changes to hide or show the BottomNavigationView
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.recipeStepsFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.GONE
                }
                R.id.recipeDetailsFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.GONE
                }
                R.id.loginFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.GONE
                }
                R.id.signUpFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.GONE
                }
                R.id.userInfoFragment -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = BottomNavigationView.VISIBLE
                }
            }
        }

        FirebaseApp.initializeApp(this)
    }
}