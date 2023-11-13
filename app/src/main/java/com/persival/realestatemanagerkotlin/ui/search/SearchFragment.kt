package com.persival.realestatemanagerkotlin.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.FragmentSearchBinding
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private val binding by viewBinding { FragmentSearchBinding.bind(it) }
    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChips()
        setupButtons()
        observeViewModel()
    }

    private fun setupChips() {
        binding.schoolChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Logique pour schoolChip sélectionné
            }
        }

        // Répétez pour les autres chips
    }

    private fun setupButtons() {
        binding.okButton.setOnClickListener {
            // Logique pour le bouton OK
            val minPrice = binding.priceMinEditText.text.toString()
            // Récupérez les autres valeurs ici et appelez le ViewModel
            //viewModel.searchProperties(minPrice /*, autres paramètres*/)
        }

        binding.clearButton.setOnClickListener {
            // Logique pour le bouton Effacer
            clearForm()
        }
    }

    private fun observeViewModel() {
        //viewModel.properties.observe(viewLifecycleOwner) { properties ->
        // Mettre à jour l'interface utilisateur avec les propriétés
        //}
    }

    private fun clearForm() {
        // Code pour réinitialiser le formulaire
    }
}