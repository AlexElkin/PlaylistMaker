package com.example.playlistmaker.ui.library.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.data.PLAYLIST
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.FragmentPlaylist1Binding
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.domain.db.TracksInPlaylistDbInteractor
import com.example.playlistmaker.ui.library.view_model.FragmentPlaylistViewModel
import com.example.playlistmaker.ui.search.adapter.SearchAdapter
import com.example.playlistmaker.ui.search.view_model.SearchState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylist : Fragment() {
    private lateinit var binding: FragmentPlaylist1Binding
    private val viewModel: FragmentPlaylistViewModel by viewModel()
    private val tracksDbInteractor: TracksDbInteractor by inject()
    private val playlistDbInteractor: PlaylistDbInteractor by inject()
    private val tracksInPlaylistDbInteractor: TracksInPlaylistDbInteractor by inject()

    private val adapter = SearchAdapter(
        emptyList(), onItemClickListener = { track -> viewModel.onTrackClicked(track)}
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylist1Binding.inflate(inflater, container, false)
        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }
        setupRecyclerView()
        observeViewModel()
        return binding.root

    }

    private fun getPlaylist(): Playlists {
        val args = arguments ?: throw IllegalStateException("Arguments not found")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable(PLAYLIST, Playlists::class.java)
        } else {
            @Suppress("DEPRECATION")
            args.getParcelable(PLAYLIST)
        } ?: throw IllegalStateException("Track not found")
    }
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            val trackIds = tracksInPlaylistDbInteractor.getIdTrackInPlaylist(
                playlistDbInteractor.getIdPlaylist(getPlaylist().title)
            )

            val tracks = tracksDbInteractor.getTracks(trackIds).first()
            adapter.updateTracks(tracks)
        }

    }

    private fun observeViewModel() {
        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            track?.let {
                val bundle = bundleOf(TRACK to track)
                findNavController().navigate(
                    R.id.action_fragmentPlaylist_to_audio_player_fragment,
                    bundle
                )
                viewModel.onPlayerNavigated()
            }
        }
    }
}