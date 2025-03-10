package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class ActivitySettings : AppCompatActivity() {

    private lateinit var buttonBack:ImageButton
    private lateinit var switch:SwitchCompat
    private lateinit var shareApp:TextView
    private lateinit var support:TextView
    private lateinit var userAgreement:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initViews()
        setOnClick()

    }

    private fun initViews() {
        buttonBack = findViewById(R.id.activity_settings_button_back)
        switch = findViewById(R.id.activity_settings_switch_theme)
        shareApp = findViewById(R.id.activity_settings_textView_share_the_app)
        support = findViewById(R.id.activity_settings_textView_write_to_support)
        userAgreement = findViewById(R.id.activity_settings_textView_user_agreement)
    }
    private fun setOnClick(){
        support.setOnClickListener {
            val email = getString(R.string.email)
            val subject = getString(R.string.theme_mail)
            val text = getString(R.string.text_mail)
            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
            }
            if (intent.resolveActivity(packageManager) != null) {
                // Пытаемся открыть почтовое приложение по умолчанию
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val chooser = Intent.createChooser(intent,
                        getString(R.string.select_email_application))
                    startActivity(chooser)
                }
            } else {
                Toast.makeText(this, getString(R.string.no_mail_applications), Toast.LENGTH_SHORT).show()
            }
        }

        shareApp.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString (R.string.link_android_developer_plus))
                type = "text/plain"
            }
            startActivity(intent)
        }

        userAgreement.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.link_practicum_offer))
            }
            startActivity(intent)
        }
        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        switch.setOnClickListener {
            support.setOnClickListener {
                // TODO:дописать свитчер
            }

        }

    }
}