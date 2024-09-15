package com.example.recipeapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.databinding.FragmentHomeBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.facebook.login.LoginManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        binding.lifecycleOwner = this


        auth = Firebase.auth
        binding.logOutButton.setOnClickListener{
            auth.signOut()
            AppUser.instance?.userId = null
            LoginManager.getInstance().logOut()

            lifecycleScope.launch {
                val db = AppDatabase.getInstance(requireContext())
                db!!.favoriteRecipesDao().clearAll()
                db.cookedRecipesDao().clearAll()
                db.toBuyIngredientsDao().clearAll()
            }

            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.feed.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
        }

        binding.search.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchCategoryFragment)
        }

        binding.cart.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_toBuyIngredientsFragment)
        }

        binding.profile.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
        }
    }
}