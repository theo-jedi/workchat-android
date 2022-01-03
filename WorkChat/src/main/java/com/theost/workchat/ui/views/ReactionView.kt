package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.theost.workchat.R

class ReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

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
    var countTextSize = 0f
        set(value) {
            field = value
            requestLayout()
        }
    var countTextColor = 0
        set(value) {
            field = value
            requestLayout()
        }

    private val tempFontMetrics = Paint.FontMetrics()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val emojiBounds = Rect()
    private val textBounds = Rect()

    private val emojiCoordinate = PointF()
    private val textCoordinate = PointF()

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ReactionView,
            defStyleAttr,
            defStyleRes
        )
        emoji = typedArray.getString(R.styleable.ReactionView_emoji).orEmpty()
        count = typedArray.getInt(R.styleable.ReactionView_count, 0)
        countTextSize = typedArray.getDimension(R.styleable.ReactionView_countTextSize, 0f)
        countTextColor = typedArray.getColor(R.styleable.ReactionView_countTextColor, 0)
        typedArray.recycle()

        textPaint.textSize = countTextSize
        textPaint.color = countTextColor
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(emoji, 0, emoji.length, emojiBounds)
        textPaint.getTextBounds(count.toString(), 0, count.toString().length, textBounds)

        val totalWidth = paddingLeft + emojiBounds.width() + textBounds.width() + paddingRight
        val totalHeight = paddingTop + maxOf(emojiBounds.height(), textBounds.height()) + paddingBottom

        val resultWidth = resolveSize(maxOf(totalWidth, layoutParams.width), widthMeasureSpec)
        val resultHeight = resolveSize(maxOf(totalHeight, layoutParams.height), heightMeasureSpec)

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textPaint.getFontMetrics(tempFontMetrics)

        emojiCoordinate.x = w / 2f - emojiBounds.width() / 2f
        emojiCoordinate.y = measuredHeight / 2f + emojiBounds.height() / 2 - tempFontMetrics.descent

        textCoordinate.x = w / 2f + emojiBounds.width() / 2f
        textCoordinate.y = measuredHeight / 2f + textBounds.height() / 2
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        return drawableState
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(emoji, emojiCoordinate.x, emojiCoordinate.y, textPaint)
        canvas.drawText(count.toString(), textCoordinate.x, textCoordinate.y, textPaint)
    }

    companion object {
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }

}
