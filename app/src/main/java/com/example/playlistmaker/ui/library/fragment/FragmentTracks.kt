package com.example.playlistmaker.ui.library.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentTracksBinding
import com.example.playlistmaker.ui.library.view_model.FragmentPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentTracks: Fragment() {

    companion object {
        fun newInstance() = FragmentTracks()
    }
    private lateinit var binding: FragmentTracksBinding
    private val viewModel: FragmentPlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
}