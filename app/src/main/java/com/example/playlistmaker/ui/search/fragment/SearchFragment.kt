package com.example.playlistmaker.ui.search.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TRACK
import com.example.playlistmaker.data.search.Track
import com.example.playlistmaker.databinding.SearchFragmentBinding
import com.example.playlistmaker.ui.library.adapter.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.SearchState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment(){
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        adapter = TrackAdapter(
            emptyList(), onItemClickListener = viewModel
        )
        setupRecyclerView()
        setupSearchField()
        setupClickListeners()
        observeViewModel()

        viewModel.showHistory()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchField() {
        binding.editText.addTextChangedListener(createTextWatcher())
        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.editText.hideKeyboard()
                viewModel.performSearch(binding.editText.text.toString())
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
        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.clearIcon.setOnClickListener {
            binding.editText.setText("")
            binding.editText.hideKeyboard()
            viewModel.showHistory()
        }
        binding.buttonUpdate.setOnClickListener {
            viewModel.performSearch(binding.editText.text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
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

        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            track?.let {
                val bundle = bundleOf(TRACK to track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_audioPlayerFragment,
                    bundle
                )
                viewModel.onPlayerNavigated()
            }
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounced(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun showDefaultState() {
        with(binding) {
            progressBar.isVisible = false
            recyclerView.isVisible = false
            imageViewError.isVisible = false
            textViewError.isVisible = false
            buttonUpdate.isVisible = false
            textViewYouWereLooking.isVisible = false
            clearHistory.isVisible = false
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            recyclerView.isVisible = false
            imageViewError.isVisible = false
            textViewError.isVisible = false
            buttonUpdate.isVisible = false
            textViewYouWereLooking.isVisible = false
            clearHistory.isVisible = false
        }
    }

    private fun showEmptyState() {
        with(binding) {
            progressBar.isVisible = false
            recyclerView.isVisible = false
            imageViewError.isVisible = true
            textViewError.isVisible = true
            buttonUpdate.isVisible = false
            textViewYouWereLooking.isVisible = false
            clearHistory.isVisible = false

            imageViewError.setImageResource(R.drawable.no_tracks)
            textViewError.setText(R.string.no_track)
        }
    }

    private fun showNoInternetError() {
        with(binding) {
            progressBar.isVisible = false
            recyclerView.isVisible = false
            imageViewError.isVisible = true
            textViewError.isVisible = true
            buttonUpdate.isVisible = true
            textViewYouWereLooking.isVisible = false
            clearHistory.isVisible = false

            imageViewError.setImageResource(R.drawable.no_internet)
            textViewError.setText(R.string.no_internet)
        }
    }

    private fun showUnknownError() {
        // Реализация для неизвестной ошибки
    }

    private fun showTracks(tracks: List<Track>) {
        with(binding) {
            progressBar.isVisible = false
            recyclerView.isVisible = true
            imageViewError.isVisible = false
            textViewError.isVisible = false
            buttonUpdate.isVisible = false
            textViewYouWereLooking.isVisible = false
            clearHistory.isVisible = false
        }
        adapter.updateTracks(tracks)
    }

    private fun showHistory(tracks: List<Track>) {
        with(binding) {
            progressBar.isVisible = false
            recyclerView.isVisible = true
            imageViewError.isVisible = false
            textViewError.isVisible = false
            buttonUpdate.isVisible = false
            textViewYouWereLooking.isVisible = tracks.isNotEmpty()
            clearHistory.isVisible = tracks.isNotEmpty()
        }
        adapter.updateTracks(tracks)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}