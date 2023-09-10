package com.persival.realestatemanagerkotlin.ui.search

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.RangeSlider
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentSearchBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Currency

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private val binding by viewBinding { FragmentSearchBinding.bind(it) }
    private val viewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enter the type of property
        val items = resources.getStringArray(R.array.property_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.typeTextView.setAdapter(adapter)

        // Set the price with seekbar
        binding.priceSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("USD")
            format.format(value.toDouble())
        }

        // Set the area with slider
        binding.areaSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 0
            format.format(value.toDouble())
        }

        // Initialize the toggle button
        binding.dateToggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.button1 -> {
                    if (isChecked) {
                        Toast.makeText(requireContext(), "Old first", Toast.LENGTH_SHORT).show()
                    }
                }

                R.id.button2 -> {
                    if (isChecked) {
                        Toast.makeText(requireContext(), "Recent first", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Initialize the cancel button
        binding.cancelButton.setOnClickListener {
            requireActivity().finish()
        }

        // Initialize the ok button for get filter
        binding.okButton.setOnClickListener {

            val typeValue = binding.typeTextView.text.toString()

            if (typeValue.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.type_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (minPrice, maxPrice) = getSliderValues(binding.priceSlider)
            val (minArea, maxArea) = getSliderValues(binding.areaSlider)

            val pois = getSelectedChips()
            val dateValue = getDateValue()

            val viewState = SearchViewState(
                type = typeValue,
                minPrice = minPrice.toInt(),
                maxPrice = maxPrice.toInt(),
                minArea = minArea.toInt(),
                maxArea = maxArea.toInt(),
                pois = pois,
                date = dateValue
            )

            viewModel.setSearchedProperties(viewState)

            requireActivity().finish()
        }
    }

    private fun getSliderValues(slider: RangeSlider): Pair<Float, Float> {
        return Pair(slider.values[0], slider.values[1])
    }

    private fun getSelectedChips(): String {
        val selectedChips = listOf(
            binding.schoolChip,
            binding.publicTransportChip,
            binding.hospitalChip,
            binding.shopChip,
            binding.greenSpacesChip,
            binding.restaurantChip
        ).filter { it.isChecked }.map { it.text.toString() }
        return selectedChips.joinToString()
    }

    private fun getDateValue(): String {
        return when (binding.dateToggleButton.checkedButtonId) {
            R.id.button1 -> "Old first"
            R.id.button2 -> "Recent first"
            else -> ""
        }
    }

}