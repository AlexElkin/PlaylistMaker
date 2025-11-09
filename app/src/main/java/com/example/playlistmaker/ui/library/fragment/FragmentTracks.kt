package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.domain.db.TracksDbInteractor
import com.example.playlistmaker.ui.library.jc.LibraryTheme
import com.example.playlistmaker.ui.search.jc.TrackItem
import com.example.playlistmaker.ui.library.view_model.FragmentTracksViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentTracks : Fragment() {

    private val viewModel: FragmentTracksViewModel by viewModel()
    private val tracksDbInteractor: TracksDbInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LibraryTheme {
                    TracksScreen()
                }
            }
        }
    }

    @Composable
    private fun TracksScreen() {
        var tracks by remember { mutableStateOf(emptyList<Track>()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            viewLifecycleOwner.lifecycleScope.launch {
                tracksDbInteractor.getLikeTrack(true).collectLatest { trackList ->
                    tracks = trackList.reversed()
                    isLoading = false
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (tracks.isEmpty()) {
            EmptyTracksState()
        } else {
            TracksList(tracks = tracks)
        }
    }

    @Composable
    private fun EmptyTracksState() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 106.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.no_tracks),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = androidx.compose.ui.graphics.Color.Unspecified
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.library_empty),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun TracksList(tracks: List<Track>) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            items(tracks) { track ->
                TrackItem(
                    track = track,
                    onItemClick = { viewModel.onItemClick(track) },
                    onItemLongClick = { viewModel.onItemLongClick(track) }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            track?.let {
                (parentFragment as? LibraryFragment)?.navigateToPlayer(it)
                viewModel.onPlayerNavigated()
            }
        }
    }

    companion object {
        fun newInstance() = FragmentTracks()
    }
}