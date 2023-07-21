package com.persival.realestatemanagerkotlin.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.ActivityMainBinding
import com.persival.realestatemanagerkotlin.ui.add.AddPropertyFragment
import com.persival.realestatemanagerkotlin.ui.description.DescriptionFragment
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
            supportFragmentManager.commitNow {
                binding.fragmentContainerView?.let {
                    replace(
                        it.id,
                        MapFragment.newInstance()
                    )
                }
            }
        }

        // Setup the toolbar
        setSupportActionBar(binding.toolbar)
        // Activate return button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                supportFragmentManager.commitNow {
                    binding.fragmentContainerView?.let {
                        replace(
                            it.id,
                            AddPropertyFragment.newInstance()
                        )
                    }
                }
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
