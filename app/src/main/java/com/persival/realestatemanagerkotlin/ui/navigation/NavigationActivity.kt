package com.persival.realestatemanagerkotlin.ui.navigation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.ui.add.AddPropertyFragment
import com.persival.realestatemanagerkotlin.ui.maps.MapFragment
import com.persival.realestatemanagerkotlin.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
