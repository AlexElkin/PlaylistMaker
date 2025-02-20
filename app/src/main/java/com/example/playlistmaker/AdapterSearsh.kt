package com.example.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AdapterSearsh(
    private val tracks: List<Track>
) : RecyclerView.Adapter<AdapterSearsh.ViewHolderSearsh>() {
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

        fun bind(model: Track) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = model.trackTime
            try {
                Glide.with(imageButton.context).load(model.artworkUrl100).transform(
                    RoundedCornersTransformation(2, 0)
                ).into(imageButton)
            } catch (e: Exception) {
                imageButton.setImageResource(R.drawable.baseline_sync_problem_24)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSearsh {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return ViewHolderSearsh(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: ViewHolderSearsh, position: Int) {
        holder.bind(tracks[position])
    }
}