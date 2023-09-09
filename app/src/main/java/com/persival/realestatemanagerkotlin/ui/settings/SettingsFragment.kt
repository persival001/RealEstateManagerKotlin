package com.persival.realestatemanagerkotlin.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentSettingsBinding
import com.persival.realestatemanagerkotlin.ui.loan_simulator.LoanSimulatorDialogFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val binding by viewBinding { FragmentSettingsBinding.bind(it) }
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Conversion button state
        binding.conversionRadioButton.isChecked = viewModel.conversionButtonState()

        binding.conversionRadioButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isCurrencyConversionTriggered(isChecked)
        }

        // Date button state
        binding.dateRadioButton.isChecked = viewModel.dateButtonState()

        binding.dateRadioButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isDateConversionTriggered(isChecked)
        }

        // Loan simulator button
        binding.loanSimulatorButton.setOnClickListener {
            val loanSimulatorDialog = LoanSimulatorDialogFragment()
            loanSimulatorDialog.show(childFragmentManager, "loanSimulatorDialogTag")
        }

    }

}