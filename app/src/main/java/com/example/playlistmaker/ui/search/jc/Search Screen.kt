package com.example.playlistmaker.ui.search.jc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val navigateToPlayer by viewModel.navigateToPlayer.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.showHistory()
    }

    LaunchedEffect(navigateToPlayer) {
        navigateToPlayer?.let { track ->
            onTrackClick(track)
            viewModel.onPlayerNavigated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SearchHeader()
        SearchField(
            viewModel = viewModel,
            onSearchAction = { viewModel.performSearch(it) }
        )
        SearchContent(
            state = state,
            onClearHistory = viewModel::clearHistory,
            onTrackClick = viewModel::onTrackClick,
            viewModel = viewModel
        )
    }
}

@Composable
private fun SearchHeader() {
    Text(
        text = stringResource(R.string.search),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
private fun SearchField(
    viewModel: SearchViewModel,
    onSearchAction: (String) -> Unit
) {
    val searchText by viewModel.searchText.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.magnifier),
                contentDescription = stringResource(R.string.search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = searchText,
                onValueChange = { newText ->
                    viewModel.updateSearchText(newText)
                    viewModel.searchDebounced(newText)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onSearchAction(searchText)
                    }
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (searchText.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.clearSearch()
                        keyboardController?.hide()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clear),
                        contentDescription = stringResource(R.string.clear),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchContent(
    state: SearchState,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit,
    viewModel: SearchViewModel
) {
    when (state) {
        SearchState.Default -> viewModel.showHistory()
        SearchState.Loading -> LoadingState()
        SearchState.Empty -> EmptyState()
        SearchState.NoInternet -> {
            ErrorContent(
                imageRes = R.drawable.no_internet,
                textRes = R.string.no_internet,
                showButton = true,
                onRetry = {
                    if (viewModel.isOnline()) {
                        val currentQuery = viewModel.getCurrentQuery()
                        if (currentQuery.isNotEmpty()) {
                            viewModel.performSearch(currentQuery)
                        }
                    }
                }
            )
        }
        SearchState.Error -> {
            ErrorContent(
                imageRes = R.drawable.no_internet,
                textRes = R.string.no_internet,
                showButton = true,
                onRetry = {
                    if (viewModel.isOnline()) {
                        val currentQuery = viewModel.getCurrentQuery()
                        if (currentQuery.isNotEmpty()) {
                            viewModel.performSearch(currentQuery)
                        }
                    }
                }
            )
        }
        is SearchState.Content -> TrackListState(
            tracks = state.tracks,
            onTrackClick = onTrackClick
        )
        is SearchState.History -> HistoryState(
            tracks = state.tracks,
            onClearHistory = onClearHistory,
            onTrackClick = onTrackClick
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(44.dp)
        )
    }
}

@Composable
private fun EmptyState() {
    ErrorContent(
        imageRes = R.drawable.no_tracks,
        textRes = R.string.no_track
    )
}

@Composable
private fun ErrorContent(
    imageRes: Int,
    textRes: Int,
    showButton: Boolean = false,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        if (showButton) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.update))
            }
        }
    }
}

@Composable
private fun TrackListState(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(tracks) { track ->
            TrackItem(
                track = track,
                onTrackClick = onTrackClick
            )
            androidx.compose.material3.Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun HistoryState(
    tracks: List<Track>,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (tracks.isNotEmpty()) {
            Text(
                text = stringResource(R.string.You_were_looking),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 12.dp),
                textAlign = TextAlign.Center
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(tracks) { track ->
                TrackItem(
                    track = track,
                    onTrackClick = onTrackClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                androidx.compose.material3.Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            if (tracks.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Button(
                            onClick = onClearHistory,
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(48.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.background,
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.clear_history),
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}