package com.example.playlistmaker.ui.player

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playImageRes: Int = 0
    private var pauseImageRes: Int = 0
    private var isPlaying = false
    private var playDrawable: android.graphics.drawable.Drawable? = null
    private var pauseDrawable: android.graphics.drawable.Drawable? = null

    private val defaultSizePx = 84.dpToPx(context)

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            0
        )

        try {
            playImageRes = typedArray.getResourceId(R.styleable.PlaybackButtonView_playImage, 0)
            pauseImageRes = typedArray.getResourceId(R.styleable.PlaybackButtonView_pauseImage, 0)

            require(playImageRes != 0) {"playImage attribute is required"}
            require(pauseImageRes != 0) {"pauseImage attribute is required"}

            playDrawable = ContextCompat.getDrawable(context, playImageRes)
            pauseDrawable = ContextCompat.getDrawable(context, pauseImageRes)

        } finally {
            typedArray.recycle()
        }

        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(defaultSizePx, widthSize)
            else -> defaultSizePx
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(defaultSizePx, heightSize)
            else -> defaultSizePx
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = w - paddingRight
        val bottom = h - paddingBottom

        playDrawable?.setBounds(left, top, right, bottom)
        pauseDrawable?.setBounds(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val currentDrawable = if (isPlaying) pauseDrawable else playDrawable
        currentDrawable?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                toggleState()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun toggleState() {
        isPlaying = !isPlaying
        invalidate()
    }

    fun setPlaying(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }

    fun isPlaying(): Boolean = isPlaying

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}