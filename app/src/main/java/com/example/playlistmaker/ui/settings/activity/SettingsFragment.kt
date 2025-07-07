package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SettingsFragmentBinding
import com.example.playlistmaker.ui.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var isSwitchProgrammaticUpdate = false
    private lateinit var binding: SettingsFragmentBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        setupClickListeners()
        observeViewModel()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.apply {
            textViewShareTheApp.setOnClickListener { viewModel.onShareAppClicked() }
            textViewWriteToSupport.setOnClickListener { viewModel.onSupportClicked() }
            textViewUserAgreement.setOnClickListener { viewModel.onUserAgreementClicked() }
            switchTheme.setOnCheckedChangeListener { _, checked ->
                if (!isSwitchProgrammaticUpdate) {
                    Log.d("SettingsFragment", "Theme switch changed: $checked")
                    viewModel.onThemeSwitchChanged(checked)
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.themeState.collect { isDarkTheme ->
                    isSwitchProgrammaticUpdate = true
                    binding.switchTheme.isChecked = isDarkTheme
                    isSwitchProgrammaticUpdate = false
                    updateThemeUi(isDarkTheme)
                }
            }
        }

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is SettingsViewModel.NavigationEvent.ShareApp -> shareApp()
                is SettingsViewModel.NavigationEvent.Support -> openSupportEmail()
                is SettingsViewModel.NavigationEvent.UserAgreement -> openUserAgreement()
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let { showToast(it) }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, getString(R.string.link_android_developer_plus))
            type = "text/plain"
        }
        startActivity(intent)
    }

    private fun updateThemeUi(isDark: Boolean) {
        binding.root.animate().alpha(0.7f).setDuration(150).withEndAction {
            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            binding.root.animate().alpha(1f).duration = 150
        }
    }

    private fun openSupportEmail() {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_mail))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail))
            })
        } catch (e: Exception) {
            viewModel.showToast(getString(R.string.no_mail_applications))
        }
    }

    private fun openUserAgreement() {
        startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.link_practicum_offer).toUri()))
    }
}