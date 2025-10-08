package com.example.playlistmaker.ui.player

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var normalImageRes: Int = 0
    private var pressedImageRes: Int = 0
    private var isPressedState = false

    init {
        // Читаем кастомные атрибуты
        if (attrs == null) {
            throw IllegalArgumentException("Attributes are required for PlaybackButtonView")
        }

        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            0
        )

        try {
            normalImageRes = typedArray.getResourceId(R.styleable.PlaybackButtonView_normalImage, 0)
            pressedImageRes = typedArray.getResourceId(R.styleable.PlaybackButtonView_pressedImage, 0)

            // Проверяем, что обе картинки заданы
            if (normalImageRes == 0) {
                throw IllegalArgumentException("normalImage attribute is required")
            }
            if (pressedImageRes == 0) {
                throw IllegalArgumentException("pressedImage attribute is required")
            }

            // Устанавливаем начальное изображение
            setImageResource(normalImageRes)

        } finally {
            typedArray.recycle()
        }

        // Настраиваем обработчики
        setupTouchListeners()
        isClickable = true
        isFocusable = true

        // Добавляем ripple-эффект
        setupBackground()
    }

    private fun setupTouchListeners() {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    setPressedState()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setNormalState()
                    performClick()
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    setNormalState()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBackground() {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        background = ContextCompat.getDrawable(context, outValue.resourceId)
    }

    private fun setNormalState() {
        setImageResource(normalImageRes)
        isPressedState = false
    }

    private fun setPressedState() {
        setImageResource(pressedImageRes)
        isPressedState = true
    }

    // Публичные методы для управления из кода
    fun setNormalImage(@DrawableRes resId: Int) {
        normalImageRes = resId
        if (!isPressedState) {
            setImageResource(normalImageRes)
        }
    }

    fun setPressedImage(@DrawableRes resId: Int) {
        pressedImageRes = resId
        if (isPressedState) {
            setImageResource(pressedImageRes)
        }
    }

    fun setImages(@DrawableRes normalResId: Int, @DrawableRes pressedResId: Int) {
        setNormalImage(normalResId)
        setPressedImage(pressedResId)
    }

    fun setState(pressed: Boolean) {
        if (pressed) {
            setPressedState()
        } else {
            setNormalState()
        }
    }

    fun toggleState() {
        if (isPressedState) {
            setNormalState()
        } else {
            setPressedState()
        }
    }

    fun isPressedState(): Boolean = isPressedState

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}