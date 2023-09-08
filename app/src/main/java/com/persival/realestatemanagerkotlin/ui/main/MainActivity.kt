package com.persival.realestatemanagerkotlin.ui.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.ActivityMainBinding
import com.persival.realestatemanagerkotlin.ui.detail.DetailFragment
import com.persival.realestatemanagerkotlin.ui.navigation.NavigationActivity
import com.persival.realestatemanagerkotlin.ui.navigation.NavigationHandler
import com.persival.realestatemanagerkotlin.ui.properties.PropertiesFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationHandler {

    companion object {
        private const val SELECTED_ITEM = "selectedItem"
        private const val ADD_ITEM = "item_add"
        private const val MODIFY_ITEM = "item_modify"
        private const val SEARCH_ITEM = "item_search"
        private const val MAP_ITEM = "item_map"
        private const val SETTINGS_ITEM = "item_settings"
    }

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    private val viewModel by viewModels<MainViewModel>()

    private val connectivityManager by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    //@Inject
    //lateinit var workerFactory: HiltWorkerFactory

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

        // Initialize ConnectivityReceiver
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Initialize WorkManager
        //viewModel.initializeWorkManager()

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

    // Initialize the network callback
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            runOnUiThread {
                Toast.makeText(this@MainActivity, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show()
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            runOnUiThread {
                Toast.makeText(this@MainActivity, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
            }
        }
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
                intent.putExtra(SELECTED_ITEM, ADD_ITEM)
                startActivity(intent)
                return true
            }

            R.id.action_modify -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra(SELECTED_ITEM, MODIFY_ITEM)
                if (viewModel.getPropertyId().value != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, R.string.no_property_selected, Toast.LENGTH_LONG).show()
                }

                return true
            }

            R.id.action_search -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra(SELECTED_ITEM, SEARCH_ITEM)
                startActivity(intent)
                return true
            }

            R.id.action_map -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra(SELECTED_ITEM, MAP_ITEM)
                startActivity(intent)
                return true
            }

            R.id.action_settings -> {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra(SELECTED_ITEM, SETTINGS_ITEM)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun navigateToDetail() {
        Log.d("MainActivity", "navigateToDetail called!")
        val containerId = if (resources.getBoolean(R.bool.isTablet)) {
            binding.mainFrameLayoutContainerDetail?.id ?: throw IllegalArgumentException("Detail container not found!")
        } else {
            binding.mainFrameLayoutContainerProperties.id
        }
        val detailFragment = DetailFragment.newInstance()
        supportFragmentManager.commit {
            replace(containerId, detailFragment)
            addToBackStack(null)
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
        connectivityManager.unregisterNetworkCallback(networkCallback)
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }

}
