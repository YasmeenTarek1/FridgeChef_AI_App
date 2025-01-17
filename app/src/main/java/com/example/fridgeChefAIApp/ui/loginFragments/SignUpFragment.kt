package com.example.fridgeChefAIApp.ui.loginFragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fridgeChefAIApp.AppUser
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentSignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)

        auth = Firebase.auth
        binding.signUpButton.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val username = binding.usernameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                lifecycleScope.launch {
                    AppUser.instance!!.userId = auth.currentUser?.uid
                    findNavController().navigate(R.id.action_signUpFragment_to_userInfoFragment)
                }
            } else {
                Log.w("SignUp Error", "createUserWithEmail:failure", task.exception)
                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}