package com.persival.realestatemanagerkotlin.ui.description

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentDescriptionBinding
import com.persival.realestatemanagerkotlin.databinding.FragmentPropertiesBinding
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesFragment
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesViewModel
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DescriptionFragment : Fragment(R.layout.fragment_description) {

    companion object {
        fun newInstance() = DescriptionFragment()
    }

    private val binding by viewBinding { FragmentDescriptionBinding.bind(it) }
    private val viewModel by viewModels<DescriptionViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}