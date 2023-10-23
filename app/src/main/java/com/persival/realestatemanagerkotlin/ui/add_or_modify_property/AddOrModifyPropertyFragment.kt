package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentAddPropertyBinding
import com.persival.realestatemanagerkotlin.ui.add_picture_dialog.AddPictureDialogFragment
import com.persival.realestatemanagerkotlin.utils.Utils
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class AddOrModifyPropertyFragment : Fragment(R.layout.fragment_add_property) {

    companion object {
        const val ACTION_TYPE_KEY = "actionType"
        const val ACTION_ADD = "add"
        const val ACTION_MODIFY = "modify"

        fun newInstance() = AddOrModifyPropertyFragment()
    }

    private val binding by viewBinding { FragmentAddPropertyBinding.bind(it) }
    private val viewModel by viewModels<AddOrModifyPropertyViewModel>()

    private var latLongString: String? = null
    private var currentPhotoUri: Uri? = null
    private var areImagesPresent = false
    private var actionType: String? = null

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var addOrModifyPropertyListAdapter: AddOrModifyPropertyListAdapter

    private val openFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    requireContext().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    setImageUri(it)
                }
            }
        }

    private val takePhotoResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val takenPhotoUri: Uri? = currentPhotoUri
                takenPhotoUri?.let {
                    setImageUri(it)
                }
            }
        }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Action type is used to determine which actions must be performed
        // depending on whether add or modify has been selected.
        actionType = arguments?.getString(ACTION_TYPE_KEY)

        when (actionType) {
            ACTION_ADD -> {
                Toast.makeText(requireContext(), "Selected: ADD", Toast.LENGTH_SHORT).show()
            }

            ACTION_MODIFY -> {
                // Complete form with information's of property selected
                Toast.makeText(requireContext(), "Selected: MODIFY", Toast.LENGTH_SHORT).show()
                displaysPropertyInformation()
                displaysPropertyPhotos()
            }
        }

        // Internet connexion define if button or edit text is visible
        val isInternetAvailable = Utils.isConnexionAvailable(requireContext())
        if (isInternetAvailable) {
            binding.addressButton.visibility = View.VISIBLE
            binding.addressTextField.visibility = View.INVISIBLE
        } else {
            binding.addressButton.visibility = View.INVISIBLE
            binding.addressTextField.visibility = View.VISIBLE
        }

        // Initialize requestPermissionLauncher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewModel.refreshStoragePermission()
                    viewModel.refreshCameraPermission()
                }
            }

        // Initialize adapter and set it for horizontal view
        addOrModifyPropertyListAdapter = AddOrModifyPropertyListAdapter()
        binding.addPhotoRecyclerView.adapter = addOrModifyPropertyListAdapter
        val horizontalLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.addPhotoRecyclerView.layoutManager = horizontalLayoutManager

        // Initialize the type of property array
        val items = resources.getStringArray(R.array.property_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.typeTextView.setAdapter(adapter)

        // Google Places UI for address autocompletion
        viewModel.initializePlaces(requireContext())

        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.ADDRESS)
            .setCountry("US")
            .build(requireContext())

        viewModel.address.observe(viewLifecycleOwner) { newAddress ->
            binding.addressButton.text = newAddress ?: getString(R.string.property_address)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        val autocompleteResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                viewModel.handleActivityResult(result.resultCode, result.data)
            }

        binding.addressButton.setOnClickListener {
            autocompleteResultLauncher.launch(intent)
        }

        // Initialize the date picker
        setupDatePicker(binding.datePickerEditText, binding.datePickerToSellText)

        // Open the picture picker
        binding.importButton.setOnClickListener {
            setupImageSelection()
        }

        // Open the camera
        binding.cameraButton.setOnClickListener {
            openCameraForTakeAPicture()
        }

        // Filled the recycle view with the list of added photos
        displaysAddedPropertyPhotos()

        viewModel.latLong.observe(viewLifecycleOwner) { latLng ->
            if (latLng != null) {
                latLongString = latLng
            }
        }

        // Notify user when the property is successfully added in room database
        viewModel.propertyAddStatus.observe(viewLifecycleOwner) { newId ->
            if (newId != null && newId > 0) {
                Toast.makeText(requireContext(), getString(R.string.property_added), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.property_not_added), Toast.LENGTH_SHORT).show()
            }
        }

        // Quit the fragment
        binding.cancelButton.setOnClickListener {
            requireActivity().finish()
        }

        // Get the information's of property added by the user
        binding.okButton.setOnClickListener {
            if (validateFields()) {
                val addViewState = retrieveFormData(isInternetAvailable)

                when (actionType) {
                    ACTION_ADD -> viewModel.addNewProperty(addViewState)
                    ACTION_MODIFY -> viewModel.updateProperty(addViewState)
                    else -> return@setOnClickListener
                }

                requireActivity().finish()
            }
        }

    }

    // Display the photos added to the list
    private fun displaysAddedPropertyPhotos() {
        viewModel.addViewStateItemList.asLiveData().observe(viewLifecycleOwner) { viewStateItemList ->
            addOrModifyPropertyListAdapter.updateList(viewStateItemList)
            areImagesPresent = viewStateItemList.isNotEmpty()
        }
    }

    private fun displaysPropertyPhotos() {
        viewModel.viewStateItemList.asLiveData().observe(viewLifecycleOwner) { viewStateItemList ->
            addOrModifyPropertyListAdapter.updateList(viewStateItemList)
            areImagesPresent = viewStateItemList.isNotEmpty()
        }
    }

    private fun retrieveFormData(isInternetAvailable: Boolean): AddOrModifyPropertyViewState {
        // Retrieve poi selected
        val selectedChips = listOf(
            binding.schoolChip,
            binding.publicTransportChip,
            binding.hospitalChip,
            binding.shopChip,
            binding.greenSpacesChip,
            binding.restaurantChip
        ).filter { it.isChecked }.map { it.text.toString() }

        val address = if (isInternetAvailable) {
            binding.addressButton.text.toString()
        } else {
            binding.addressEditText.text.toString()
        }

        return AddOrModifyPropertyViewState(
            binding.typeTextView.text.toString(),
            address,
            latLongString ?: "",
            binding.areaEditText.text.toString().toInt(),
            binding.roomsEditText.text.toString().toInt(),
            binding.bathroomsEditText.text.toString().toInt(),
            binding.bedroomsEditText.text.toString().toInt(),
            binding.descriptionEditText.text.toString(),
            binding.priceEditText.text.toString().toInt(),
            binding.datePickerEditText.text.toString(),
            binding.datePickerToSellText.text.toString(),
            selectedChips.joinToString()
        )
    }

    private fun validateFields(): Boolean {
        val isInternetAvailable = Utils.isConnexionAvailable(requireContext())

        val mandatoryEditTextList = listOf(
            binding.typeTextView,
            binding.areaEditText,
            binding.roomsEditText,
            binding.bathroomsEditText,
            binding.bedroomsEditText,
            binding.descriptionEditText,
            binding.priceEditText,
            binding.datePickerEditText
        )

        // Check if all mandatory fields are filled and set error messages
        val allMandatoryFieldsFilled = mandatoryEditTextList.all { editText ->
            val isFilled = editText.text?.isNotEmpty() == true
            editText.error = if (isFilled) null else getString(R.string.error_message)
            isFilled
        }

        val isOptionalFieldsValid: Boolean

        // Conditionally validate address and latLongString based on Internet availability
        if (isInternetAvailable) {
            val isLatLongValid = !latLongString.isNullOrBlank()
            val isAddressFilled = binding.addressButton.text?.isNotEmpty() == true

            if (!isLatLongValid) {
                binding.addressButton.error = getString(R.string.invalid_address_message)
            }

            isOptionalFieldsValid = isLatLongValid && isAddressFilled

        } else {
            // If no Internet, allow manual address input and skip latLongString validation
            val isAddressFilled = binding.addressEditText.text?.isNotEmpty() == true
            binding.addressEditText.error = if (isAddressFilled) null else getString(R.string.error_message)
            isOptionalFieldsValid = isAddressFilled
        }

        // Check if at least one photo is filled
        if (!areImagesPresent) {
            Toast.makeText(requireContext(), getString(R.string.photo_required), Toast.LENGTH_SHORT).show()
        }

        return allMandatoryFieldsFilled && isOptionalFieldsValid && areImagesPresent
    }

    // (MODIFY) Displays the property information to modify
    private fun displaysPropertyInformation() {
        viewModel.viewStateFlow.asLiveData().observe(viewLifecycleOwner) { modifyViewState ->

            // Fill in the fields for the property
            binding.typeTextView.setText(modifyViewState.type)
            binding.datePickerToSellText.setText(modifyViewState.soldAt)
            binding.datePickerEditText.setText(modifyViewState.availableFrom)
            binding.priceEditText.setText(modifyViewState.price.toString())
            binding.areaEditText.setText(modifyViewState.area.toString())
            binding.roomsEditText.setText(modifyViewState.rooms.toString())
            binding.bedroomsEditText.setText(modifyViewState.bedrooms.toString())
            binding.bathroomsEditText.setText(modifyViewState.bathrooms.toString())
            binding.descriptionEditText.setText(modifyViewState.description)
            binding.addressEditText.setText(modifyViewState.address)
            latLongString = modifyViewState.latLng

            // Fill in the chips for the poi's
            val chips = listOf(
                binding.schoolChip,
                binding.publicTransportChip,
                binding.hospitalChip,
                binding.shopChip,
                binding.greenSpacesChip,
                binding.restaurantChip
            )
            for (chip in chips) {
                chip.isChecked = modifyViewState.pointsOfInterest.contains(chip.text, ignoreCase = true)
            }

        }
    }

    private fun setupDatePicker(vararg editTexts: TextInputEditText) {
        editTexts.forEach { editText ->
            editText.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker().build()
                datePicker.addOnPositiveButtonClickListener { selection: Long? ->
                    val selectedDate = selection?.let { Date(it) }
                    viewLifecycleOwner.lifecycleScope.launch {
                        val formattedDate = selectedDate?.let { viewModel.getFormattedDate(it) }
                        editText.setText(formattedDate)
                    }
                }

                datePicker.show(parentFragmentManager, "date_picker_tag")
            }
        }
    }

    private fun setupImageSelection() {

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

    private fun openCameraForTakeAPicture() {
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

    private fun setImageDescription(uri: Uri, onDescriptionEntered: (String) -> Unit) {
        val dialogFragment = AddPictureDialogFragment().apply {
            arguments = Bundle().apply {
                putString("image_uri", uri.toString())
            }
        }
        dialogFragment.onDescriptionEntered = onDescriptionEntered
        dialogFragment.show(requireActivity().supportFragmentManager, "AddPictureDialogFragment")
    }

    private fun setImageUri(uri: Uri) {
        if (uri.toString().isEmpty() || uri == Uri.EMPTY) {
            Log.e("IMAGE_IMPORT", "Failed to set image from URI: $uri")
            return
        }

        setImageDescription(uri) { description ->
            if (description.isEmpty()) {
                Toast.makeText(context, getString(R.string.not_empty), Toast.LENGTH_SHORT).show()
                return@setImageDescription
            }
            when (actionType) {
                ACTION_ADD -> viewModel.addImageAndDescription(uri.toString(), description)
                ACTION_MODIFY -> viewModel.insertImageAndDescription(uri.toString(), description)
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoUri = Uri.fromFile(this)
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

}