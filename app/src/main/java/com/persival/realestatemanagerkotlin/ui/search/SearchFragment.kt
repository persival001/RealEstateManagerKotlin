package com.persival.realestatemanagerkotlin.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentSearchBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private val binding by viewBinding { FragmentSearchBinding.bind(it) }
    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the type of property array
        val items = resources.getStringArray(R.array.property_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.typeTextView.setAdapter(adapter)

        // Reinitialize the list
        binding.clearButton.setOnClickListener {
            // Clear the form
        }

        // Get the information's of property added by the user
        binding.okButton.setOnClickListener {
            // Check if the form is valid
            requireActivity().finish()
        }

        // Retrieve chip selected for poi
        val selectedPoisChips = listOf(
            binding.schoolChip,
            binding.publicTransportChip,
            binding.hospitalChip,
            binding.shopChip,
            binding.greenSpacesChip,
            binding.restaurantChip
        ).filter { it.isChecked }.map { it.text.toString() }

        // Retrieve chip selected for time of sale
        binding.weekChip
        binding.monthChip
        binding.yearChip


        val mandatoryEditTextList = listOf(
            binding.typeTextView,
            binding.priceMinEditText,
            binding.priceMaxEditText,
            binding.areaMinEditText,
            binding.areaMaxEditText,
        )

        // Check if all mandatory fields are filled and set error messages
        val allMandatoryFieldsFilled = mandatoryEditTextList.all { editText ->
            val isFilled = editText.text?.isNotEmpty() == true
            editText.error = if (isFilled) null else getString(R.string.error_message)
            isFilled
        }
    }
}

