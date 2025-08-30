package com.example.playlistmaker.ui.player.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.library.Playlists
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AudioPlayerAdapter (
    private var playlists: List<Playlists>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AudioPlayerAdapter.ViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(playlists: Playlists)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylists(newPlaylists: List<Playlists>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playlistName: TextView = itemView.findViewById(R.id.playlist_name2)
        private val tracksCount: TextView = itemView.findViewById(R.id.tracks_count2)
        private val imageButton: ImageView = itemView.findViewById(R.id.playlist_icon2)



        fun bind(playlists: Playlists) {
            // Установка текста

            playlistName.text = playlists.title
            tracksCount.text = when {
                playlists.countTracks % 10 == 1 && playlists.countTracks % 100 != 11 ->
                    "${playlists.countTracks} трек"
                playlists.countTracks % 10 in 2..4 && playlists.countTracks % 100 !in 12..14 ->
                    "${playlists.countTracks} трека"
                else ->
                    "${playlists.countTracks} треков"
            }
            if (playlists.picture.isEmpty()) {
                imageButton.setImageResource(R.drawable.placeholder)
            } else {
                Glide.with(itemView.context)
                    .load(playlists.picture)
                    .placeholder(R.drawable.placeholder)
                    .transform(RoundedCornersTransformation(dpToPx(itemView.context, 2), 0))
                    .into(imageButton)
            }
        }

        fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlists_view2, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(playlist)
        }
    }
}
