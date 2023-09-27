package com.persival.realestatemanagerkotlin.ui.gps_dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.persival.realestatemanagerkotlin.databinding.DialogGpsBinding

class GpsDialogFragment : DialogFragment() {

    private lateinit var binding: DialogGpsBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        binding = DialogGpsBinding.inflate(requireActivity().layoutInflater)

        builder.setView(binding.root)

        binding.dialogGpsCancel.setOnClickListener {
            dismiss()
        }
        binding.dialogGpsActivate.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            dismiss()
        }

        return builder.create()
    }
}
