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
        //recyclerView.adapter = context?.let { DetailImageAdapter(it, detailViewStateItems) }

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

        // Show the property details
        val propertyId = arguments?.getLong("property_id")
        binding.locationTextView.text = propertyId.toString()

    }

}
