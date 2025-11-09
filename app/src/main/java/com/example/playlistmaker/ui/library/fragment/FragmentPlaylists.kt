package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.data.PLAYLIST
import com.example.playlistmaker.data.library.Playlists
import com.example.playlistmaker.domain.db.PlaylistDbInteractor
import com.example.playlistmaker.ui.library.jc.LibraryTheme
import com.example.playlistmaker.ui.library.jc.PlaylistItem
import com.example.playlistmaker.ui.library.view_model.FragmentPlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists : Fragment() {

    private val viewModel: FragmentPlaylistsViewModel by viewModel()
    private val playlistDbInteractor: PlaylistDbInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LibraryTheme {
                    PlaylistsScreen()
                }
            }
        }
    }

    @Composable
    private fun PlaylistsScreen() {
        var playlists by remember { mutableStateOf(emptyList<Playlists>()) }
        var isLoading by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            loadPlaylists(
                onLoading = { loading -> isLoading = loading },
                onSuccess = { playlistList ->
                    playlists = playlistList
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Button(
                onClick = {
                    requireActivity().supportFragmentManager.setFragmentResult(
                        "navigate_to_new_playlist",
                        bundleOf()
                    )
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(70.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(54.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_playlists),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (playlists.isEmpty()) {
                EmptyPlaylistsState()
            } else {
                PlaylistsGrid(
                    playlists = playlists,
                    onPlaylistClick = { playlist -> viewModel.onPlaylistClicked(playlist) }
                )
            }
        }
    }

    @Composable
    private fun EmptyPlaylistsState() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 46.dp),
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
                text = stringResource(R.string.you_havent_created_any_playlists),
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
    private fun PlaylistsGrid(
        playlists: List<Playlists>,
        onPlaylistClick: (Playlists) -> Unit
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 8.dp, start = 4.dp, end = 4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(playlists) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onItemClick = onPlaylistClick
                )
            }
        }
    }
    private fun loadPlaylists(
        onLoading: (Boolean) -> Unit,
        onSuccess: (List<Playlists>) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            onLoading(true)
            try {
                val playlists = playlistDbInteractor.getPlaylists()
                onSuccess(playlists)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка загрузки плейлистов", Toast.LENGTH_SHORT).show()
                onSuccess(emptyList())
            } finally {
                onLoading(false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupResultListener()
        observeViewModel()
    }

    private fun setupResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            "new_playlist_request",
            this
        ) { requestKey, bundle ->
            if (requestKey == "new_playlist_request") {
                val title = bundle.getString("playlist_title", "")
                if (title.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Создан плейлист: $title", Toast.LENGTH_SHORT).show()
                    loadPlaylists(
                        onLoading = { },
                        onSuccess = { }
                    )
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                val bundle = bundleOf(PLAYLIST to playlist)
                findNavController().navigate(
                    R.id.action_library_fragment_to_fragment_playlist,
                    bundle
                )
                viewModel.onPlayerNavigated()
            }
        }
    }

    companion object {
        fun newInstance() = FragmentPlaylists()
    }
}