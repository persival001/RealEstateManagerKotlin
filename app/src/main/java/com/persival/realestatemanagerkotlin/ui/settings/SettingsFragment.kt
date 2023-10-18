package com.persival.realestatemanagerkotlin.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentSettingsBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val binding by viewBinding { FragmentSettingsBinding.bind(it) }
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe conversion button state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.conversionButtonState.collect { isChecked ->
                binding.conversionRadioButton.isChecked = isChecked
            }
        }

        // Set conversion button listener
        binding.conversionRadioButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isCurrencyConversionTriggered(isChecked)
        }

        // Observe date button state
        viewModel.dateButtonState.observe(viewLifecycleOwner) { isChecked ->
            binding.dateRadioButton.isChecked = isChecked
        }

        // Set date button listener
        binding.dateRadioButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isDateConversionTriggered(isChecked)
        }
    }
}
