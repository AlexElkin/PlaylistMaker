package com.example.playlistmaker.ui.player.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.CORNER_RADIUS_DP_LOGO_500
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerActivity : AppCompatActivity() {
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(getTrack())
    }
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        observeViewModel()
        setupTrackInfo(getTrack())
    }

    private fun getTrack(): Track {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK)
        } ?: run {
            finish()
            throw IllegalStateException("Track not found")
        }
    }
    private fun initViews() {
        binding.buttonBack.setOnClickListener { finish() }
        binding.playTrack.setOnClickListener { viewModel.playbackControl() }
    }

    private fun observeViewModel() {
        viewModel.playbackState.observe(this) { state ->
            binding.playTrack.setImageResource(
                when (state) {
                    PlayerViewModel.PlaybackState.PLAYING -> R.drawable.track_pause
                    else -> R.drawable.track_play
                }
            )
        }

        viewModel.currentPosition.observe(this) { position ->
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

            val imageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            val cornerRadiusPx = CORNER_RADIUS_DP_LOGO_500.dpToPx(this@AudioPlayerActivity)
            Glide.with(this@AudioPlayerActivity)
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

    private fun Float.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        ).toInt()
    }
}