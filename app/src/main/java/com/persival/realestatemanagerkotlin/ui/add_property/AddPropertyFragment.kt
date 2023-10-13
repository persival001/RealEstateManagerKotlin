package com.persival.realestatemanagerkotlin.ui.add_property

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
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.persival.realestatemanagerkotlin.ui.add_picture_dialog.AddPictureDialogFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class AddPropertyFragment : Fragment(R.layout.fragment_add_property) {

    companion object {
        fun newInstance(): AddPropertyFragment {
            return AddPropertyFragment()
        }
    }

    private val binding by viewBinding { FragmentAddPropertyBinding.bind(it) }
    private val viewModel by viewModels<AddPropertyViewModel>()

    private var latLongString: String? = null
    private var currentPhotoUri: Uri? = null
    private var areImagesPresent = false

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var addPropertyListAdapter: AddPropertyListAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize requestPermissionLauncher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewModel.refreshStoragePermission()
                    viewModel.refreshCameraPermission()
                }
            }

        // Initialize adapter and set it for horizontal view
        addPropertyListAdapter = AddPropertyListAdapter()
        binding.addPhotoRecyclerView.adapter = addPropertyListAdapter
        val horizontalLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.addPhotoRecyclerView.layoutManager = horizontalLayoutManager

        // Initialize the type of property array
        val items = resources.getStringArray(R.array.property_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        binding.typeTextView.setAdapter(adapter)

        // Google Places UI for address autocompletion
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), MAPS_API_KEY)
        }
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
                val addViewState = retrieveFormData()
                viewModel::addNewProperty.invoke(addViewState)
                requireActivity().finish()
            }
        }

    }

    private fun displaysAddedPropertyPhotos() {
        viewModel.addViewStateItemList.asLiveData().observe(viewLifecycleOwner) { viewStateItemList ->
            addPropertyListAdapter.updateList(viewStateItemList)
            areImagesPresent = viewStateItemList.isNotEmpty()
        }
    }

    private fun retrieveFormData(): AddPropertyViewState {

        // Retrieve poi selected
        val selectedChips = listOf(
            binding.schoolChip,
            binding.publicTransportChip,
            binding.hospitalChip,
            binding.shopChip,
            binding.greenSpacesChip,
            binding.restaurantChip
        ).filter { it.isChecked }.map { it.text.toString() }

        return AddPropertyViewState(
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

        // Check if all fields are filled and set error messages
        val allFieldsFilled = editTextList.all { editText ->
            val isFilled = editText.text?.isNotEmpty() == true
            editText.error = if (isFilled) null else getString(R.string.error_message)
            isFilled
        }

        // Check if latLongString is valid
        val isLatLongValid = !latLongString.isNullOrBlank()
        if (!isLatLongValid) {
            binding.addressEditText.error = getString(R.string.invalid_address_message)
        }

        // Check if at least one photo is filled
        if (!areImagesPresent) {
            Toast.makeText(requireContext(), getString(R.string.photo_required), Toast.LENGTH_SHORT).show()
        }

        return allFieldsFilled && isLatLongValid && areImagesPresent
    }

    private fun setupDatePicker(vararg editTexts: TextInputEditText) {
        editTexts.forEach { editText ->
            editText.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker().build()
                datePicker.addOnPositiveButtonClickListener { selection: Long? ->
                    val selectedDate = selection?.let { Date(it) }
                    val formattedDate = selectedDate?.let { viewModel.getFormattedDate(it) }
                    editText.setText(formattedDate)
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
            viewModel.addImageAndDescription(uri.toString(), description)

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
