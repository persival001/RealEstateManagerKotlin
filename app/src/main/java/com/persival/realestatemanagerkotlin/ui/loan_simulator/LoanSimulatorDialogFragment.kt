package com.persival.realestatemanagerkotlin.ui.loan_simulator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.persival.realestatemanagerkotlin.R

class LoanSimulatorDialogFragment : DialogFragment() {

    private lateinit var apportEditText: EditText
    private lateinit var tauxEditText: EditText
    private lateinit var dureeEditText: EditText
    private lateinit var resultTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loan_simulator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apportEditText = view.findViewById(R.id.apportEditText)
        tauxEditText = view.findViewById(R.id.tauxEditText)
        dureeEditText = view.findViewById(R.id.dureeEditText)
        resultTextView = view.findViewById(R.id.resultTextView)

        view.findViewById<Button>(R.id.calculateButton).setOnClickListener {
            calculateLoan()
        }
    }

    private fun calculateLoan() {
        val apport = apportEditText.text.toString().toDoubleOrNull() ?: 0.0
        val taux = tauxEditText.text.toString().toDoubleOrNull() ?: 0.0
        val duree = dureeEditText.text.toString().toIntOrNull() ?: 0

        if (duree == 0) {
            resultTextView.text = "Résultat: Durée ne peut pas être zéro!"
            return
        }

        val monthlyRate = taux / 12 / 100
        val monthlyPayment = apport * monthlyRate / (1 - Math.pow((1 + monthlyRate).toDouble(), -duree.toDouble()))

        resultTextView.text = "Résultat: Paiement mensuel = €${String.format("%.2f", monthlyPayment)}"
    }
}
