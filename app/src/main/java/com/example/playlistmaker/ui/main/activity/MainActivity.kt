package com.example.playlistmaker.ui.main.activity

import androidx.activity.viewModels
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.library.activity.ActivityLibrary
import com.example.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.ActivitySettings

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.activityMainButtonSearch.setOnClickListener {
            viewModel.onSearchClicked()
        }

        binding.activityMainButtonLibrary.setOnClickListener {
            viewModel.onLibraryClicked()
        }

        binding.activityMainButtonSettings.setOnClickListener {
            viewModel.onSettingsClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.navigationEvent.observe(this) { destination ->
            when (destination) {
                MainViewModel.NavigationDestination.SEARCH -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                }
                MainViewModel.NavigationDestination.LIBRARY -> {
                    startActivity(Intent(this, ActivityLibrary::class.java))
                }
                MainViewModel.NavigationDestination.SETTINGS -> {
                    startActivity(Intent(this, ActivitySettings::class.java))
                }
            }
        }
    }
}