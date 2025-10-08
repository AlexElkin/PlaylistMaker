package com.example.playlistmaker.ui.player.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.CORNER_RADIUS_DP_LOGO_500
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.AudioPlayerFragmentBinding
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.ui.player.adapter.AudioPlayerAdapter
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(getTrack())
    }
    private var _binding: AudioPlayerFragmentBinding? = null
    private val binding get() = _binding!!

    private val tracksDbInteractor: TracksDbInteractor by inject()
    private val playlistDbInteractor: PlaylistDbInteractor by inject()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var adapter: AudioPlayerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        _binding = AudioPlayerFragmentBinding.inflate(inflater, container, false)
        binding.createPlaylist.setOnClickListener { findNavController().navigate(R.id.action_audio_player_fragment_to_newPlaylistFragment) }
        createBottomSheet()
        setupResultListener()
        initViews()
        observeViewModel()
        setupTrackInfo(getTrack())
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPlaylists()
    }
    private fun setupResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener("new_playlist_request", this) { requestKey, bundle ->
            if (requestKey == "new_playlist_request") {
                val title = bundle.getString("playlist_title", "")
                if (title.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Создан плейлист: $title", Toast.LENGTH_SHORT).show()
                    loadPlaylists()
                }
            }
        }
    }
    private fun createBottomSheet(){
        val bottomSheetContainer = binding.standardBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.addTrack.setOnClickListener {bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.backgroundBlack50.isVisible = true}
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.backgroundBlack50.isVisible = false
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        adapter = AudioPlayerAdapter(
            emptyList(),
            onItemClickListener = { playlist -> viewModel.onPlaylistClicked(playlist) }
        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
    private fun getTrack(): Track {
        val args = arguments ?: throw IllegalStateException("Arguments not found")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable(TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            args.getParcelable(TRACK)
        } ?: throw IllegalStateException("Track not found")
    }

    private fun initViews() {
        binding.buttonBack.setOnClickListener { parentFragmentManager.popBackStack()  }
        binding.playTrack.setOnClickListener { viewModel.playbackControl() }
        binding.likeTrack.setOnClickListener { controlFavoritesTracks(getTrack()) }
    }

    private fun observeViewModel() {
        viewModel.playbackState.observe(viewLifecycleOwner) { state ->
            val shouldBePressed = when (state) {
                PlayerViewModel.PlaybackState.PLAYING -> true
                PlayerViewModel.PlaybackState.PREPARED -> false
                PlayerViewModel.PlaybackState.PAUSED -> false
                PlayerViewModel.PlaybackState.COMPLETED -> false
                PlayerViewModel.PlaybackState.IDLE -> false
            }
            if (binding.playTrack.isPressedState() != shouldBePressed) {
                binding.playTrack.setState(shouldBePressed)
            }
        }


        viewModel.showMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            loadPlaylists()
        }
        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            binding.trackPlaybackTime.text = formatTrackTime(position.toLong())
        }
    }

    private fun setupTrackInfo(track: Track) {
        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            textViewTrackTimeResult.text = formatTrackTime(track.trackTimeMillis)
            textViewAlbumResult.text = track.collectionName
            textViewTrackYearResult.text = track.releaseDate.take(4)
            textViewGenreResult.text = track.primaryGenreName
            textViewCountryResult.text = track.country
            trackName.isSelected = true
            controlImageFavoritesTracks(track)
            val imageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            val cornerRadiusPx = CORNER_RADIUS_DP_LOGO_500.dpToPx(requireContext())
            Glide.with(requireContext())
                .load(imageUrl)
                .error(R.drawable.placeholder)
                .transform(RoundedCornersTransformation(cornerRadiusPx, 0))
                .placeholder(R.drawable.placeholder)
                .into(trackViewLogo)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTrackTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun controlFavoritesTracks(track: Track) {
        if (!isAdded) return
        viewLifecycleOwner.lifecycleScope.launch {
            tracksDbInteractor.updateStatusTrack(track)
            controlImageFavoritesTracks(track)
        }

    }

    fun controlImageFavoritesTracks(track: Track) {
        viewLifecycleOwner.lifecycleScope.launch {
            when(tracksDbInteractor.getStatusTrack(track.previewUrl)){
                1 -> binding.likeTrack.setImageResource(R.drawable.like_activated)
                0 -> binding.likeTrack.setImageResource(R.drawable.like_track)
            }

        }

    }

    private fun loadPlaylists() {
        viewLifecycleOwner.lifecycleScope.launch {
            val playlists = playlistDbInteractor.getPlaylists()
            adapter.updatePlaylists(playlists)

        }
    }

    private fun Float.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}