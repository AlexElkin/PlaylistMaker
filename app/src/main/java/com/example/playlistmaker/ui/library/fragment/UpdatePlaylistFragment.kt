package com.example.playlistmaker.ui.library.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.PLAYLIST
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.databinding.UpdatePlaylistFragmentBinding
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class UpdatePlaylistFragment : Fragment() {
    private val playlistDbInteractor: PlaylistDbInteractor by inject()
    private var url: Uri? = null
    private var text = false
    private var title: String? = null
    private lateinit var playlist: Playlists
    private var _binding: UpdatePlaylistFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        _binding = UpdatePlaylistFragmentBinding.inflate(inflater, container, false)
        playlist = getPlaylist()
        setupClickListeners()
        setupTextWatcher()
        setPlaylist()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            exit()
            }


        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.icon)
                url = uri
                updateButtonState()
            }
        }

        binding.icon.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonCave.setOnClickListener {
            savePlaylistAndExit()
        }
    }
    private fun exit() = findNavController().popBackStack()

    private fun savePlaylistAndExit() {
        val titleText = binding.editTextTitle.text.toString().trim()
        val descriptionText = binding.editTextDescription.text.toString().trim()
        val imagePath = if (url != null) {
            url!!.saveImageToPrivateStorage(titleText)
        } else {
            playlist.picture
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val playlistId = playlistDbInteractor.getIdPlaylist(playlist.title)
            val currentCountTracks = playlistDbInteractor.getPlaylistById(playlistId).countTracks
            playlistDbInteractor.updatePlaylistById(
                rowId = playlistId,
                playlistTitle = titleText,
                picture = imagePath,
                description = descriptionText
            )
            val resultBundle = bundleOf(
                "playlist_object" to Playlists(title = titleText,description = descriptionText,picture = imagePath, countTracks = currentCountTracks)
            )
            parentFragmentManager.setFragmentResult("playlist_changed", resultBundle)
            exit()
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
        binding.buttonCave.isEnabled = isEnabled
        binding.buttonCave.isClickable = isEnabled

        val color = if (isEnabled) Color.BLUE else Color.GRAY
        binding.buttonCave.backgroundTintList = ColorStateList.valueOf(color)
    }
    private fun setPlaylist() {
        Glide.with(requireContext())
            .load(playlist.picture)
            .placeholder(R.drawable.placeholder)
            .into(binding.icon)
        binding.editTextTitle.setText(playlist.title)
        binding.editTextDescription.setText(playlist.description)
    }

    private fun getPlaylist(): Playlists {
        val args = arguments ?: throw IllegalStateException("Arguments not found")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable(PLAYLIST, Playlists::class.java)
        } else {
            @Suppress("DEPRECATION")
            args.getParcelable(PLAYLIST)
        } ?: throw IllegalStateException("Track not found")
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