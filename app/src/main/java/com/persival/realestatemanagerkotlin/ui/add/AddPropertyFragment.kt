package com.persival.realestatemanagerkotlin.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentAddPropertyBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPropertyFragment : Fragment(R.layout.fragment_add_property) {

    companion object {
        fun newInstance() = AddPropertyFragment()
        private const val REQUEST_CODE_IMAGE1 = 1
        private const val REQUEST_CODE_IMAGE2 = 2
        private const val REQUEST_CODE_IMAGE3 = 3
        private const val REQUEST_CODE_IMAGE4 = 4
        private const val REQUEST_CODE_IMAGE5 = 5
        private const val REQUEST_CODE_IMAGE6 = 6
    }

    private val binding by viewBinding { FragmentAddPropertyBinding.bind(it) }
    private val viewModel by viewModels<AddPropertyViewModel>()

    private val imageUris = arrayOfNulls<Uri?>(6)
    private val requestCodes = arrayOf(
        REQUEST_CODE_IMAGE1,
        REQUEST_CODE_IMAGE2,
        REQUEST_CODE_IMAGE3,
        REQUEST_CODE_IMAGE4,
        REQUEST_CODE_IMAGE5,
        REQUEST_CODE_IMAGE6
    )
    private lateinit var imageViews: Array<ImageView>
    private lateinit var editTexts: Array<EditText>
    private lateinit var imageButtons: Array<ImageButton>

    private var currentRequestCode = 0

    private val openFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    requireContext().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    when (currentRequestCode) {
                        REQUEST_CODE_IMAGE1 -> setImageAndRequireName(it, REQUEST_CODE_IMAGE1)
                        REQUEST_CODE_IMAGE2 -> setImageAndRequireName(it, REQUEST_CODE_IMAGE2)
                        REQUEST_CODE_IMAGE3 -> setImageAndRequireName(it, REQUEST_CODE_IMAGE3)
                        REQUEST_CODE_IMAGE4 -> setImageAndRequireName(it, REQUEST_CODE_IMAGE4)
                        REQUEST_CODE_IMAGE5 -> setImageAndRequireName(it, REQUEST_CODE_IMAGE5)
                        REQUEST_CODE_IMAGE6 -> setImageAndRequireName(it, REQUEST_CODE_IMAGE6)
                    }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the arrays of views
        imageViews = arrayOf(
            binding.card1ImageView,
            binding.card2ImageView,
            binding.card3ImageView,
            binding.card4ImageView,
            binding.card5ImageView,
            binding.card6ImageView
        )
        editTexts = arrayOf(
            binding.card1EditText,
            binding.card2EditText,
            binding.card3EditText,
            binding.card4EditText,
            binding.card5EditText,
            binding.card6EditText
        )
        imageButtons = arrayOf(
            binding.card1ImageButton,
            binding.card2ImageButton,
            binding.card3ImageButton,
            binding.card4ImageButton,
            binding.card5ImageButton,
            binding.card6ImageButton
        )

        // Set the image selection callback for each ImageView
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                currentRequestCode = requestCodes[index]
                val openFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
                openFileResult.launch(openFileIntent)
            }
        }

        // Set the image remove callback for each remove button
        imageButtons.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                removeImageAndName(requestCodes[index])
            }
        }

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Get the property added by the user
        binding.okButton.setOnClickListener {

            val editTextList = listOf(
                binding.typeEditText,
                binding.addressEditText,
                binding.areaEditText,
                binding.roomsEditText,
                binding.bathroomsEditText,
                binding.bedroomsEditText,
                binding.descriptionEditText,
                binding.priceEditText,
                binding.datePickerEditText,
                binding.poiEditText
            )

            editTextList.forEach { editText ->
                if (editText.text?.isEmpty() == true) {
                    editText.error = getString(R.string.error_message)
                } else {
                    editText.error = null
                }
            }

            if (editTextList.all { it.text?.isEmpty() == false }) {
                val imageUrisList = imageUris.mapNotNull { uri -> uri?.toString() }
                val photoDescriptionsList = editTexts.mapNotNull { it.text?.toString() }

                viewModel.addNewProperty(
                    binding.typeEditText.text.toString(),
                    binding.addressEditText.text.toString(),
                    binding.areaEditText.text.toString().toInt(),
                    binding.roomsEditText.text.toString().toInt(),
                    binding.bathroomsEditText.text.toString().toInt(),
                    binding.bedroomsEditText.text.toString().toInt(),
                    binding.descriptionEditText.text.toString(),
                    binding.priceEditText.text.toString().toInt(),
                    binding.datePickerEditText.text.toString(),
                    binding.datePickerToSellText.text.toString(),
                    imageUrisList,
                    photoDescriptionsList
                )
            }

            imageUris.forEachIndexed { index, uri ->
                uri?.let {
                    Log.d("ImageUri", "Image ${index + 1}: $it")
                }
            }
        }
    }

    private fun setImageAndRequireName(uri: Uri, requestCode: Int) {
        val index = requestCodes.indexOf(requestCode)
        imageViews[index].setImageURI(uri)
        imageUris[index] = uri
        editTexts[index].text.clear()
        editTexts[index].requestFocus()
    }

    private fun removeImageAndName(requestCode: Int) {
        val index = requestCodes.indexOf(requestCode)
        imageViews[index].setImageResource(R.drawable.property_picture)
        imageUris[index] = null
        editTexts[index].text.clear()
    }
}
