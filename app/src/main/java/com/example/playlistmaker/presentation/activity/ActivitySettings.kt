package com.example.playlistmaker.presentation.activity

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.MY_SAVES
import com.example.playlistmaker.data.THEME
import com.example.playlistmaker.presentation.ui.App

class ActivitySettings : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var switch: SwitchCompat
    private lateinit var shareApp: TextView
    private lateinit var support: TextView
    private lateinit var userAgreement: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPreferences = getSharedPreferences(MY_SAVES, MODE_PRIVATE)
        initViews()
        switch.isChecked = sharedPreferences.getBoolean(THEME, false)
        setOnClick()
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.activity_settings_button_back)
        switch = findViewById(R.id.activity_settings_switch_theme)
        shareApp = findViewById(R.id.activity_settings_textView_share_the_app)
        support = findViewById(R.id.activity_settings_textView_write_to_support)
        userAgreement = findViewById(R.id.activity_settings_textView_user_agreement)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun setOnClick() {
        support.setOnClickListener {
            val email = getString(R.string.email)
            val subject = getString(R.string.theme_mail)
            val text = getString(R.string.text_mail)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.no_mail_applications), Toast.LENGTH_SHORT).show()
            }
        }

        shareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.link_android_developer_plus))
                type = "text/plain"
            }
            startActivity(intent)
        }

        userAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.link_practicum_offer).toUri()
            }
            startActivity(intent)
        }

        buttonBack.setOnClickListener {
            finish()
        }

        switch.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPreferences.edit().putBoolean(THEME, checked).apply()
        }
    }
}