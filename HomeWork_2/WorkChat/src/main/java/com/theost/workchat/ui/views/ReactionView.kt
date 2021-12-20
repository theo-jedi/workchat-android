package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
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
    var itemWidth = 0
        set(value) {
            field = value
            requestLayout()
        }
    var itemHeight = 0
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
            R.styleable.ReactionView,
            defStyleAttr,
            defStyleRes
        )

        emoji = typedArray.getString(R.styleable.ReactionView_emoji).orEmpty()
        count = typedArray.getInt(R.styleable.ReactionView_text, 0)
        textSize = typedArray.getDimension(R.styleable.ReactionView_reactionTextSize, SIZE_DEFAULT)
        textColor = typedArray.getColor(
            R.styleable.ReactionView_reactionTextColor,
            ContextCompat.getColor(context, R.color.lighter_gray)
        )
        itemWidth = typedArray.getDimension(
            R.styleable.ReactionView_reactionViewWidth,
            context.resources.getDimension(R.dimen.emoji_view_width)
        ).toInt()
        itemHeight = typedArray.getDimension(
            R.styleable.ReactionView_reactionViewWidth,
            context.resources.getDimension(R.dimen.emoji_view_height)
        ).toInt()
        backgroundDrawable = typedArray.getResourceId(
            R.styleable.ReactionView_reactionBackground,
            R.drawable.bg_reaction_view
        )
        typedArray.recycle()

        setBackgroundResource(backgroundDrawable)

        textPaint.textSize = textSize
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(emoji, 0, emoji.length, emojiBounds)
        textPaint.getTextBounds(emoji, 0, emoji.length, textBounds)
        val resultWidth = resolveSize(itemWidth, widthMeasureSpec)
        val resultHeight = resolveSize(itemHeight, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textPaint.getFontMetrics(tempFontMetrics)

        emojiCoordinate.x = w / 2f - emojiBounds.width() / 2f
        emojiCoordinate.y = itemHeight / 2f + emojiBounds.height() / 2 - tempFontMetrics.descent

        textCoordinate.x = w / 2f + textBounds.width() / 2f
        textCoordinate.y = itemHeight / 2f + textBounds.height() / 2 - tempFontMetrics.descent
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

        private const val SIZE_DEFAULT = 40f
    }

}
