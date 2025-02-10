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

class ActivitySearch : AppCompatActivity() {

    private var countValue: String = ""
    private lateinit var buttonBack: ImageButton
    private lateinit var editText: EditText
    private lateinit var  clearButton: ImageView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTERED_TEXT, countValue)
    }

    companion object {
        const val ENTERED_TEXT = "ENTERED_TEXT"
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Вторым параметром мы передаём значение по умолчанию
        countValue = savedInstanceState.getString(ENTERED_TEXT, countValue)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
        setOnClick(savedInstanceState)
        editText.addTextChangedListener(createTextWatcher())
    }


    private fun initViews() {
        buttonBack = findViewById(R.id.activity_search_button_back)
        editText = findViewById(R.id.activity_search_editText)
        clearButton = findViewById(R.id.clearIcon)
    }

    private fun setOnClick(savedInstanceState: Bundle?){
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
}