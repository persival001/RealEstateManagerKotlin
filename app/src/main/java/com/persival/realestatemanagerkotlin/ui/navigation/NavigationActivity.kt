package com.persival.realestatemanagerkotlin.ui.navigation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.ActivityNavigationBinding
import com.persival.realestatemanagerkotlin.ui.add_or_modify_property.AddOrModifyPropertyFragment
import com.persival.realestatemanagerkotlin.ui.detail.DetailFragment
import com.persival.realestatemanagerkotlin.ui.maps.MapFragment
import com.persival.realestatemanagerkotlin.ui.settings.SettingsFragment
import com.persival.realestatemanagerkotlin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity(), NavigationHandler {

    private val binding by viewBinding { ActivityNavigationBinding.inflate(it) }

    companion object {
        private const val SELECTED_ITEM = "selectedItem"
        private const val ADD_ITEM = "item_add"
        private const val MODIFY_ITEM = "item_modify"
        private const val MAP_ITEM = "item_map"
        private const val SETTINGS_ITEM = "item_settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        when (intent.getStringExtra(SELECTED_ITEM)) {
            ADD_ITEM -> {
                val bundle = Bundle()
                bundle.putString(AddOrModifyPropertyFragment.ACTION_TYPE_KEY, AddOrModifyPropertyFragment.ACTION_ADD)
                val fragment = AddOrModifyPropertyFragment()
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, fragment)
                    .commit()
            }

            MODIFY_ITEM -> {
                val bundle = Bundle()
                bundle.putString(AddOrModifyPropertyFragment.ACTION_TYPE_KEY, AddOrModifyPropertyFragment.ACTION_MODIFY)
                val fragment = AddOrModifyPropertyFragment()
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, fragment)
                    .commit()
            }

            MAP_ITEM -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, MapFragment())
                    .commit()
            }

            SETTINGS_ITEM -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, SettingsFragment())
                    .commit()
            }

            else -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, AddOrModifyPropertyFragment())
                    .commit()
            }
        }
    }

    override fun navigateToDetail() {
        Log.d("MainActivity", "navigateToDetail called!")
        val containerId = if (resources.getBoolean(R.bool.isTablet)) {
            binding.fragmentMenuContainer.id
        } else {
            binding.fragmentMenuContainer.id
        }
        val detailFragment = DetailFragment.newInstance()
        supportFragmentManager.commit {
            replace(containerId, detailFragment)
            addToBackStack(null)
        }
    }
}
