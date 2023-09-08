package com.persival.realestatemanagerkotlin.ui.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddPropertyFragment : Fragment(R.layout.fragment_add_property) {

    companion object {
        fun newInstance(): AddPropertyFragment {
            return AddPropertyFragment()
        }
    }

    private val binding by viewBinding { FragmentAddPropertyBinding.bind(it) }
    private val viewModel by viewModels<AddPropertyViewModel>()

    private val imageUris = arrayOfNulls<Uri?>(6)
    private val requestCodes = intArrayOf(1, 2, 3, 4, 5, 6)

    private var latLongString: String? = null
    private var currentRequestCode = 0
    private var currentPhotoUri: Uri? = null

    private lateinit var imageViews: Array<ImageView>
    private lateinit var editTexts: Array<EditText>
    private lateinit var imageButtons: Array<ImageButton>
    private lateinit var viewFinder: PreviewView
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val openFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    requireContext().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    setImageAndRequireName(it, currentRequestCode)
                }
            }
        }

    private val takePhotoResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val takenPhotoUri: Uri? = currentPhotoUri
                takenPhotoUri?.let {
                    setImageAndRequireName(it, currentRequestCode)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionType = arguments?.getString("ACTION_TYPE")

        // Initialize the type of property array
        val items = resources.getStringArray(R.array.property_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.typeTextView.setAdapter(adapter)

        // Initialize the Places API for Maps
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

        setupDatePicker(binding.datePickerEditText, binding.datePickerToSellText)

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

        viewFinder = binding.cameraPreview

        // Initialize the buttons for open camera
        setupImageButtonsForCamera()

        // Initialize the buttons for open gallery
        setupImageSelectionAndRemoval()

        // MODIFY PROPERTY - Complete form with property selected
        if (actionType == "modify") {
            viewModel.viewStateLiveData.observe(viewLifecycleOwner) { addViewState ->

                // Set the property information
                binding.typeTextView.setText(addViewState.type)
                binding.datePickerToSellText.setText(addViewState.soldAt)
                binding.datePickerEditText.setText(addViewState.availableFrom)
                binding.priceEditText.setText(addViewState.price.toString())
                binding.areaEditText.setText(addViewState.area.toString())
                binding.roomsEditText.setText(addViewState.rooms.toString())
                binding.bedroomsEditText.setText(addViewState.bedrooms.toString())
                binding.bathroomsEditText.setText(addViewState.bathrooms.toString())
                binding.descriptionEditText.setText(addViewState.description)
                binding.addressEditText.setText(addViewState.address)
                latLongString = addViewState.latLng

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
                            .override(800, 800)
                            .into(imageViews[index])
                    }
                }

            }
        }

        // Initialize the cancel button
        binding.cancelButton.setOnClickListener {
            requireActivity().finish()
        }

        // Get the property added or modified by the user
        binding.okButton.setOnClickListener {
            if (validateFields()) {
                val addViewState = retrieveFormData()

                if (actionType == "add") {
                    viewModel.addNewProperty(addViewState)
                } else if (actionType == "modify") {
                    viewModel.updateProperty(addViewState)
                }

                requireActivity().finish()
            }
        }

        // Initialize requestPermissionLauncher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewModel.refreshStoragePermission()
                    viewModel.refreshCameraPermission()
                }
            }


    }

    private fun setImageAndRequireName(uri: Uri, requestCode: Int) {
        val index = requestCodes.indexOf(requestCode)
        if (uri.toString().isNotEmpty() && uri != Uri.EMPTY) {
            imageViews[index].setImageURI(uri)
            imageUris[index] = uri
            editTexts[index].text.clear()
            editTexts[index].requestFocus()
        } else {
            Log.e("IMAGE_IMPORT", "Failed to set image from URI: $uri for requestCode: $requestCode")
        }
    }

    private fun removeImageAndName(requestCode: Int) {
        val index = requestCodes.indexOf(requestCode)
        imageViews[index].setImageResource(R.drawable.property_picture)
        imageUris[index] = null
        editTexts[index].text.clear()
    }

    private fun retrieveFormData(): AddViewState {

        // Retrieve photos and description
        val imageUrisList = imageUris.mapNotNull { uri -> uri?.toString() }
        val photoDescriptionsList = editTexts.mapNotNull { it.text?.toString() }

        // Retrieve poi selected
        val selectedChips = listOf(
            binding.schoolChip,
            binding.publicTransportChip,
            binding.hospitalChip,
            binding.shopChip,
            binding.greenSpacesChip,
            binding.restaurantChip
        ).filter { it.isChecked }.map { it.text.toString() }

        return AddViewState(
            binding.typeTextView.text.toString(),
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
            selectedChips.joinToString()
        )
    }

    private fun validateFields(): Boolean {
        val editTextList = listOf(
            binding.typeTextView,
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

        if (latLongString == null || latLongString == "") {
            Toast.makeText(requireContext(), R.string.invalid_address_message, Toast.LENGTH_LONG).show()
            return false
        }

        return allFieldsFilled
    }

    private fun setupImageSelectionAndRemoval() {
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                currentRequestCode = requestCodes[index]
                if (viewModel.hasCameraPermission().asLiveData().value == true &&
                    viewModel.hasStoragePermission().asLiveData().value == true
                ) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }

                    openFileResult.launch(intent)
                }
            }
        }

        imageButtons.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                removeImageAndName(requestCodes[index])
            }
        }
    }

    private fun setupImageButtonsForCamera() {
        val photoButtons = arrayOf(
            binding.card1PhotoButton,
            binding.card2PhotoButton,
            binding.card3PhotoButton,
            binding.card4PhotoButton,
            binding.card5PhotoButton,
            binding.card6PhotoButton
        )

        photoButtons.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {
                currentRequestCode = requestCodes[index]
                openCameraForPhoto()
            }
        }
    }

    private fun setupDatePicker(vararg editTexts: TextInputEditText) {
        editTexts.forEach { editText ->
            editText.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker().build()
                datePicker.addOnPositiveButtonClickListener { selection: Long? ->
                    Log.d("DATE_PICKER", "Selected timestamp: $selection")
                    val selectedDate: String? =
                        selection?.let { Date(it) }
                            ?.let { SimpleDateFormat(viewModel.getFormattedDate(), Locale.getDefault()).format(it) }
                    Log.d("DATE_PICKER", "Formatted date: $selectedDate")
                    editText.setText(selectedDate)
                }

                datePicker.show(parentFragmentManager, "date_picker_tag")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        // Refresh Storage and Camera permissions
        viewModel.refreshStoragePermission()
        viewModel.refreshCameraPermission()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        super.onResume()
    }

    private fun openCameraForPhoto() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile: File = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.persival.realestatemanagerkotlin.fileprovider",
                photoFile
            )
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            takePhotoResult.launch(takePhotoIntent)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoUri = Uri.fromFile(this)
        }
    }


}
