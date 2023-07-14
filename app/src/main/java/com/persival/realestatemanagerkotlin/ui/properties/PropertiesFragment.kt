package com.persival.realestatemanagerkotlin.ui.properties

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentPropertiesBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertiesFragment : Fragment(R.layout.fragment_properties) {

    companion object {
        fun newInstance() = PropertiesFragment()
    }

    private val binding by viewBinding { FragmentPropertiesBinding.bind(it) }
    private val viewModel by viewModels<PropertiesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}