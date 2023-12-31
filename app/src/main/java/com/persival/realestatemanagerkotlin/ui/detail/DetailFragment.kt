package com.persival.realestatemanagerkotlin.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.google.android.material.carousel.CarouselLayoutManager
import com.persival.realestatemanagerkotlin.BuildConfig.MAPS_API_KEY
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentDetailBinding
import com.persival.realestatemanagerkotlin.ui.loan_simulator.LoanSimulatorDialogFragment
import com.persival.realestatemanagerkotlin.ui.maps.MapFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private val binding by viewBinding { FragmentDetailBinding.bind(it) }
    private val viewModel by viewModels<DetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Show the property details photos
        val carouselLayoutManager = CarouselLayoutManager()
        binding.carouselRecyclerView.layoutManager = carouselLayoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.carouselRecyclerView)

        // Show the property details
        viewModel.detailViewStateLiveData.observe(viewLifecycleOwner) { details ->

            // Details view is gone if no property selected
            if (details.propertyId <= 0) {
                binding.root.visibility = View.GONE
            } else {
                binding.root.visibility = View.VISIBLE
            }

            binding.roomsTextView.text = details.rooms
            binding.bedroomsTextView.text = details.bedrooms
            binding.bathroomsTextView.text = details.bathrooms
            binding.descriptionTextView.text = details.description
            binding.surfaceTextView.text = details.surface
            binding.locationTextView.text = details.address
            binding.poiTextView.text = details.pointOfInterest

            val detailImageAdapter = DetailImageAdapter(requireContext(), details.pictures)
            binding.carouselRecyclerView.adapter = detailImageAdapter

            val isSoldString = if (details.isSold) {
                getString(
                    R.string.information_is_sold,
                    details.agentName,
                    details.entryDate,
                    details.saleDate
                )
            } else {
                getString(R.string.information_on_sale, details.agentName, details.entryDate)
            }
            binding.contactTextView.text = isSoldString

            // Show the static map
            val address = details.address.replace(" ", "+")
            val zoomLevel = 14
            val imageSize = "400x400"
            val markerColor = "red"
            val markerLabel = "Property is here !"

            val url = "https://maps.googleapis.com/maps/api/staticmap?" +
                    "center=$address&" +
                    "zoom=$zoomLevel&" +
                    "size=$imageSize&" +
                    "markers=color:$markerColor%7Clabel:$markerLabel%7C$address&" +
                    "key=$MAPS_API_KEY"

            Glide.with(this)
                .load(url)
                .override(500, 500)
                .error(R.drawable.baseline_signal_wifi_connected_no_internet_4_24)
                .into(binding.mapImageView)

            // Loan simulator button
            binding.loanSimulatorButton.setOnClickListener {
                details.price.toDoubleOrNull()?.let { price ->
                    val loanSimulatorDialog = LoanSimulatorDialogFragment.newInstance(price)
                    loanSimulatorDialog.show(parentFragmentManager, "loanSimulatorDialogTag")
                }
            }

            binding.mapImageView.setOnClickListener {
                if (details.isLatLongAvailable) {
                    parentFragmentManager.commit {
                        replace(this@DetailFragment.id, MapFragment.newInstance(mapImageClicked = true))
                        addToBackStack(null)
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.invalid_address_message), Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }


    }

}
