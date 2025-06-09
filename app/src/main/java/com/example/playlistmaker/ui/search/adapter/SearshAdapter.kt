package com.example.playlistmaker.ui.search.adapter

import android.annotation.SuppressLint
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

class SearchAdapter(
    private var tracks: List<Track>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(track: Track)
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
            trackTime.text = track.trackTimeMillis.toString()

            // Настройка максимального количества строк
            trackName.maxLines = 1
            artistName.maxLines = 1
            trackTime.maxLines = 1

            // Настройка ellipsize
            trackName.ellipsize = TextUtils.TruncateAt.END
            artistName.ellipsize = TextUtils.TruncateAt.END
            trackTime.ellipsize = TextUtils.TruncateAt.END

            // Загрузка изображения с помощью Glide
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.baseline_sync_problem_24)
                .error(R.drawable.baseline_sync_problem_24)
                .transform(RoundedCornersTransformation(2, 0))
                .into(imageButton)
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
    }
}
