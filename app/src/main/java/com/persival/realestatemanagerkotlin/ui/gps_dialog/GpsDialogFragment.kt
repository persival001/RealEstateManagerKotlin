package com.persival.realestatemanagerkotlin.ui.gps_dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.persival.realestatemanagerkotlin.R

class GpsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_gps, null)
        builder.setView(view)
        view.findViewById<View>(R.id.dialog_gps_cancel).setOnClickListener { v: View? -> dismiss() }
        view.findViewById<View>(R.id.dialog_gps_activate).setOnClickListener { v: View? ->
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            dismiss()
        }
        return builder.create()
    }
}