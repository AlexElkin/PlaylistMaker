package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val buttonSearch = findViewById<Button>(R.id.button_search)
        val buttonLibrary = findViewById<Button>(R.id.button_library)
        val buttonSettings = findViewById<Button>(R.id.button_settings)

        buttonSearch.setOnClickListener {
            startActivity(Intent(this, ActivitySearch::class.java))
        }
        buttonLibrary.setOnClickListener {
            startActivity(Intent(this, ActivityLibrary::class.java))
        }
        buttonSettings.setOnClickListener{
            startActivity(Intent(this, ActivitySettings::class.java))
        }
    }
}

