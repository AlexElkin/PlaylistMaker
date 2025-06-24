package com.example.playlistmaker.ui.search.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.ui.player.activity.AudioPlayerActivity
import com.example.playlistmaker.ui.search.adapter.SearchAdapter
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private val adapter = SearchAdapter(
        emptyList(), onItemClickListener = { track -> viewModel.onTrackClicked(track)}
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchField()
        setupClickListeners()
        observeViewModel()

        viewModel.showHistory()
    }

    private fun setupRecyclerView() {
        binding.activitySearchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.activitySearchRecyclerView.adapter = adapter
    }

    private fun setupSearchField() {
        binding.activitySearchEditText.addTextChangedListener(createTextWatcher())
        binding.activitySearchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.activitySearchEditText.hideKeyboard()
                viewModel.performSearch(binding.activitySearchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }
    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setupClickListeners() {
        binding.activitySearchButtonBack.setOnClickListener { finish() }
        binding.activitySearchClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.activitySearchClearIcon.setOnClickListener {
            binding.activitySearchEditText.setText("")
            binding.activitySearchEditText.hideKeyboard()
            viewModel.showHistory()
        }
        binding.activitySearchButtonUpdate.setOnClickListener {
            viewModel.performSearch(binding.activitySearchEditText.text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                SearchState.Default -> showDefaultState()
                SearchState.Loading -> showLoading()
                SearchState.Empty -> showEmptyState()
                SearchState.NoInternet -> showNoInternetError()
                SearchState.Error -> showUnknownError()
                is SearchState.Content -> showTracks(state.tracks)
                is SearchState.History -> showHistory(state.tracks)
            }
        }

        viewModel.navigateToPlayer.observe(this) { track ->
            track?.let {
                startActivity(
                    Intent(this, AudioPlayerActivity::class.java).apply {
                        putExtra(TRACK, track)
                    }
                )
                viewModel.onPlayerNavigated()
            }
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.activitySearchClearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounced(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun showDefaultState() {
        with(binding) {
            progressBar.isVisible = false
            activitySearchRecyclerView.isVisible = false
            activitySearchImageViewError.isVisible = false
            activitySearchTextViewError.isVisible = false
            activitySearchButtonUpdate.isVisible = false
            activitySearchTextViewYouWereLooking.isVisible = false
            activitySearchClearHistory.isVisible = false
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            activitySearchRecyclerView.isVisible = false
            activitySearchImageViewError.isVisible = false
            activitySearchTextViewError.isVisible = false
            activitySearchButtonUpdate.isVisible = false
            activitySearchTextViewYouWereLooking.isVisible = false
            activitySearchClearHistory.isVisible = false
        }
    }

    private fun showEmptyState() {
        with(binding) {
            progressBar.isVisible = false
            activitySearchRecyclerView.isVisible = false
            activitySearchImageViewError.isVisible = true
            activitySearchTextViewError.isVisible = true
            activitySearchButtonUpdate.isVisible = false
            activitySearchTextViewYouWereLooking.isVisible = false
            activitySearchClearHistory.isVisible = false

            activitySearchImageViewError.setImageResource(R.drawable.no_tracks)
            activitySearchTextViewError.setText(R.string.no_track)
        }
    }

    private fun showNoInternetError() {
        with(binding) {
            progressBar.isVisible = false
            activitySearchRecyclerView.isVisible = false
            activitySearchImageViewError.isVisible = true
            activitySearchTextViewError.isVisible = true
            activitySearchButtonUpdate.isVisible = true
            activitySearchTextViewYouWereLooking.isVisible = false
            activitySearchClearHistory.isVisible = false

            activitySearchImageViewError.setImageResource(R.drawable.no_internet)
            activitySearchTextViewError.setText(R.string.no_internet)
        }
    }

    private fun showUnknownError() {
        // Реализация для неизвестной ошибки
    }

    private fun showTracks(tracks: List<Track>) {
        with(binding) {
            progressBar.isVisible = false
            activitySearchRecyclerView.isVisible = true
            activitySearchImageViewError.isVisible = false
            activitySearchTextViewError.isVisible = false
            activitySearchButtonUpdate.isVisible = false
            activitySearchTextViewYouWereLooking.isVisible = false
            activitySearchClearHistory.isVisible = false
        }
        adapter.updateTracks(tracks)
    }

    private fun showHistory(tracks: List<Track>) {
        with(binding) {
            progressBar.isVisible = false
            activitySearchRecyclerView.isVisible = true
            activitySearchImageViewError.isVisible = false
            activitySearchTextViewError.isVisible = false
            activitySearchButtonUpdate.isVisible = false
            activitySearchTextViewYouWereLooking.isVisible = tracks.isNotEmpty()
            activitySearchClearHistory.isVisible = tracks.isNotEmpty()
        }
        adapter.updateTracks(tracks)
    }
}