package com.persival.realestatemanagerkotlin.ui.main

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.ActivityMainBinding
import com.persival.realestatemanagerkotlin.ui.description.DescriptionFragment
import com.persival.realestatemanagerkotlin.ui.maps.MapFragment
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

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
                    // handle navigation
                    true
                }
                R.id.nav_logout -> {
                    // handle navigation to settings
                    true
                }

                else -> false
            }
        }
    }
}
