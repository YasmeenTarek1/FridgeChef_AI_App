package com.example.recipeapp.ui.feedFragment

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.AppUser
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentFeedBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import com.example.recipeapp.sharedPreferences.SharedPreferences
import com.example.recipeapp.ui.chatBotServiceFragment.ChatBotServiceViewModel
import com.facebook.login.LoginManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import io.noties.markwon.Markwon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var repository: Repository
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter

    private var tooltipWindow: PopupWindow? = null
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        tooltipWindow?.dismiss()
    }
    private var isFetchingTip = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFeedBinding.bind(view)
        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = FeedViewModelFactory(repository , requireActivity().application)
        feedViewModel = ViewModelProvider(this, factory).get(FeedViewModel::class.java)

        feedAdapter = FeedAdapter(
            checkFavorite = { recipeId ->
                var isFavorite = false
                lifecycleScope.launch(Dispatchers.IO) {
                    isFavorite = feedViewModel.checkFavorite(recipeId)
                }
                isFavorite }
            ,
            onLoveClick = { recipe ->
                feedViewModel.onLoveClick(recipe)
            }
        )

        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.setAdapter(feedAdapter)

        ViewCompat.setTooltipText(binding.cookingTipButton, "Take a Cooking Tip")

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                // Show tooltip automatically when the fragment is first displayed
                showCustomTooltip(binding.cookingTipButton, "Take a Cooking Tip", 4500)
            }
        }, 100)


        val auth: FirebaseAuth = Firebase.auth
        binding.logOutButton.setOnClickListener{
            auth.signOut()
            AppUser.instance?.userId = null
            LoginManager.getInstance().logOut()

            lifecycleScope.launch(Dispatchers.IO) {
                feedViewModel.clearAllInfo()
            }

            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
        }

        // Custom Input
//        val staticData = listOf(
//            Recipe(id = 1, title = "Test Recipe 1", image = "", servings = 2, readyInMinutes = 30),
//            Recipe(id = 2, title = "Test Recipe 2", image = "", servings = 3, readyInMinutes = 25)
//        )
//        lifecycleScope.launch {
//            feedAdapter.submitData(PagingData.from(staticData))
//        }

        lifecycleScope.launch {
            // collectLatest -> take the latest emitted value and cancels any ongoing processing of previous emissions.
            feedViewModel.recipes.collectLatest { pagingData ->
                feedAdapter.submitData(pagingData)
            }
        }

        binding.cookingTipButton.setOnClickListener {
            showCookingTip()
        }

    }

    private fun showCookingTip() {
        val chatBotServiceViewModel = ChatBotServiceViewModel(repository , SharedPreferences(requireContext()))

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_cooking_tip, null)
        val tipTextView: TextView = dialogView.findViewById(R.id.cookingTipText)
        val gotItButton: Button = dialogView.findViewById(R.id.gotItButton)

        lifecycleScope.launch {
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()

            tipTextView.text = "Loading..."
            try {
                isFetchingTip = true
                val tip: String = chatBotServiceViewModel.getCookingTip()
                val markwon = Markwon.create(requireContext())
                markwon.setMarkdown(tipTextView, tip)
            } finally {
                isFetchingTip = false
            }

            gotItButton.setOnClickListener {
                if (!isFetchingTip)
                    dialog.dismiss()
            }
        }
    }


    fun showCustomTooltip(view: View, message: String, durationMillis: Long) {
        // Dismiss any existing tooltip if present
        tooltipWindow?.dismiss()

        val inflater = LayoutInflater.from(view.context)
        val tooltipView = inflater.inflate(R.layout.tooltip, null)
        val tooltipText = tooltipView.findViewById<TextView>(R.id.tooltip_text)
        tooltipText.text = message

        tooltipWindow = PopupWindow(tooltipView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        tooltipWindow?.isOutsideTouchable = true
        tooltipWindow?.isFocusable = true

        // Get the location of the view on screen
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val xOffset = -247 // Horizontal offset
        tooltipWindow?.showAtLocation(view, 0, location[0] + xOffset, location[1] + view.height)

        // Schedule dismissal of the tooltip
        handler.postDelayed(runnable, durationMillis)
    }

}
