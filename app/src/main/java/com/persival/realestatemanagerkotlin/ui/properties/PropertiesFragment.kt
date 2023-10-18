package com.persival.realestatemanagerkotlin.ui.properties

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentPropertiesBinding
import com.persival.realestatemanagerkotlin.ui.detail.DetailFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertiesFragment : Fragment(R.layout.fragment_properties) {

    companion object {
        fun newInstance() = PropertiesFragment()
    }

    private val binding by viewBinding { FragmentPropertiesBinding.bind(it) }
    private val viewModel by viewModels<PropertiesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Synchronize firebase and room
        viewModel.synchronizeDatabase()

        // Set the listener to capture user query input
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Call viewModel method even if newText is empty or null
                viewModel.combineFiltersWithProperties(
                    newText ?: "", areaSearch = false, roomSearch = false, priceSearch = false,
                    soldSearch = false
                )
                return true
            }
        })

        // Initialize chip listeners
        binding.areaChip.setOnCheckedChangeListener { _, _ ->
            updateFiltersAndSearch(binding.searchView.query.toString())
        }
        binding.roomsChip.setOnCheckedChangeListener { _, _ ->
            updateFiltersAndSearch(binding.searchView.query.toString())
        }
        binding.priceChip.setOnCheckedChangeListener { _, _ ->
            updateFiltersAndSearch(binding.searchView.query.toString())
        }
        binding.soldChip.setOnCheckedChangeListener { _, _ ->
            updateFiltersAndSearch(binding.searchView.query.toString())
        }

        // Initializes PropertyListAdapter to handle property selection
        val propertyListAdapter = PropertyListAdapter { property ->
            onPropertySelected(property.id)
            val detailFragment = DetailFragment.newInstance()
            val containerId = if (resources.getBoolean(R.bool.isTablet)) {
                R.id.main_FrameLayout_container_detail
            } else {
                R.id.main_FrameLayout_container_properties
            }

            requireActivity().supportFragmentManager.commit {
                replace(containerId, detailFragment)
                addToBackStack(null)
            }
        }

        binding.propertiesRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            binding.propertiesRecyclerView.adapter = propertyListAdapter
        }

        viewModel.properties.observe(viewLifecycleOwner) { properties ->
            propertyListAdapter.submitList(properties)

            if (properties.isNullOrEmpty()) {
                binding.propertiesRecyclerView.visibility = View.GONE
                binding.emptyTextView.visibility = View.VISIBLE
            } else {
                binding.propertiesRecyclerView.visibility = View.VISIBLE
                binding.emptyTextView.visibility = View.GONE
            }
        }

    }

    // Update the ViewModel based on the text and chip states
    private fun updateFiltersAndSearch(query: String) {
        val areaSearch = binding.areaChip.isChecked
        val roomSearch = binding.roomsChip.isChecked
        val priceSearch = binding.priceChip.isChecked
        val soldSearch = binding.soldChip.isChecked

        // Combine all filter info and query text
        viewModel.combineFiltersWithProperties(query, areaSearch, roomSearch, priceSearch, soldSearch)
    }

    private fun onPropertySelected(id: Long?) {
        viewModel.updateSelectedPropertyId(id)
    }

}
