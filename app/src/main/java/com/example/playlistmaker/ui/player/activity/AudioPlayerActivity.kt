package com.example.playlistmaker.ui.player.activity

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.CORNER_RADIUS_DP_LOGO_500
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.domain.player.impl.PlayerUseCase
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModelFactory
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK)
        } ?: run {
            finish()
            return
        }
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
        val repository = PlayerRepositoryImpl(mediaPlayer)
        val useCase = PlayerUseCase(repository)
        viewModel = ViewModelProvider(this, PlayerViewModelFactory(useCase, track))
            .get(PlayerViewModel::class.java)

        initViews()
        observeViewModel()
        setupTrackInfo(track)
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