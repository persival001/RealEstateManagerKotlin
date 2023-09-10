package com.persival.realestatemanagerkotlin.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
    private var propertyPrice: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch the property ID
        viewModel.fetchAndLoadDetailsForSelectedProperty()

        // Details view is gone if no property selected
        viewModel.selectedId.observe(viewLifecycleOwner) { propertyId ->
            if (propertyId == null || propertyId <= 0) {
                binding.root.visibility = View.GONE
            } else {
                binding.root.visibility = View.VISIBLE
            }
        }

        // Show the property details photos
        val recyclerView: RecyclerView = binding.carouselRecyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        viewModel.detailItem.observe(viewLifecycleOwner) { detailViewStateItems ->
            val detailImageAdapter = DetailImageAdapter(requireContext(), detailViewStateItems)
            recyclerView.adapter = detailImageAdapter
        }

        // Show the property details
        viewModel.details.observe(viewLifecycleOwner) { details ->
            binding.roomsTextView.text = details.rooms
            binding.bedroomsTextView.text = details.bedrooms
            binding.bathroomsTextView.text = details.bathrooms
            binding.descriptionTextView.text = details.description
            binding.surfaceTextView.text = details.surface
            binding.locationTextView.text = details.address
            binding.poiTextView.text = details.pointOfInterest
            propertyPrice = details.price.toDouble()

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
                .into(binding.mapImageView)
        }

        // Open the map fragment if clicked
        binding.mapImageView.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    this@DetailFragment.id,
                    MapFragment.newInstance()
                )
                addToBackStack(null)
            }
        }

        // Loan simulator button
        binding.loanSimulatorButton.setOnClickListener {
            val loanSimulatorDialog = LoanSimulatorDialogFragment.newInstance(propertyPrice)
            loanSimulatorDialog.show(parentFragmentManager, "loanSimulatorDialogTag")
        }

    }

}
