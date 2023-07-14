package com.persival.realestatemanagerkotlin.ui.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentAddPropertyBinding
import com.persival.realestatemanagerkotlin.databinding.FragmentDescriptionBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPropertyFragment : Fragment(R.layout.fragment_add_property) {

    companion object {
        fun newInstance() = AddPropertyFragment()
    }

    private val binding by viewBinding { FragmentAddPropertyBinding.bind(it) }
    private val viewModel by viewModels<AddPropertyViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}