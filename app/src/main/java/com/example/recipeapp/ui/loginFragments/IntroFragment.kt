package com.example.recipeapp.ui.loginFragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentIntroBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class IntroFragment : Fragment(R.layout.fragment_intro) {

    private lateinit var binding: FragmentIntroBinding
    private lateinit var repository: Repository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIntroBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        if (FirebaseAuth.getInstance().currentUser != null) {
            lifecycleScope.launch{
                AppUser.instance!!.userId = FirebaseAuth.getInstance().currentUser!!.uid
                findNavController().navigate(R.id.action_introFragment_to_feedFragment)
                FetchingViewModel(repository).fetchUserInfoFromFirestoreAndInitializeRoom()
            }
        }

        binding.getStartedTV.setOnClickListener{
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
        binding.getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_loginFragment)
        }
    }
}