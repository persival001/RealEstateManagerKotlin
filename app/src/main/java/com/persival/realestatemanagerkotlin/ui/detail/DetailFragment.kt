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
        fun newInstance(propertyId: Int): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putInt("property_id", propertyId)
            fragment.arguments = args
            return fragment
        }
    }

    private val binding by viewBinding { FragmentDetailBinding.bind(it) }
    private val viewModel by viewModels<DetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView
        val imageUrls = listOf(
            "content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3DbX53lkaBmAPTqL9DhP-naDAieVWi7FvOEb84_Az02gltJ_LK1x0FWdIR",
            "content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3DgO9R07XlDxGjwuiJog9ChTB5IjVhZbo-OoWs5XFMxA00j90S-8Uosd78",
            "content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3Dgg4qFnePsiX_StfeK7v3YGxb6R__oAKhUsxdIxjP81GUtkN07cFZPERf",
            "content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3D9rgLo8QAZUHgUAFCJov0Oo5o-MpmFkku2MlfPM4J9OmpPhG4IbNPVwLP",
            "content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3DgGL_gad-Z6ujV9bGn4cRZo3Q-BnmMK5N5peY1oYgHP0a-pxTCg9bgbSV",
            "content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3DQ-oYa22hrv0qvGeHHy_N-8CaBp0qSMtHHl3Qs1kS1EwHBC7NpYGqaY72"
        )

        val captions = listOf("Image 1", "Image 2", "Image 3", "Image 4", "Image 5", "Image 6")

        val recyclerView: RecyclerView = view.findViewById(R.id.carousel_recycler_view)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = context?.let { DetailImageAdapter(it, imageUrls, captions) }

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
        val propertyId = arguments?.getInt("property_id")
        binding.locationTextView.text = propertyId.toString()

    }

}
