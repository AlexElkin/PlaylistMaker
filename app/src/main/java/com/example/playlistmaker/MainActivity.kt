package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var buttonSearch: Button
    private lateinit var buttonLibrary: Button
    private lateinit var buttonSettings: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setOnClick()
    }

    private fun initViews() {
        buttonSearch = findViewById(R.id.activity_main_button_search)
        buttonLibrary = findViewById(R.id.activity_main_button_library)
        buttonSettings = findViewById(R.id.activity_main_button_settings)
    }

    private fun setOnClick() {
        buttonSearch.setOnClickListener {
            startActivity(Intent(this, ActivitySearch::class.java))
        }
        buttonLibrary.setOnClickListener {
            startActivity(Intent(this, ActivityLibrary::class.java))
        }
        buttonSettings.setOnClickListener {
            startActivity(Intent(this, ActivitySettings::class.java))
        }
    }
}

