package com.example.playlistmaker.activity

import android.content.SharedPreferences
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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.BUTTON_UPDATE_VISIBILITY
import com.example.playlistmaker.ENTERED_TEXT
import com.example.playlistmaker.IMAGE_ERROR_IMAGE_RESOURCE
import com.example.playlistmaker.IMAGE_ERROR_VISIBILITY
import com.example.playlistmaker.MY_SAVES
import com.example.playlistmaker.adapters.AdapterSearsh
import com.example.playlistmaker.api.ApiService
import com.example.playlistmaker.R
import com.example.playlistmaker.RECYCLER_VISIBILITY
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.TEXT_ERROR_RESOURCE
import com.example.playlistmaker.TEXT_ERROR_VISIBILITY
import com.example.playlistmaker.data_classes.Track
import com.example.playlistmaker.data_classes.TrackResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class ActivitySearch : AppCompatActivity(), AdapterSearsh.OnItemClickListener {

    private var countValue: String = ""
    private lateinit var buttonBack: ImageButton
    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AdapterSearsh
    private lateinit var imageError: ImageView
    private lateinit var buttonUpdate: Button
    private lateinit var clearHistory : Button
    private lateinit var textError: TextView
    private lateinit var textYouWereLooking: TextView
    private var imageResource: Int = R.drawable.no_tracks
    private var textResource: Int = R.string.no_track
    private lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = getSharedPreferences(MY_SAVES, MODE_PRIVATE)
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
        editText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && countValue.isEmpty()) {
                createHistory()
            }
        }

        adapter = AdapterSearsh(emptyList(),this,this)
        startEditText()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun startEditText(){
        editText.requestFocus()
        createHistory()
    }
    private fun createHistory(){
        val tracks = SearchHistory(sharedPreferences).read()
        if (tracks != null && tracks.isNotEmpty()) {
            textYouWereLooking.visibility = View.VISIBLE
            clearHistory.visibility = View.VISIBLE
            recycler.visibility = View.VISIBLE
            adapter.updateTracks(tracks.reversed<Track>())
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
    }

    private fun setOnClick() {
        buttonBack.setOnClickListener { finish() }
        clearHistory.setOnClickListener {
            textYouWereLooking.visibility = View.GONE
            SearchHistory(sharedPreferences).removeHistory()
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
                    if (s.isEmpty()) {
                        clearingScreen()
                        createHistory()
                    } else {
                        textYouWereLooking.visibility = View.GONE
                        recycler.visibility = View.GONE
                        clearHistory.visibility = View.GONE
                    }
                    countValue = s.toString()
                    clearButton.visibility = clearButtonVisibility(s)
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

    private fun performSearch() {
        adapter.updateTracks(emptyList())
        recycler.visibility = View.GONE
        imageError.visibility = View.GONE
        textError.visibility = View.GONE
        buttonUpdate.visibility = View.GONE
        textYouWereLooking.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val tracks = apiConnection()
                if (tracks.results.isEmpty()) {
                    imageResource = R.drawable.no_tracks
                    imageError.setImageResource(imageResource)
                    textResource = R.string.no_track
                    textError.setText(textResource)
                    imageError.visibility = View.VISIBLE
                    textError.visibility = View.VISIBLE
                } else {
                    recycler.visibility = View.VISIBLE
                    adapter.updateTracks(tracks.results)

                }

            } catch (e: IOException) {
                // Обработка ошибок, связанных с сетью
                internetErrorHandling()
            } catch (e: HttpException) {
                // Обработка HTTP-ошибок (4xx, 5xx)
                internetErrorHandling()
            } catch (e: Exception) {
                // Обработка всех остальных ошибок
                println("Unexpected error: ${e.message}")
            }
        }
    }

    private fun internetErrorHandling() {
        imageResource = R.drawable.no_internet
        imageError.setImageResource(imageResource)
        textResource = R.string.no_internet
        textError.setText(textResource)
        imageError.visibility = View.VISIBLE
        textError.visibility = View.VISIBLE
        buttonUpdate.visibility = View.VISIBLE
    }

    private suspend fun apiConnection(): TrackResponse {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)
        return apiService.getTrack(countValue)
    }

    override fun onItemClick(track: Track) {
        SearchHistory(sharedPreferences).addTrack(track)
    }
}


