package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
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
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AddOrModifyPropertyFragment : Fragment(R.layout.fragment_add_property) {

    companion object {
        fun newInstance(): AddOrModifyPropertyFragment {
            return AddOrModifyPropertyFragment()
        }

        private const val ACTION_TYPE = "ACTION_TYPE"
    }

    private val binding by viewBinding { FragmentAddPropertyBinding.bind(it) }
    private val viewModel by viewModels<AddOrModifyPropertyViewModel>()

    private var latLongString: String? = null

    private lateinit var viewFinder: PreviewView
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var addOrModifyPropertyListAdapter: AddOrModifyPropertyListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionType = arguments?.getString(ACTION_TYPE)

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

        // Initialize the date picker
        setupDatePicker(binding.datePickerEditText, binding.datePickerToSellText)

        viewFinder = binding.cameraPreview

        // MODIFY PROPERTY - Complete form with information's of property selected'
        if (actionType == "modify") {
            displaysPropertyInformation()
            displaysPropertyPhotos()
        }

        // Initialize the cancel button
        binding.cancelButton.setOnClickListener {
            requireActivity().finish()
        }

        // Initialize the ok button to get the property added or modified by the user
        binding.okButton.setOnClickListener {
            if (validateFields()) {
                val addViewState = retrieveFormData()

                if (actionType == "add") {
                    //viewModel.addNewProperty(addViewState)
                } else if (actionType == "modify") {
                    viewModel.updateProperty(addViewState)
                }

                requireActivity().finish()
            }
        }


    }

    private fun displaysPropertyInformation() {
        viewModel.viewStateFlow.asLiveData().observe(viewLifecycleOwner) { addViewState ->

            // Fill in the fields for the property
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
                chip.isChecked = addViewState.pointsOfInterest.contains(chip.text, ignoreCase = true)
            }

        }
    }

    private fun displaysPropertyPhotos() {
        val currentList = mutableListOf<AddOrModifyPropertyViewStateItem>()

        viewModel.viewStateItemFlow.asLiveData().observe(viewLifecycleOwner) { viewStateItem ->
            currentList.add(viewStateItem)
            addOrModifyPropertyListAdapter.updateList(currentList)
        }
    }

    private fun retrieveFormData(): AddOrModifyPropertyViewState {

        // Retrieve photos and description

        // Retrieve poi selected
        val selectedChips = listOf(
            binding.schoolChip,
            binding.publicTransportChip,
            binding.hospitalChip,
            binding.shopChip,
            binding.greenSpacesChip,
            binding.restaurantChip
        ).filter { it.isChecked }.map { it.text.toString() }

        return AddOrModifyPropertyViewState(
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
            binding.addressEditText.error = getString(R.string.invalid_address_message)
            return false
        }

        return allFieldsFilled
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
