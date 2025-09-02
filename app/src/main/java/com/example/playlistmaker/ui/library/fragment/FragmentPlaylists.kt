package com.example.playlistmaker.ui.library.fragment

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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.data.PLAYLIST
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.ui.library.adapter.PlaylistsAdapter
import com.example.playlistmaker.ui.library.view_model.FragmentPlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FragmentPlaylistsViewModel by viewModel()
    private val playlistDbInteractor: PlaylistDbInteractor by inject()

    private lateinit var adapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = 1
        }
        binding.recyclerView.layoutManager = layoutManager
        adapter = PlaylistsAdapter(
            emptyList(),
            onItemClickListener = { playlist -> viewModel.onPlaylistClicked(playlist) }
        )
        observeViewModel()
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupResultListener()
        loadPlaylists()
    }

    private fun loadPlaylists() {
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false
        binding.noTracks.isVisible = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val playlists = playlistDbInteractor.getPlaylists()

                if (playlists.isNotEmpty()) {
                    binding.recyclerView.isVisible = true
                    binding.noTracks.isVisible = false
                } else {
                    binding.recyclerView.isVisible = false
                    binding.noTracks.isVisible = true
                }

                adapter.updatePlaylists(playlists)

            } catch (e: Exception) {
                binding.recyclerView.isVisible = false
                binding.noTracks.isVisible = true
                Toast.makeText(requireContext(), "Ошибка загрузки плейлистов", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.isVisible = false
            }
        }
    }
    private fun setupClickListeners() {
        binding.buttonNewPlaylists.setOnClickListener {
            requireActivity().supportFragmentManager.setFragmentResult("navigate_to_new_playlist", bundleOf())
        }
    }

    private fun setupResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener("new_playlist_request", this) { requestKey, bundle ->
            if (requestKey == "new_playlist_request") {
                val title = bundle.getString("playlist_title", "")
                if (title.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Создан плейлист: $title", Toast.LENGTH_SHORT).show()
                    loadPlaylists()
                }
            }
        }
    }

    private fun observeViewModel() {
        parentFragmentManager.setFragmentResultListener("playlist_changed", viewLifecycleOwner) { requestKey, _ ->
            if (requestKey == "playlist_changed") {
                viewLifecycleOwner.lifecycleScope.launch {
                adapter.updatePlaylists(playlistDbInteractor.getPlaylists())}
                }
            }


        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                val bundle = bundleOf(PLAYLIST to playlist)
                requireParentFragment().findNavController().navigate(
                    R.id.action_library_fragment_to_fragment_playlist,
                    bundle
                )
            viewModel.onPlayerNavigated()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FragmentPlaylists()
    }
}