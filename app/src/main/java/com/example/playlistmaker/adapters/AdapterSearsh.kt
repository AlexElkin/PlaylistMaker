package com.example.playlistmaker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.MILLISECONDS_IN_SECOND
import com.example.playlistmaker.R
import com.example.playlistmaker.SECONDS_IN_MINUTE
import com.example.playlistmaker.TIME_FORMAT
import com.example.playlistmaker.TRACK
import com.example.playlistmaker.activity.AudioPlayerActivity
import com.example.playlistmaker.data_classes.Track
import com.google.gson.Gson
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AdapterSearsh(
    private var tracks: List<Track>,
    private val onItemClickListener: OnItemClickListener,
    private val context: Context
) : RecyclerView.Adapter<AdapterSearsh.ViewHolderSearsh>() {

    interface OnItemClickListener {
        fun onItemClick(track: Track)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    class ViewHolderSearsh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val imageButton: ImageButton = itemView.findViewById(R.id.track_view_logo)

        @SuppressLint("DefaultLocale")
        private fun formatTrackTime(milliseconds: Long): String {
            val totalSeconds = milliseconds / MILLISECONDS_IN_SECOND
            val minutes = totalSeconds / SECONDS_IN_MINUTE
            val seconds = totalSeconds % SECONDS_IN_MINUTE
            return String.format(TIME_FORMAT, minutes, seconds)
        }

        private fun setMaxLines() {
            trackName.maxLines = 1
            artistName.maxLines = 1
            trackTime.maxLines = 1

            trackName.ellipsize = TextUtils.TruncateAt.END
            artistName.ellipsize = TextUtils.TruncateAt.END
            trackTime.ellipsize = TextUtils.TruncateAt.END
        }

        fun bind(model: Track) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = formatTrackTime(model.trackTimeMillis)
            setMaxLines()

            Glide.with(imageButton.context)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.baseline_sync_problem_24)
                .error(R.drawable.baseline_sync_problem_24)
                .transform(RoundedCornersTransformation(2, 0))
                .into(imageButton)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSearsh {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return ViewHolderSearsh(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: ViewHolderSearsh, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(track)
            val intent = Intent(context, AudioPlayerActivity::class.java)
            intent.putExtra(TRACK, track)
            context.startActivity(intent)
        }
    }
}