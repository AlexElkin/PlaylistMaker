package com.example.playlistmaker.ui.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.data.library.Playlists

class PlaylistsAdapter(
    private var playlists: List<Playlists>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(playlists: Playlists)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylists(newPlaylists: List<Playlists>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
        private val tracksCount: TextView = itemView.findViewById(R.id.tracks_count)
        private val imageButton: ImageView = itemView.findViewById(R.id.playlist_icon)

        fun bind(playlists: Playlists) {
            playlistName.text = playlists.title
            playlistName.ellipsize = TextUtils.TruncateAt.END
            tracksCount.text = when {
                playlists.countTracks % 10 == 1 && playlists.countTracks % 100 != 11 ->
                    "${playlists.countTracks} трек"
                playlists.countTracks % 10 in 2..4 && playlists.countTracks % 100 !in 12..14 ->
                    "${playlists.countTracks} трека"
                else ->
                    "${playlists.countTracks} треков"
            }

            Glide.with(itemView.context)
                .load(playlists.picture)
                .placeholder(R.drawable.placeholder2)
                .into(imageButton)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlists_view, parent, false)
        return ViewHolder(view)
    }



    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlists = playlists[position]
        holder.bind(playlists)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(playlists)
        }
    }
}
