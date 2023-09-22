package com.persival.realestatemanagerkotlin.ui.add_picture_dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.databinding.DialogAddPictureBinding

class AddPictureDialogFragment : DialogFragment() {

    var onDescriptionEntered: ((String) -> Unit)? = null

    private lateinit var binding: DialogAddPictureBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val imageUri = Uri.parse(arguments?.getString("image_uri"))

        binding = DialogAddPictureBinding.inflate(requireActivity().layoutInflater)

        builder.setView(binding.root)

        Glide.with(binding.dialogImage)
            .load(imageUri)
            .override(800, 800)
            .into(binding.dialogImage)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.okButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            onDescriptionEntered?.invoke(description)
            dismiss()
        }

        return builder.create()
    }
}
