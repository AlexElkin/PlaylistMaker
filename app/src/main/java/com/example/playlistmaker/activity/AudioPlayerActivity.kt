package com.example.playlistmaker.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.playlistmaker.CORNER_RADIUS_DP_LOGO_500
import com.example.playlistmaker.KEY_IS_PLAYING
import com.example.playlistmaker.KEY_PLAYER_POSITION
import com.example.playlistmaker.REQUESTING_PLAYBACK_TIME
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.data_classes.Track
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var trackNameView: TextView
    private lateinit var trackViewLogo: ImageView
    private lateinit var artistNameView: TextView
    private lateinit var trackTimeView: TextView
    private lateinit var trackPlaybackTimeView: TextView
    private lateinit var albumView: TextView
    private lateinit var yearView: TextView
    private lateinit var genreView: TextView
    private lateinit var countryView: TextView
    private lateinit var buttonBackView: ImageButton
    private lateinit var buttonPlaybackControl: ImageButton

    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable
    private var currentPosition = 0L
    private var isPlaying = false
    private var prepared = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_PLAYER_POSITION, currentPosition)
        outState.putBoolean(KEY_IS_PLAYING, isPlaying)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentPosition = savedInstanceState.getLong(KEY_PLAYER_POSITION, 0)
        isPlaying = savedInstanceState.getBoolean(KEY_IS_PLAYING, false)
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            currentPosition = mediaPlayer.currentPosition.toLong()
            mediaPlayer.pause()
            handler.removeCallbacks(updateTimeRunnable)
            isPlaying = false
        }
        updatePlayButtonState()
    }

    override fun onResume() {
        super.onResume()
        if (prepared) {
            mediaPlayer.seekTo(currentPosition.toInt())
            trackPlaybackTimeView.text = formatTrackTime(currentPosition)
        }
        updatePlayButtonState()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        initViews()
        setValues()
        initTimeUpdater()

        buttonBackView.setOnClickListener { finish() }
        buttonPlaybackControl.setOnClickListener { playbackControl() }
    }

    private fun playbackControl() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(currentPosition.toInt())
            mediaPlayer.start()
            handler.post(updateTimeRunnable)
            isPlaying = true
        } else {
            currentPosition = mediaPlayer.currentPosition.toLong()
            mediaPlayer.pause()
            handler.removeCallbacks(updateTimeRunnable)
            isPlaying = false
        }
        updatePlayButtonState()
    }

    private fun updatePlayButtonState() {
        buttonPlaybackControl.setImageResource(
            if (mediaPlayer.isPlaying) R.drawable.track_pause
            else R.drawable.track_play
        )
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            prepared = true
            buttonPlaybackControl.isEnabled = true
            mediaPlayer.seekTo(currentPosition.toInt())
            trackPlaybackTimeView.text = formatTrackTime(currentPosition)
            updatePlayButtonState()
        }
        mediaPlayer.setOnCompletionListener {
            currentPosition = 0
            isPlaying = false
            updatePlayButtonState()
            trackPlaybackTimeView.text = formatTrackTime(0)
        }
    }

    private fun initTimeUpdater() {
        updateTimeRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    currentPosition = mediaPlayer.currentPosition.toLong()
                    trackPlaybackTimeView.text = formatTrackTime(currentPosition)
                }
                handler.postDelayed(this, REQUESTING_PLAYBACK_TIME)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTrackTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setValues() {
        read()?.apply {
            preparePlayer(previewUrl)
            trackNameView.text = trackName
            artistNameView.text = artistName
            trackTimeView.text = formatTrackTime(trackTimeMillis)
            albumView.text = collectionName
            if (collectionName.isEmpty()) albumView.visibility = View.GONE
            yearView.text = releaseDate.toString().take(4)
            genreView.text = primaryGenreName
            countryView.text = country
            trackNameView.isSelected = true

            val imageUrl = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
            val cornerRadiusPx = CORNER_RADIUS_DP_LOGO_500.dpToPx(trackViewLogo.context)
            Glide.with(trackViewLogo.context)
                .load(imageUrl)
                .error(R.drawable.placeholder)
                .transform(RoundedCornersTransformation(cornerRadiusPx, 0))
                .placeholder(R.drawable.placeholder)
                .into(trackViewLogo)
        }
    }

    private fun Float.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun initViews() {
        trackNameView = findViewById(R.id.AudioPlayer_trackName)
        artistNameView = findViewById(R.id.AudioPlayer_artistName)
        trackTimeView = findViewById(R.id.AudioPlayer_TextView_Track_Time_Result)
        trackPlaybackTimeView = findViewById(R.id.AudioPlayer_track_playback_time)
        albumView = findViewById(R.id.AudioPlayer_TextView_Album_Result)
        yearView = findViewById(R.id.AudioPlayer_TextView_Track_Year_Result)
        genreView = findViewById(R.id.AudioPlayer_TextView_Genre_Result)
        countryView = findViewById(R.id.AudioPlayer_TextView_Country_Result)
        trackViewLogo = findViewById(R.id.AudioPlayer_track_view_logo)
        buttonBackView = findViewById(R.id.AudioPlayer_button_back)
        buttonPlaybackControl = findViewById(R.id.AudioPlayer_play_track)
    }

    private fun read(): Track? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK) as? Track
        }
    }
}