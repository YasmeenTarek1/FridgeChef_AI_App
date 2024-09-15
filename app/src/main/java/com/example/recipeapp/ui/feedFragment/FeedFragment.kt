package com.example.recipeapp.ui.feedFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.Repository
import com.example.recipeapp.api.service.RetrofitInstance
import com.example.recipeapp.databinding.FragmentFeedBinding
import com.example.recipeapp.room_DB.database.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController


class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var repository: Repository
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFeedBinding.bind(view)
        binding.lifecycleOwner = this

        repository = Repository(RetrofitInstance(), AppDatabase.getInstance(requireContext()))

        val factory = FeedViewModelFactory(requireActivity().application , repository)
        feedViewModel = ViewModelProvider(this, factory).get(FeedViewModel::class.java)

        feedAdapter = FeedAdapter(onLoveClick = { recipe ->
            feedViewModel.onLoveClick(recipe)
        })

        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setAdapter(feedAdapter)

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
            val action = FeedFragmentDirections.actionFeedFragmentToChatBotFragment(null , null , 1)
            findNavController().navigate(action)
        }

    }
}