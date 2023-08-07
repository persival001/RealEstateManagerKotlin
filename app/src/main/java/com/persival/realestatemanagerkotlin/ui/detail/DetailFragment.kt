package com.persival.realestatemanagerkotlin.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.BuildConfig.MAPS_API_KEY
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentDetailBinding
import com.persival.realestatemanagerkotlin.ui.maps.MapFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    companion object {
        fun newInstance(propertyId: Long): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putLong("property_id", propertyId)
            fragment.arguments = args
            return fragment
        }
    }

    private val binding by viewBinding { FragmentDetailBinding.bind(it) }
    private val viewModel by viewModels<DetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.carouselRecyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // observe the detailItem livedata
        viewModel.detailItem.observe(viewLifecycleOwner) { detailViewStateItem ->
            context?.let { safeContext ->
                val detailImageAdapter = DetailImageAdapter(
                    safeContext,
                    detailViewStateItem.url,
                    detailViewStateItem.caption
                )
                recyclerView.adapter = detailImageAdapter
            }
        }

        val propertyId = arguments?.getLong("property_id")

        if (propertyId == null) {
            binding.root.visibility = View.GONE
        } else {
            binding.root.visibility = View.VISIBLE
            viewModel.setPropertyId(propertyId)
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
        }

        // Show the static map
        val url =
            "https://maps.googleapis.com/maps/api/" +
                    "staticmap?center=1600+Amphitheatre+Parkway," +
                    "+Mountain+View," +
                    "+CA&zoom=14&size=400x400&markers=color:red%7Clabel:C%7C1600+Amphitheatre+Parkway," +
                    "+Mountain+View," +
                    "+CA&key=$MAPS_API_KEY"

        Glide.with(this)
            .load(url)
            .override(500, 500)
            .into(binding.mapImageView)

        // Open the map fragment if clicked
        binding.mapImageView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    this.id,
                    MapFragment.newInstance()
                )
                .addToBackStack(null)
                .commit()
        }

    }

}
