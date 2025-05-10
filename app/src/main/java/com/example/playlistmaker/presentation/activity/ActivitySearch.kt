package com.example.playlistmaker.presentation.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.utils.Debouncer
import com.example.playlistmaker.R
import com.example.playlistmaker.data.BUTTON_UPDATE_VISIBILITY
import com.example.playlistmaker.data.ENTERED_TEXT
import com.example.playlistmaker.data.IMAGE_ERROR_IMAGE_RESOURCE
import com.example.playlistmaker.data.IMAGE_ERROR_VISIBILITY
import com.example.playlistmaker.data.NoInternetException
import com.example.playlistmaker.data.RECYCLER_VISIBILITY
import com.example.playlistmaker.data.SEARCH_DEBOUNCE_DELAY
import com.example.playlistmaker.data.TEXT_ERROR_RESOURCE
import com.example.playlistmaker.data.TEXT_ERROR_VISIBILITY
import com.example.playlistmaker.data.dto.Track
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.presentation.adapter.AdapterSearsh
import kotlinx.coroutines.launch

class ActivitySearch : AppCompatActivity(), AdapterSearsh.OnItemClickListener {

    private var countValue: String = ""
    private lateinit var buttonBack: ImageButton
    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AdapterSearsh
    private lateinit var imageError: ImageView
    private lateinit var buttonUpdate: Button
    private lateinit var clearHistory: Button
    private lateinit var textError: TextView
    private lateinit var textYouWereLooking: TextView
    private lateinit var progressBar: ProgressBar
    private var imageResource: Int = R.drawable.no_tracks
    private var textResource: Int = R.string.no_track
    private val debouncer = Debouncer(SEARCH_DEBOUNCE_DELAY)
    private val trackInteractor = Creator.provideTrackInteractor()
    private val sharedPreferences by lazy {
        getSharedPreferences("playlist_maker_prefs", MODE_PRIVATE)
    }
    private val searchHistory by lazy {
        SearchHistory(sharedPreferences)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTERED_TEXT, countValue)
        outState.putInt(RECYCLER_VISIBILITY, recycler.visibility)
        outState.putInt(IMAGE_ERROR_VISIBILITY, imageError.visibility)
        outState.putInt(IMAGE_ERROR_IMAGE_RESOURCE, imageResource)
        outState.putInt(TEXT_ERROR_VISIBILITY, textError.visibility)
        outState.putInt(TEXT_ERROR_RESOURCE, textResource)
        outState.putInt(BUTTON_UPDATE_VISIBILITY, buttonUpdate.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countValue = savedInstanceState.getString(ENTERED_TEXT, "")
        recycler.visibility = savedInstanceState.getInt(RECYCLER_VISIBILITY, View.VISIBLE)
        imageError.visibility = savedInstanceState.getInt(IMAGE_ERROR_VISIBILITY, View.GONE)
        imageError.setImageResource(
            savedInstanceState.getInt(
                IMAGE_ERROR_IMAGE_RESOURCE,
                imageResource
            )
        )
        textError.visibility = savedInstanceState.getInt(TEXT_ERROR_VISIBILITY, View.GONE)
        buttonUpdate.visibility = savedInstanceState.getInt(BUTTON_UPDATE_VISIBILITY, View.GONE)
        editText.setText(countValue)
        textError.setText(savedInstanceState.getInt(TEXT_ERROR_RESOURCE, textResource))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
        setOnClick()
        editText.addTextChangedListener(createTextWatcher())
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(editText)
                performSearch()
                true
            } else {
                false
            }
        }
        editText.setOnFocusChangeListener { _,hasFocus ->
            if (hasFocus && countValue.isEmpty()) {
                createHistory()
            }
        }

        adapter = AdapterSearsh(emptyList(), this, this)
        startEditText()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun startEditText() {
        editText.requestFocus()
        createHistory()
    }

    private fun createHistory() {
        val tracks = searchHistory.getHistory()
        if (tracks != null && tracks.isNotEmpty()) {
            textYouWereLooking.visibility = View.VISIBLE
            clearHistory.visibility = View.VISIBLE
            recycler.visibility = View.VISIBLE
            adapter.updateTracks(tracks.reversed())
        }
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.activity_search_button_back)
        editText = findViewById(R.id.activity_search_editText)
        clearButton = findViewById(R.id.activity_search_clearIcon)
        recycler = findViewById(R.id.activity_search_recyclerView)
        imageError = findViewById(R.id.activity_search_imageView_Error)
        buttonUpdate = findViewById(R.id.activity_search_button_update)
        textError = findViewById(R.id.activity_search_textView_Error)
        textYouWereLooking = findViewById(R.id.activity_search_TextView_You_were_looking)
        clearHistory = findViewById(R.id.activity_search_clear_history)
        progressBar = findViewById(R.id.progressBar)
    }
    private fun performSearch() {
        progressBar.visibility = View.VISIBLE
        hideErrorStates()

        lifecycleScope.launch {
            try {
                val tracks = trackInteractor.searchTrack(countValue)
                if (tracks.isEmpty()) {
                    showEmptyState()
                } else {
                    showTracks(bringDto(tracks))
                }
            } catch (e: NoInternetException) {
                showNoInternetError()
            } catch (e: Exception) {
                showUnknownError()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun hideErrorStates() {
        recycler.visibility = View.GONE
        imageError.visibility = View.GONE
        textError.visibility = View.GONE
        buttonUpdate.visibility = View.GONE
        textYouWereLooking.visibility = View.GONE
    }

    private fun showEmptyState() {
        imageResource = R.drawable.no_tracks
        imageError.setImageResource(imageResource)
        textResource = R.string.no_track
        textError.setText(textResource)
        imageError.visibility = View.VISIBLE
        textError.visibility = View.VISIBLE
    }

    private fun showNoInternetError() {
        imageResource = R.drawable.no_internet
        imageError.setImageResource(imageResource)
        textResource = R.string.no_internet
        textError.setText(textResource)
        imageError.visibility = View.VISIBLE
        textError.visibility = View.VISIBLE
        buttonUpdate.visibility = View.VISIBLE
    }

    private fun showUnknownError() {
        TODO()
    }

    private fun showTracks(tracks: List<TrackDto>) {
        recycler.visibility = View.VISIBLE
        adapter.updateTracks(tracks)
    }

    private fun setOnClick() {
        buttonBack.setOnClickListener { finish() }
        clearHistory.setOnClickListener {
            textYouWereLooking.visibility = View.GONE
            searchHistory.clearHistory()
            recycler.visibility = View.GONE
            clearHistory.visibility = View.GONE
        }
        clearButton.setOnClickListener {
            editText.setText("")
            clearingScreen()
            createHistory()
            hideKeyboard(editText)
        }
        buttonUpdate.setOnClickListener {
            performSearch()
        }
    }

    private fun clearingScreen() {
        clearButton.visibility = View.GONE
        recycler.visibility = View.GONE
        imageError.visibility = View.GONE
        textError.visibility = View.GONE
        buttonUpdate.visibility = View.GONE
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO:
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    countValue = s.toString()
                    clearButton.visibility = clearButtonVisibility(s)
                    if (s.isEmpty()) {
                        clearingScreen()
                        createHistory()
                    } else {
                        textYouWereLooking.visibility = View.GONE
                        recycler.visibility = View.GONE
                        clearHistory.visibility = View.GONE
                        debouncer.debounce { performSearch() }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // TODO:
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }



    private fun bringDto(tracks: List<Track>): List<TrackDto> {
        return tracks.map { track ->
            TrackDto(
                track.trackName,
                track.artistName,
                track.collectionName,
                track.trackTimeMillis,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.artworkUrl100,
                track.previewUrl
            )
        }
    }

    override fun onItemClick(track: TrackDto) {
        searchHistory.addTrack(track)
    }
}