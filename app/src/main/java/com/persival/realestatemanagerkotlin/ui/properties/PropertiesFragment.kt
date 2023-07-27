package com.persival.realestatemanagerkotlin.ui.properties

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentPropertiesBinding
import com.persival.realestatemanagerkotlin.ui.detail.DetailFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertiesFragment : Fragment(R.layout.fragment_properties) {

    private lateinit var propertyListAdapter: PropertyListAdapter

    companion object {
        fun newInstance() = PropertiesFragment()
    }

    private val binding by viewBinding { FragmentPropertiesBinding.bind(it) }
    private val viewModel by viewModels<PropertiesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propertyListAdapter = PropertyListAdapter { property ->
            val detailFragment = DetailFragment.newInstance(property.id)
            val containerId = if (resources.getBoolean(R.bool.isTablet)) {
                R.id.main_FrameLayout_container_detail
            } else {
                R.id.main_FrameLayout_container_properties
            }

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(containerId, detailFragment)
                ?.addToBackStack(null)
                ?.commit()

        }

        binding.propertiesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.propertiesRecyclerView.adapter = propertyListAdapter

        binding.propertiesRecyclerView.post {
            val fakeProperties = generateFakeData()
            propertyListAdapter.submitList(fakeProperties)
        }
    }


    fun generateFakeData(): List<PropertyViewStateItem> {
        val fakeProperties = mutableListOf<PropertyViewStateItem>()

        for (i in 1..20) {
            val property = PropertyViewStateItem(
                id = i,
                type = "Property $i",
                address = "Address $i",
                price = "$${i}00,000",
                picture = R.drawable.property_picture,
                isSold = i % 2 == 0
            )
            fakeProperties.add(property)
        }

        return fakeProperties
    }
}
