package com.example.playlistmaker.ui.library.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.ui.library.adapter.FragmentViewPagerAdapter
import com.example.playlistmaker.ui.main.activity.MainActivity
import com.example.playlistmaker.ui.settings.viewmodel.LibraryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityLibrary : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
    private val viewModel: LibraryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = FragmentViewPagerAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
        observeViewModel()
        binding.buttonBack.setOnClickListener { viewModel.onBackClicked() }
    }

    private fun observeViewModel() {
        viewModel.navigationEvent.observe(this) { event ->
            when (event) {
                is LibraryViewModel.NavigationEvent.Back -> startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}

