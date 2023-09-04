package com.persival.realestatemanagerkotlin.ui.search

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentSearchBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Currency

// TODO BottomSheetFragment instead ! Sexier
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
        binding.dateToggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
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

        // Get the property added or modified by the user
        binding.okButton.setOnClickListener {

            // Get type value
            binding.typeTextView.text.toString()

            if (binding.typeTextView.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.type_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get price values
            val priceValues = binding.priceSlider.values
            val minPriceValue = priceValues[0]
            val maxPriceValue = priceValues[1]

            // Get area values
            val areaValues = binding.areaSlider.values
            val minAreaValue = areaValues[0]
            val maxAreaValue = areaValues[1]

            // Get poi selected
            val selectedChips = listOf(
                binding.schoolChip,
                binding.publicTransportChip,
                binding.hospitalChip,
                binding.shopChip,
                binding.greenSpacesChip,
                binding.restaurantChip
            ).filter { it.isChecked }.map { it.text.toString() }
            val selectedChipsString = selectedChips.joinToString()

            // Get date filter (older first or newer first)
            val selectedButtonId = binding.dateToggleButton.checkedButtonId

            val isButton1Checked = (selectedButtonId == R.id.button1)
            val isButton2Checked = (selectedButtonId == R.id.button2)

            //requireActivity().finish()
        }
    }

}


