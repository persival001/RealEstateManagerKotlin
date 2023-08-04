package com.persival.realestatemanagerkotlin.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentAddPropertyBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddPropertyFragment : Fragment(R.layout.fragment_add_property) {

    companion object {
        private const val ARG_PROPERTY_ID = "propertyId"
        fun newInstance(propertyId: Long) = AddPropertyFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_PROPERTY_ID, propertyId)
            }
        }

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

        val propertyId = arguments?.getLong(ARG_PROPERTY_ID)
        val items = resources.getStringArray(R.array.poi_items)
        val checkedItems = BooleanArray(items.size)
        var multiChoiceItemsSelected = ""

        // Initialize the POI button
        binding.poiButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_poi))
                .setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                    checkedItems[which] = isChecked
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    multiChoiceItemsSelected = items.withIndex()
                        .filter { (index, _) -> checkedItems[index] }
                        .joinToString(", ") { (_, item) -> item }
                    binding.poiButton.text = multiChoiceItemsSelected
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }

        // Initialize the date picker "for sale"
        val datePickerEditText: TextInputEditText = view.findViewById(R.id.date_picker_edit_text)
        datePickerEditText.setOnClickListener { showDatePicker(it) }

        // Initialize the date picker "sold"
        val datePickerSoldEditText: TextInputEditText =
            view.findViewById(R.id.date_picker_to_sell_text)
        datePickerSoldEditText.setOnClickListener { showDatePicker(it) }

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

        // Set the property information in the form if the propertyId is not null
        if (propertyId != null) {
            viewModel.getProperty(propertyId).observe(viewLifecycleOwner) { property ->
                binding.typeEditText.setText(property.property.type)
                binding.datePickerToSellText.setText(property.property.saleDate)
                binding.datePickerEditText.setText(property.property.entryDate)
                binding.priceEditText.setText(property.property.price.toString())
                binding.areaEditText.setText(property.property.area.toString())
                binding.roomsEditText.setText(property.property.rooms.toString())
                binding.bedroomsEditText.setText(property.property.bedrooms.toString())
                binding.bathroomsEditText.setText(property.property.bathrooms.toString())
                binding.descriptionEditText.setText(property.property.description)
                binding.addressEditText.setText(property.property.address)

                binding.card1EditText.setText(property.photos[0].description)
                binding.card2EditText.setText(property.photos[1].description)
                binding.card3EditText.setText(property.photos[2].description)
                binding.card4EditText.setText(property.photos[3].description)
                binding.card5EditText.setText(property.photos[4].description)
                binding.card6EditText.setText(property.photos[5].description)

                property.pointsOfInterest.forEach { poi ->
                    binding.poiButton.text = poi.poi
                }
            }
        }

        // Initialize the cancel button
        binding.cancelButton.setOnClickListener {
            requireActivity().finish()
        }

        // Get the property added by the user
        binding.okButton.setOnClickListener {
            if (validateFields()) {
                val addViewState = retrieveFormData()

                if (propertyId != null && propertyId != -1L) {
                    viewModel.updateProperty(propertyId, addViewState)
                } else {
                    viewModel.addNewProperty(addViewState)
                }

                // Return to main activity
                requireActivity().finish()
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

    private fun retrieveFormData(): AddViewState {
        val imageUrisList = imageUris.mapNotNull { uri -> uri?.toString() }
        val photoDescriptionsList = editTexts.mapNotNull { it.text?.toString() }

        return AddViewState(
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
            photoDescriptionsList,
            binding.poiButton.text.toString()
        )
    }

    private fun validateFields(): Boolean {
        val editTextList = listOf(
            binding.typeEditText,
            binding.addressEditText,
            binding.areaEditText,
            binding.roomsEditText,
            binding.bathroomsEditText,
            binding.bedroomsEditText,
            binding.descriptionEditText,
            binding.priceEditText,
            binding.datePickerEditText
        )

        var allFieldsFilled = true

        editTextList.forEach { editText ->
            if (editText.text?.isEmpty() == true) {
                editText.error = getString(R.string.error_message)
                allFieldsFilled = false
            } else {
                editText.error = null
            }
        }

        return allFieldsFilled
    }

    private fun showDatePicker(view: View) {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener { selection: Long? ->
            val selectedDate: String? =
                selection?.let { Date(it) }
                    ?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) }
            (view as TextInputEditText).setText(selectedDate)
        }
        datePicker.show(parentFragmentManager, "date_picker_tag")
    }
}
