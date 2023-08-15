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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.persival.realestatemanagerkotlin.BuildConfig.MAPS_API_KEY
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
        const val ARG_PROPERTY_ID = "propertyId"
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

    private var latLongString: String? = null
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
        Log.d("AddPropertyFragment", "Retrieved Property ID: $propertyId")
        val items = resources.getStringArray(R.array.poi_items)
        val checkedItems = BooleanArray(items.size)
        var multiChoiceItemsSelected: String

        // Initialize the Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), MAPS_API_KEY)
        }

        // Google Places UI for address autocompletion
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        val autocompleteResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        val place = Autocomplete.getPlaceFromIntent(result.data!!)
                        binding.addressEditText.setText(place.address)
                        place.latLng?.let { latLng ->
                            latLongString = "${latLng.latitude},${latLng.longitude}"
                        }
                    }

                    AutocompleteActivity.RESULT_ERROR -> {
                        val status = Autocomplete.getStatusFromIntent(result.data!!)
                        Toast.makeText(requireContext(), status.statusMessage, Toast.LENGTH_LONG).show()
                    }

                    Activity.RESULT_CANCELED -> {
                        binding.addressEditText.setText("")
                    }
                }
            }

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.ADDRESS)
            .setCountry("US")
            .build(requireContext())
        binding.addressEditText.setOnClickListener {
            autocompleteResultLauncher.launch(intent)
        }

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
            viewModel.viewStateLiveData.observe(viewLifecycleOwner) { addViewState ->

                // Set the property information
                binding.typeEditText.setText(addViewState.type)
                binding.datePickerToSellText.setText(addViewState.soldAt)
                binding.datePickerEditText.setText(addViewState.availableFrom)
                binding.priceEditText.setText(addViewState.price.toString())
                binding.areaEditText.setText(addViewState.area.toString())
                binding.roomsEditText.setText(addViewState.rooms.toString())
                binding.bedroomsEditText.setText(addViewState.bedrooms.toString())
                binding.bathroomsEditText.setText(addViewState.bathrooms.toString())
                binding.descriptionEditText.setText(addViewState.description)
                binding.addressEditText.setText(addViewState.address)

                // Set the card photos and descriptions
                val cardEditTexts = listOf(
                    binding.card1EditText,
                    binding.card2EditText,
                    binding.card3EditText,
                    binding.card4EditText,
                    binding.card5EditText,
                    binding.card6EditText
                )

                cardEditTexts.forEachIndexed { index, editText ->
                    addViewState.photoDescriptions.getOrNull(index)?.let { editText.setText(it) }
                }

                val imageViews = listOf(
                    binding.card1ImageView,
                    binding.card2ImageView,
                    binding.card3ImageView,
                    binding.card4ImageView,
                    binding.card5ImageView,
                    binding.card6ImageView
                )

                addViewState.photoUris.forEachIndexed { index, photoUrl ->
                    if (index < imageViews.size) {
                        Glide.with(imageViews[index])
                            .load(photoUrl)
                            .into(imageViews[index])
                    }
                }

                // Set the POI button text
                if (addViewState.pointsOfInterest.isNotEmpty()) {
                    binding.poiButton.text = addViewState.pointsOfInterest
                } else {
                    binding.poiButton.text = getString(R.string.select_poi)
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
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.property_modified),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.addNewProperty(addViewState)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.property_added),
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
            latLongString ?: "",
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
