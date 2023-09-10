package com.persival.realestatemanagerkotlin.ui.loan_simulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentLoanSimulatorBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import java.util.Currency
import java.util.Locale

class LoanSimulatorDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_PROPERTY_PRICE = "property_price"

        fun newInstance(price: Double): LoanSimulatorDialogFragment {
            val args = Bundle()
            args.putDouble(ARG_PROPERTY_PRICE, price)
            val fragment = LoanSimulatorDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val binding by viewBinding { FragmentLoanSimulatorBinding.bind(it) }
    private val viewModel by viewModels<LoanSimulatorViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loan_simulator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val propertyPrice = arguments?.getDouble(ARG_PROPERTY_PRICE) ?: 0.0

        binding.calculateButton.setOnClickListener {
            val bring = binding.bringEditText.text.toString().toDoubleOrNull() ?: 0.0
            val rate = binding.rateEditText.text.toString().toDoubleOrNull() ?: 0.0
            val duration = binding.durationEditText.text.toString().toIntOrNull() ?: 0

            viewModel.calculateMonthlyPayment(bring, rate, duration, propertyPrice)
        }

        viewModel.loanState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoanCalculationState.Success -> {
                    val currencySymbol = Currency.getInstance(Locale.getDefault()).symbol
                    binding.resultTextView.text =
                        getString(R.string.monthly_payment_text, currencySymbol, state.monthlyPayment)
                }

                LoanCalculationState.InvalidDuration -> {
                    binding.resultTextView.text = getString(R.string.error_message_duration)
                }

                LoanCalculationState.Error -> {
                    Toast.makeText(context, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}
