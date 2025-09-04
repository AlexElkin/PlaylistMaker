package com.example.playlistmaker.ui.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.search.Track
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class TrackAdapter(
    private var tracks: List<Track>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TrackAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(track: Track)
        fun onItemLongClick(track: Track)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val imageButton: ImageButton = itemView.findViewById(R.id.track_view_logo)

        fun bind(track: Track) {
            // Установка текста
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = formatTrackTime(track.trackTimeMillis)

            // Настройка ellipsize
            trackName.ellipsize = TextUtils.TruncateAt.END
            artistName.ellipsize = TextUtils.TruncateAt.END
            trackTime.ellipsize = TextUtils.TruncateAt.END

            // Загрузка изображения с помощью Glide
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.baseline_sync_problem_24)
                .error(R.drawable.baseline_sync_problem_24)
                .transform(RoundedCornersTransformation(
                    dpToPx(itemView.context, 2),0))
                .into(imageButton)
        }
        private fun formatTrackTime(milliseconds: Long): String {
            val totalSeconds = milliseconds / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
        fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return ViewHolder(view)
    }



    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(track)
        }
        holder.itemView.setOnLongClickListener {
            onItemClickListener.onItemLongClick(track)
            true
        }
    }
}
