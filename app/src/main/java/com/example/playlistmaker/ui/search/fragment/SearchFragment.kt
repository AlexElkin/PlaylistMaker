package com.example.playlistmaker.ui.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.ui.search.jc.SearchScreen
import com.example.playlistmaker.ui.search.jc.SearchTheme
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                val isDarkTheme = when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
                    android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
                    android.content.res.Configuration.UI_MODE_NIGHT_NO -> false
                    android.content.res.Configuration.UI_MODE_NIGHT_UNDEFINED -> false
                    else -> false
                }
                SearchTheme(
                    darkTheme = isDarkTheme,
                    content = {
                        SearchScreen(
                            viewModel = viewModel,
                            onTrackClick = { track ->
                                val bundle = Bundle().apply {
                                    putParcelable(TRACK, track)
                                }
                                findNavController().navigate(
                                    R.id.action_searchFragment_to_audioPlayerFragment,
                                    bundle
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}