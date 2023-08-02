package com.persival.realestatemanagerkotlin.ui.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.ui.add.AddPropertyFragment
import com.persival.realestatemanagerkotlin.ui.maps.MapFragment
import com.persival.realestatemanagerkotlin.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        when (intent.getStringExtra("selectedItem")) {
            "item_add", "item_modify", "item_search" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, AddPropertyFragment())
                    .commit()
            }

            "item_map" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, MapFragment())
                    .commit()
            }

            "item_settings" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, SettingsFragment())
                    .commit()
            }

            else -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_menu_container, AddPropertyFragment())
                    .commit()
            }
        }
    }
}
