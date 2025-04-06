package com.example.playlistmaker.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.LIST_TRACKS
import com.example.playlistmaker.R
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.playlistmaker.data_classes.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.playlistmaker.MY_SAVES
import com.example.playlistmaker.TRACK
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
    private lateinit var shared: SharedPreferences
    private lateinit var buttonBackView: ImageButton
    private var textWidth = 0f
    private var screenWidth = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        initVievs()
        shared = getSharedPreferences(MY_SAVES, MODE_PRIVATE)
        setValues()
        buttonBackView.setOnClickListener { finish() }

    }

    @SuppressLint("DefaultLocale")
    private fun formatTrackTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setValues() {
        read().apply {
            trackNameView.text = trackName
            artistNameView.text = artistName
            trackTimeView.text = formatTrackTime(trackTimeMillis)
            albumView.text = collectionName
            if (collectionName.isEmpty()){
                albumView.visibility = View.GONE
            }
            yearView.text = releaseDate.toString().take(4)
            genreView.text = primaryGenreName
            countryView.text = country
            trackNameView.isSelected = true

            try {
                Glide.with(trackViewLogo.context)
                    .load(artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                    .transform(
                        RoundedCornersTransformation(8, 0)
                    )
                    .into(trackViewLogo)
            } catch (e: Exception) {
                Glide.with(trackViewLogo.context)
                    .load(R.drawable.placeholder)
                    .transform(
                        RoundedCornersTransformation(8, 0)
                    )
                    .into(trackViewLogo)
            }
        }
    }


    private fun initVievs() {
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
    }

    private fun read(): Track {
        val json = intent.getStringExtra(TRACK)
        val type = object : TypeToken<Track>() {}.type
        return Gson().fromJson(json, type)
    }
}