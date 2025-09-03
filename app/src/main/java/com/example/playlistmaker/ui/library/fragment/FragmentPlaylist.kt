package com.example.playlistmaker.ui.library.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.PLAYLIST
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.FragmentPlaylist1Binding
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.domain.db.TracksInPlaylistDbInteractor
import com.example.playlistmaker.ui.library.adapter.TrackAdapter
import com.example.playlistmaker.ui.library.view_model.FragmentPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylist : Fragment() {
    private var _binding: FragmentPlaylist1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: FragmentPlaylistViewModel by viewModel()
    private val tracksDbInteractor: TracksDbInteractor by inject()
    private val playlistDbInteractor: PlaylistDbInteractor by inject()
    private val tracksInPlaylistDbInteractor: TracksInPlaylistDbInteractor by inject()
    private lateinit var adapter: TrackAdapter
    private lateinit var playlist: Playlists
    private lateinit var bottomSheetBehavior:  BottomSheetBehavior<MaterialCardView>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylist1Binding.inflate(inflater, container, false)
        val bottomSheet = binding.bottomSheetSettingPlaylist
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        playlist = getPlaylist()
        adapter = TrackAdapter(
            tracks = emptyList(), onItemClickListener = viewModel
        )
        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }
        setPlaylist()
        setupRecyclerView()
        observeViewModel()
        binding.share.setOnClickListener {share()}
        menu()
        updatePlaylist()
        return binding.root

    }
    private fun updateBottomSheet() {
        binding.playlistsView.playlistName2.text = playlist.title
        binding.playlistsView.tracksCount2.text = getTracksCount(playlist.countTracks)
        Glide.with(requireContext())
            .load(playlist.picture)
            .placeholder(R.drawable.placeholder)
            .into(binding.playlistsView.playlistIcon2)
    }
    private fun updatePlaylist(){
        parentFragmentManager.setFragmentResultListener("playlist_changed", viewLifecycleOwner) { requestKey, bundle ->
            if (requestKey == "playlist_changed") {
                val updatedPlaylist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable("playlist_object", Playlists::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    bundle.getParcelable("playlist_object") as? Playlists
                }

                updatedPlaylist?.let {
                    playlist = it
                    Glide.with(requireContext())
                        .load(playlist.picture)
                        .placeholder(R.drawable.placeholder)
                        .into(binding.playlistIcon)
                    binding.playlistName.text = playlist.title
                    binding.description.text = playlist.description
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.trackTimeView.text = getSumTracksTime(playlist)
                        val idPlaylist = playlistDbInteractor.getIdPlaylist(playlist.title)
                        val trackIds = tracksInPlaylistDbInteractor.getIdTrackInPlaylist(idPlaylist)
                        val tracks = tracksDbInteractor.getTracks(trackIds).first()
                        updateAdapter(tracks)
                    }
                    binding.countTrackInPlaylist.text = getTracksCount(playlist.countTracks)

                    parentFragmentManager.setFragmentResult("playlist_changed", Bundle())
                }

            }
        }
    }

    private fun updateAdapter(tracks:  List<Track>){
        if (tracks.isEmpty()){
            binding.recyclerView.isVisible = false
            binding.noTracks.isVisible = true
        }else {
            binding.recyclerView.isVisible = true
            binding.noTracks.isVisible = false
            adapter.updateTracks(tracks.reversed())}
    }

    private fun formatTrackTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun menu() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.textViewShare.setOnClickListener { share() }
        binding.editInformation.setOnClickListener {
            val bundle = bundleOf(PLAYLIST to playlist)
            requireParentFragment().findNavController().navigate(
                R.id.action_fragmentPlaylist_to_updatePlaylistFragment,
                bundle
            ) }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.backgroundBlack2.isVisible = false
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        binding.deletePlaylist.setOnClickListener { showDeletePlaylistDialog() }
        binding.points.setOnClickListener {
            binding.backgroundBlack2.isVisible = true
            updateBottomSheet()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setPlaylist() {
        Glide.with(requireContext())
            .load(playlist.picture)
            .placeholder(R.drawable.placeholder)
            .into(binding.playlistIcon)
        binding.playlistName.text = playlist.title
        binding.description.text = playlist.description
        viewLifecycleOwner.lifecycleScope.launch {
            binding.trackTimeView.text = getSumTracksTime(playlist)
        }
        binding.countTrackInPlaylist.text = getTracksCount(playlist.countTracks)
    }

    private suspend fun getSumTracksTime(playlist: Playlists): String {
        return tracksInPlaylistDbInteractor.getSumTracksTime(playlist)
    }

    private fun share() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (playlist.countTracks < 1) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_tracks_in_playlist_share), Toast.LENGTH_SHORT).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                val trackIds = tracksInPlaylistDbInteractor.getIdTrackInPlaylist(
                    playlistDbInteractor.getIdPlaylist(playlist.title)
                )
                val tracks = tracksDbInteractor.getTracks(trackIds).first()
                val tracksCount = getTracksCount(playlist.countTracks)
                var msg = "${playlist.title}\n${playlist.description}\n$tracksCount\n"
                for ((index, value) in tracks.withIndex()) {
                    msg += "${index + 1}. ${value.artistName} - ${value.trackName} (${formatTrackTime(value.trackTimeMillis)}).\n"
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, msg)
                    type = "text/plain"
                }
                startActivity(intent)
            }
        }
    }

    private fun getTracksCount(countTracks: Int) = when {
        countTracks % 10 == 1 && countTracks % 100 != 11 ->
            "${countTracks} трек"

        countTracks % 10 in 2..4 && countTracks % 100 !in 12..14 ->
            "${countTracks} трека"

        else ->
            "${countTracks} треков"
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
            updateAdapter(tracks)
        }

    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist))
            .setMessage(getString(R.string.do_you_want_to_delete_a_playlist))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val idPlaylist = playlistDbInteractor.getIdPlaylist(playlist.title)
                    tracksInPlaylistDbInteractor.deletePlaylist(idPlaylist)
                    parentFragmentManager.setFragmentResult("playlist_changed", Bundle())
                    findNavController().popBackStack()}
            }
            .show()
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_track))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()

                viewLifecycleOwner.lifecycleScope.launch {
                    val idPlaylist = playlistDbInteractor.getIdPlaylist(getPlaylist().title)
                    tracksInPlaylistDbInteractor.deleteTrackEntity(
                        tracksDbInteractor.getIdTrack(
                            track.previewUrl
                        ), idPlaylist
                    )
                    val trackIds = tracksInPlaylistDbInteractor.getIdTrackInPlaylist(idPlaylist)
                    val tracks = tracksDbInteractor.getTracks(trackIds).first()
                    playlist = playlistDbInteractor.getPlaylistById(idPlaylist)
                    updateAdapter(tracks)
                }
            }
            .show()
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
        viewModel.deleteTrack.observe(viewLifecycleOwner) { track ->
            track?.let {
                showDeleteTrackDialog(track)
                viewModel.onNullDeleteTrack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}