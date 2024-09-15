package com.example.recipeapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.databinding.FragmentSignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)
        binding.lifecycleOwner = this

        auth = Firebase.auth
        binding.SignUpButton.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val email = binding.EmailEditText.text.toString()
        val password = binding.PasswordEditText.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                AppUser.instance!!.userId = auth.currentUser?.uid
                findNavController().navigate(R.id.action_signUpFragment_to_userInfoFragment)
            } else {
                Log.w("TAGY", "createUserWithEmail:failure", task.exception)
                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}