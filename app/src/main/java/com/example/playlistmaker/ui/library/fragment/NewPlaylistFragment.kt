package com.example.playlistmaker.ui.library.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.databinding.NewPlaylistFragmentBinding
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class NewPlaylistFragment : Fragment() {

    private var photo = false
    private var text = false
    private var title: String? = null
    private var url: Uri? = null

    private val playlistDbInteractor: PlaylistDbInteractor by inject()
    private var _binding: NewPlaylistFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewPlaylistFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupTextWatcher()
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            if (text || photo) {
                showExitDialog()
            } else {
                exit()
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.icon.setImageURI(uri)
                url = uri
                photo = true
                updateButtonState()
            }
        }

        binding.icon.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonCreate.setOnClickListener {
            savePlaylistAndExit()
        }
    }

    private fun setupTextWatcher() {
        binding.editTextTitle.addTextChangedListener(createTextWatcher())
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                title = s?.toString()
                text = !s.isNullOrBlank()
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun updateButtonState() {
        val isEnabled = text
        binding.buttonCreate.isEnabled = isEnabled
        binding.buttonCreate.isClickable = isEnabled

        val color = if (isEnabled) Color.BLUE else Color.GRAY
        binding.buttonCreate.backgroundTintList = ColorStateList.valueOf(color)
    }

    private fun exit() = findNavController().popBackStack()
    private fun saveDefaultImageToPrivateStorage(title: String): String {
        val context = requireContext()
        try {
            val storageDir = context.getDir("album_playlist", Context.MODE_PRIVATE)
            val file = File(storageDir, "$title.jpg")

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder)

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
            bitmap.recycle()

            return file.absolutePath

        } catch (e: Exception) {
            Log.e("NewPlaylistFragment", "Error saving default image: ${e.message}")
            return ""
        }
    }
    private fun savePlaylistAndExit() {
        val titleText = binding.editTextTitle.text.toString().trim()

        val imagePath = if (url != null) {
            url!!.saveImageToPrivateStorage(titleText)
        } else {
            saveDefaultImageToPrivateStorage(titleText)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            playlistDbInteractor.insertPlaylist(Playlists(
                title = titleText,
                picture = imagePath))
        }
        requireActivity().supportFragmentManager.setFragmentResult(
            "new_playlist_request",
            bundleOf("playlist_title" to titleText)
        )
        exit()
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Завершить") { _, _ -> exit() }
            .show()
    }

    private fun Uri.saveImageToPrivateStorage(title: String): String {
        val context = requireContext()
        try {
            val storageDir = context.getDir("album_playlist", Context.MODE_PRIVATE)
            val file = File(storageDir, "$title.jpg")

            context.contentResolver.openInputStream(this)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    bitmap?.recycle()
                }
            }
            return file.absolutePath

        } catch (e: Exception) {
            Log.e("NewPlaylistFragment", "Error saving image: ${e.message}")
            return ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}