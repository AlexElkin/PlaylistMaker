package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class ActivitySearch : AppCompatActivity() {

    private var countValue: String = ""
    private lateinit var buttonBack: ImageButton
    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recycler: RecyclerView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTERED_TEXT, countValue)
    }

    companion object {
        const val ENTERED_TEXT = "ENTERED_TEXT"
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countValue = savedInstanceState.getString(ENTERED_TEXT, countValue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
        setOnClick(savedInstanceState)
        editText.addTextChangedListener(createTextWatcher())
        createRecycler(recycler)
    }


    private fun initViews() {
        buttonBack = findViewById(R.id.activity_search_button_back)
        editText = findViewById(R.id.activity_search_editText)
        clearButton = findViewById(R.id.activity_search_clearIcon)
        recycler = findViewById(R.id.activity_search_recyclerView)
    }

    private fun setOnClick(savedInstanceState: Bundle?) {
        buttonBack.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        clearButton.setOnClickListener {
            editText.setText("")
            View.GONE
            hideKeyboard(editText)
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

    private fun createArrayTrack(): Array<Track> {
        val arrayList = arrayOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg "
            )
        )
        return arrayList
    }

    private fun createRecycler(recycler: RecyclerView) {
        val track = createArrayTrack()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = AdapterSearsh(
            tracks = List(5) {
                val tr = track[Random.nextInt(0, track.size)]
                Track(tr.trackName, tr.artistName, tr.trackTime, tr.artworkUrl100)

            }
        )

    }
}