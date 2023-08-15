package com.persival.realestatemanagerkotlin.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.ActivityMainBinding
import com.persival.realestatemanagerkotlin.ui.add.PropertyIdListener
import com.persival.realestatemanagerkotlin.ui.detail.DetailFragment
import com.persival.realestatemanagerkotlin.ui.navigation.NavigationActivity
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PropertyIdListener {

    private var propertyId: Long? = null

    override fun onPropertyIdRequested(propertyId: Long) {
        this.propertyId = propertyId
    }

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    private val viewModel by viewModels<MainViewModel>()

    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            if (f is DetailFragment && !resources.getBoolean(R.bool.isTablet)) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

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

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)

        // If the device is a tablet, display the first property in the detail fragment
        /* if (resources.getBoolean(R.bool.isTablet)) {
             val firstPropertyId = 1L
             propertyId = firstPropertyId

             val containerDetailsId = binding.mainFrameLayoutContainerDetail?.id
             if (containerDetailsId != null) {
                 supportFragmentManager.beginTransaction()
                     .replace(containerDetailsId, DetailFragment.newInstance(firstPropertyId))
                     .commitNow()
             }
         }*/

        // Setup the toolbar
        setSupportActionBar(binding.toolbar)

        val containerDetailsId = binding.mainFrameLayoutContainerDetail?.id
        if (containerDetailsId != null && supportFragmentManager.findFragmentById(containerDetailsId) == null) {
            supportFragmentManager.beginTransaction()
                .replace(containerDetailsId, DetailFragment())
                .commitNow()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else if (resources.getBoolean(R.bool.isTablet)) {
                    binding.mainFrameLayoutContainerDetail?.let {
                        supportFragmentManager.beginTransaction()
                            .replace(
                                it.id,
                                DetailFragment()
                            )
                            .commit()
                    }
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            binding.mainFrameLayoutContainerProperties.id,
                            PropertiesFragment.newInstance()
                        )
                        .commit()
                }
            }
        })
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
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                true
            }

            // Handle the toolbar menu items
            R.id.action_add -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("selectedItem", "item_add")
                startActivity(intent)
                return true
            }

            R.id.action_modify -> {
                // TODO Persival respect the UDF and use a small im memory repository to store and use the "current property id" !
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("selectedItem", "item_modify")
                startActivity(intent)
                return true
            }

            R.id.action_search -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("selectedItem", "item_search")
                startActivity(intent)
                return true
            }

            R.id.action_map -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("selectedItem", "item_map")
                startActivity(intent)
                return true
            }

            R.id.action_settings -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("selectedItem", "item_settings")
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.show()
        if (!resources.getBoolean(R.bool.isTablet)) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        viewModel.onResume(resources.getBoolean(R.bool.isTablet))
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }

}
