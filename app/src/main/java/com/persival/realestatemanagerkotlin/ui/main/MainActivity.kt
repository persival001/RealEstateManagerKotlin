package com.persival.realestatemanagerkotlin.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.ActivityMainBinding
import com.persival.realestatemanagerkotlin.ui.add.AddPropertyFragment
import com.persival.realestatemanagerkotlin.ui.detail.DetailActivity
import com.persival.realestatemanagerkotlin.ui.detail.DetailFragment
import com.persival.realestatemanagerkotlin.ui.maps.MapFragment
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesFragment
import com.persival.realestatemanagerkotlin.ui.settings.SettingsFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.mainFrameLayoutContainerProperties.id,
                    PropertiesFragment.newInstance()
                )
                .commitNow()
        }

        // Setup the toolbar
        setSupportActionBar(binding.toolbar)
        // Activate return button
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val containerDetailsId = binding.mainFrameLayoutContainerDetail?.id
        if (containerDetailsId != null && supportFragmentManager.findFragmentById(containerDetailsId) == null) {
            supportFragmentManager.beginTransaction()
                .replace(containerDetailsId, DetailFragment())
                .commitNow()
        }

        viewModel.mainViewActionLiveData.observe(this) { event ->
            event.handleContent {
                when (it) {
                    MainViewAction.NavigateToDetailActivity -> startActivity(
                        Intent(
                            this,
                            DetailActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.show()
        viewModel.onResume(resources.getBoolean(R.bool.isTablet))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            // Handle the back button click
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                true
            }

            R.id.action_add -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.mainFrameLayoutContainerProperties.id,
                        AddPropertyFragment.newInstance()
                    )
                    .addToBackStack(null)
                    .commit()
                true
            }

            R.id.action_modify -> {
                // TODO: Handle modify action
                true
            }

            R.id.action_search -> {
                // TODO: Handle search action
                true
            }

            R.id.action_map -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.mainFrameLayoutContainerProperties.id,
                        MapFragment.newInstance()
                    )
                    .addToBackStack(null)
                    .commit()
                true
            }

            R.id.action_settings -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.mainFrameLayoutContainerProperties.id,
                        SettingsFragment.newInstance()
                    )
                    .addToBackStack(null)
                    .commit()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDatePicker(view: View) {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener { selection: Long? ->
            val selectedDate: String? =
                selection?.let { Date(it) }
                    ?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) }
            (view as TextInputEditText).setText(selectedDate)
        }
        datePicker.show(supportFragmentManager, "date_picker_tag")
    }

}
