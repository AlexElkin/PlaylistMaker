package com.example.playlistmaker.ui.library.jc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.library.Playlists

@Composable
fun PlaylistItem(
    playlist: Playlists,
    onItemClick: (Playlists) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onItemClick(playlist) },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = playlist.picture,
                    contentDescription = "Обложка плейлиста ${playlist.title}",
                    placeholder = painterResource(id = R.drawable.placeholder2),
                    error = painterResource(id = R.drawable.placeholder2),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = playlist.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )


            Text(
                text = getTracksCountText(playlist.countTracks),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun getTracksCountText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "$count трек"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "$count трека"
        else -> "$count треков"
    }
}