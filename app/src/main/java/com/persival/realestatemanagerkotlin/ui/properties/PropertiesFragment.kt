package com.persival.realestatemanagerkotlin.ui.properties

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var sharedPreferenceListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.synchronizeDatabase()

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

        binding.propertiesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.propertiesRecyclerView.adapter = propertyListAdapter

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

        viewModel.showNotificationEvent.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                Toast.makeText(requireContext(), getString(R.string.property_added), Toast.LENGTH_SHORT).show()
                viewModel.showNotificationEvent.value = false
            }
        }

        sharedPreferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "KEY_CURRENCY") {
                viewModel.updatePropertyPrices()
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceListener)

    }

    private fun onPropertySelected(id: Long?) {
        viewModel.updateSelectedPropertyId(id)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceListener)
    }

}
