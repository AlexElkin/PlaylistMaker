package com.example.playlistmaker

import android.content.Context
import android.content.Intent
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
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

class ActivitySearch : AppCompatActivity() {

    private var countValue: String = ""
    private lateinit var buttonBack: ImageButton
    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AdapterSearsh
    private lateinit var imageError: ImageView
    private lateinit var buttonUpdate: Button
    private lateinit var textError: TextView
    private var imageResource: Int = R.drawable.no_tracks
    private var textResource: Int = R.string.no_track

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

    companion object {
        const val ENTERED_TEXT = "ENTERED_TEXT"
        const val RECYCLER_VISIBILITY = "RECYCLER_VISIBILITY"
        const val IMAGE_ERROR_VISIBILITY = "IMAGE_ERROR_VISIBILITY"
        const val IMAGE_ERROR_IMAGE_RESOURCE = "IMAGE_ERROR_IMAGE_RESOURCE"
        const val TEXT_ERROR_VISIBILITY = "TEXT_ERROR_VISIBILITY"
        const val TEXT_ERROR_RESOURCE = "TEXT_ERROR_RESOURCE"
        const val BUTTON_UPDATE_VISIBILITY = "BUTTON_UPDATE_VISIBILITY"
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countValue = savedInstanceState.getString(ENTERED_TEXT, "")
        recycler.visibility = savedInstanceState.getInt(RECYCLER_VISIBILITY, View.VISIBLE)
        imageError.visibility = savedInstanceState.getInt(IMAGE_ERROR_VISIBILITY, View.GONE)
        imageError.setImageResource(savedInstanceState.getInt(IMAGE_ERROR_IMAGE_RESOURCE, imageResource))
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

        adapter = AdapterSearsh(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.activity_search_button_back)
        editText = findViewById(R.id.activity_search_editText)
        clearButton = findViewById(R.id.activity_search_clearIcon)
        recycler = findViewById(R.id.activity_search_recyclerView)
        imageError = findViewById(R.id.activity_search_imageView_Error)
        buttonUpdate = findViewById(R.id.activity_search_button_update)
        textError = findViewById(R.id.activity_search_textView_Error)
    }

    private fun setOnClick() {
        buttonBack.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        clearButton.setOnClickListener {
            editText.setText("")
            clearButton.visibility = View.GONE
            recycler.visibility = View.GONE
            hideKeyboard(editText)
        }

        buttonUpdate.setOnClickListener{
            performSearch()
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO:
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                countValue = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // TODO:
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
        lifecycleScope.launch {
            try {
                val tracks = apiConnection()
                println(tracks.results)
                if (tracks.results.isEmpty()){
                    imageResource = R.drawable.no_tracks
                    imageError.setImageResource(imageResource)
                    textResource = R.string.no_track
                    textError.setText(textResource)
                    imageError.visibility = View.VISIBLE
                    textError.visibility = View.VISIBLE
                } else {
                    recycler.visibility = View.VISIBLE
                    adapter.updateTracks(tracks.results)}

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

    private fun internetErrorHandling(){
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
}

interface ApiService {
    @GET("/search?entity=song")
    suspend fun getTrack(@Query("term") track: String ): TrackResponse
}

data class TrackResponse(
    val results: List<Track>
)

data class Track(val trackName: String,
                 val artistName:String,
                 val trackTimeMillis: Long,
                 val artworkUrl100:String)
