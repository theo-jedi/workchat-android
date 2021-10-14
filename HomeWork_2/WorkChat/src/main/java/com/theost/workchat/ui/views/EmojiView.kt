package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.theost.workchat.R

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener {

    var emoji = ""
        set(value) {
            field = value
            requestLayout()
        }
    var count = 0
        set(value) {
            field = value
            requestLayout()
        }
    var textSize = 0f
        set(value) {
            field = value
            requestLayout()
        }
    var textColor = 0
        set(value) {
            field = value
            requestLayout()
        }
    var padding = 0
        set(value) {
            field = value
            requestLayout()
        }
    var backgroundDrawable = 0
        set(value) {
            field = value
            requestLayout()
        }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val emojiBounds = Rect()
    private val textBounds = Rect()
    private val emojiCoordinate = PointF()
    private val textCoordinate = PointF()

    private val tempFontMetrics = Paint.FontMetrics()

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.EmojiView,
            defStyleAttr,
            defStyleRes
        )

        emoji = typedArray.getString(R.styleable.EmojiView_emoji).orEmpty()
        count = typedArray.getInt(R.styleable.EmojiView_text, 0)
        textSize = typedArray.getDimension(R.styleable.EmojiView_emojiTextSize, 40f)
        textColor = typedArray.getColor(
            R.styleable.EmojiView_emojiTextColor,
            ContextCompat.getColor(context, R.color.lighter_gray)
        )
        padding = typedArray.getDimension(R.styleable.EmojiView_emojiPadding, 28f).toInt()
        backgroundDrawable = typedArray.getResourceId(
            R.styleable.EmojiView_emojiBackground,
            R.drawable.bg_emoji_view
        )
        typedArray.recycle()

        setBackgroundResource(backgroundDrawable)
        setPadding(padding)

        textPaint.textSize = textSize
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER

        setOnClickListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(emoji, 0, emoji.length, emojiBounds)

        var totalWidth = 0
        var totalHeight = 0

        val emojiWidth = emojiBounds.width()
        val emojiHeight = emojiBounds.height()

        totalWidth += emojiWidth
        totalHeight = maxOf(totalHeight, emojiHeight)

        textPaint.getTextBounds(emoji, 0, emoji.length, textBounds)

        val textWidth = textBounds.width()
        val textHeight = textBounds.height()

        totalWidth += textWidth
        totalHeight = maxOf(totalHeight, textHeight)

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textPaint.getFontMetrics(tempFontMetrics)

        emojiCoordinate.x = w / 2f - emojiBounds.width() / 2f
        emojiCoordinate.y = h / 2f + emojiBounds.height() / 2 - tempFontMetrics.descent

        textCoordinate.x = w / 2f + textBounds.width() / 2f
        textCoordinate.y = h / 2f + textBounds.height() / 2 - tempFontMetrics.descent
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) {
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        }
        return drawableState
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(emoji, emojiCoordinate.x, emojiCoordinate.y, textPaint)
        canvas.drawText(count.toString(), textCoordinate.x, textCoordinate.y, textPaint)
    }

    companion object {
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }

    override fun onClick(view: View) {
        isSelected = !isSelected
    }
}
