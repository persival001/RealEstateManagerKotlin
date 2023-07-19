package com.persival.realestatemanagerkotlin.ui.main

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(binding.fragmentContainerView.id, MapFragment.newInstance())
            }
        }

        // Setup the toolbar
        setSupportActionBar(binding.toolbar)

        // Setup the drawer layout toggle and link it with the toolbar
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set up the bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_1 -> {
                    supportFragmentManager.commitNow {
                        replace(binding.fragmentContainerView.id, MapFragment.newInstance())
                    }
                    true
                }
                R.id.item_2 -> {
                    supportFragmentManager.commitNow {
                        replace(binding.fragmentContainerView.id, PropertiesFragment.newInstance())
                    }
                    true
                }
                R.id.item_3 -> {
                    supportFragmentManager.commitNow {
                        replace(binding.fragmentContainerView.id, DescriptionFragment.newInstance())
                    }
                    true
                }

                else -> false
            }
        }

        // Set up the drawer menu
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_settings -> {
                    supportFragmentManager.commitNow {
                        replace(binding.fragmentContainerView.id, SettingsFragment.newInstance())
                    }
                    true
                }
                R.id.nav_logout -> {
                    finish()

                    true
                }

                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                supportFragmentManager.commitNow {
                    replace(binding.fragmentContainerView.id, AddPropertyFragment.newInstance())
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
