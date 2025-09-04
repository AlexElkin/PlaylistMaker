package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.FragmentTracksBinding
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.ui.library.adapter.TrackAdapter
import com.example.playlistmaker.ui.library.view_model.FragmentTracksViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentTracks : Fragment() {

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FragmentTracksViewModel by viewModel()

    private val tracksDbInteractor: TracksDbInteractor by inject()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)
        adapter = TrackAdapter(
            emptyList(), onItemClickListener = viewModel
        )
        setupRecyclerView()
        observeTracks()
        observeViewModel()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeTracks() {
        viewLifecycleOwner.lifecycleScope.launch {
            tracksDbInteractor.getLikeTrack(true).collectLatest { tracks ->
                adapter.updateTracks(tracks.reversed())
                updateUI(tracks)
            }
        }
    }

    private fun updateUI(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            binding.recyclerView.isGone = true
            binding.noTracks.isVisible = true
        } else {
            binding.recyclerView.isVisible = true
            binding.noTracks.isGone = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FragmentTracks()
    }

    private fun observeViewModel() {
        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            track?.let {
                (parentFragment as? LibraryFragment)?.navigateToPlayer(it)
                viewModel.onPlayerNavigated()
            }
        }
    }
}