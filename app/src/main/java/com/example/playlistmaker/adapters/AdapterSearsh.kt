package com.example.playlistmaker.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data_classes.Track
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AdapterSearsh(
    private var tracks: List<Track>,
    private val onItemClickListener: OnItemClickListener
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
        private val trackName: TextView
        private val artistName: TextView
        private val trackTime: TextView
        private val imageButton: ImageButton

        init {
            trackName = itemView.findViewById(R.id.trackName)
            artistName = itemView.findViewById(R.id.artistName)
            trackTime = itemView.findViewById(R.id.trackTime)
            imageButton = itemView.findViewById(R.id.track_view_logo)
        }

        @SuppressLint("DefaultLocale")
        private fun formatTrackTime(milliseconds: Long): String {
            val totalSeconds = milliseconds / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
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
            try {
                Glide.with(imageButton.context)
                    .load(model.artworkUrl100)
                    .transform(
                        RoundedCornersTransformation(2, 0)
                    )
                    .into(imageButton)
            } catch (e: Exception) {
                Glide.with(imageButton.context)
                    .load(R.drawable.baseline_sync_problem_24)
                    .transform(
                        RoundedCornersTransformation(2, 0)
                    )
                    .into(imageButton)
            }
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
        }
    }
}