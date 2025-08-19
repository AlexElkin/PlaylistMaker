package com.example.playlistmaker.ui.player.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.CORNER_RADIUS_DP_LOGO_500
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.AudioPlayerFragmentBinding
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(getTrack())
    }
    private lateinit var binding: AudioPlayerFragmentBinding
    private val tracksDbInteractor: TracksDbInteractor by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        binding = AudioPlayerFragmentBinding.inflate(inflater, container, false)
        initViews()
        observeViewModel()
        setupTrackInfo(getTrack())
        return binding.root
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
            binding.playTrack.setImageResource(
                when (state) {
                    PlayerViewModel.PlaybackState.PLAYING -> R.drawable.track_pause
                    else -> R.drawable.track_play
                }
            )
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
        viewLifecycleOwner.lifecycleScope.launch {
            tracksDbInteractor.updateStatusTrack(track)
            controlImageFavoritesTracks(track)
        }

    }

    fun controlImageFavoritesTracks(track: Track) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (tracksDbInteractor.getStatusTrack(track)) {
                Log.d("тест","true")
                binding.likeTrack.setImageResource(R.drawable.like_activated)
            } else {
                Log.d("тест","false")
                binding.likeTrack.setImageResource(R.drawable.like_track)
            }
        }

    }

    private fun Float.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        ).toInt()
    }
}