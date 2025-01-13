package com.example.fridgeChefAIApp.ui.specialRecipesFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridgeChefAIApp.R
import com.example.fridgeChefAIApp.databinding.FragmentSpecialRecipesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpecialRecipesFragment : Fragment(R.layout.fragment_special_recipes) {

    private lateinit var binding: FragmentSpecialRecipesBinding
    private val viewModel : SpecialRecipesViewModel by viewModels()
    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var recyclerView3: RecyclerView
    private lateinit var cookedAdapter: CookedRecipesAdapter
    private lateinit var favAdapter: FavRecipesAdapter
    private lateinit var aiAdapter: ChatBotAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSpecialRecipesBinding.bind(view)
        cookedAdapter = CookedRecipesAdapter()
        favAdapter = FavRecipesAdapter()
        aiAdapter = ChatBotAdapter()

        recyclerView1 = binding.recyclerView1
        recyclerView1.setHasFixedSize(true)
        recyclerView1.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.adapter = aiAdapter

        recyclerView2 = binding.recyclerView2
        recyclerView2.setHasFixedSize(true)
        recyclerView2.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.adapter = favAdapter

        recyclerView3 = binding.recyclerView3
        recyclerView3.setHasFixedSize(true)
        recyclerView3.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView3.adapter = cookedAdapter

        binding.viewAll1.setOnClickListener {
            val action =
                SpecialRecipesFragmentDirections.actionSpecialRecipesFragmentToChatBotRecipesFragment()
            findNavController().navigate(action)
        }

        binding.viewAll2.setOnClickListener {
            val action =
                SpecialRecipesFragmentDirections.actionSpecialRecipesFragmentToFavoriteRecipesFragment()
            findNavController().navigate(action)
        }

        binding.viewAll3.setOnClickListener {
            val action =
                SpecialRecipesFragmentDirections.actionSpecialRecipesFragmentToCookedRecipesFragment()
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            viewModel.aiRecipes.collect { aiRecipes ->
                aiAdapter.differ.submitList(aiRecipes)
                binding.emptyText1.visibility = if (aiRecipes.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            viewModel.favRecipes.collect { favRecipes ->
                favAdapter.differ.submitList(favRecipes)
                binding.emptyText2.visibility = if (favRecipes.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            viewModel.cookedRecipes.collect { cookedRecipes ->
                cookedAdapter.differ.submitList(cookedRecipes)
                binding.emptyText3.visibility = if (cookedRecipes.isEmpty()) View.VISIBLE else View.GONE
            }
        }

    }
}