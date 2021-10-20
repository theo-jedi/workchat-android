package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import com.theost.workchat.R


class DateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var text = ""
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
    var bubbleColor = 0
        set(value) {
            field = value
            requestLayout()
        }
    var marginTop = 0
        set(value) {
            field = value
            requestLayout()
        }
    var paddingsVertical = 0
        set(value) {
            field = value
            requestLayout()
        }
    var paddingsHorizontal = 0
        set(value) {
            field = value
            requestLayout()
        }

    private val textCoordinate = PointF()
    private val textBounds = Rect()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.DateView,
            defStyleAttr,
            defStyleRes
        )
        text = typedArray.getString(R.styleable.DateView_dateText).orEmpty()
        textSize = typedArray.getDimension(R.styleable.DateView_dateTextSize, SIZE_DEFAULT)
        marginTop = typedArray.getInteger(R.styleable.DateView_dateMargins, context.resources.getDimension(R.dimen.message_margin).toInt())
        paddingsVertical = typedArray.getInteger(R.styleable.DateView_datePaddingsVertical, PADDING_VERTICAL_DEFAULT)
        paddingsHorizontal = typedArray.getInteger(R.styleable.DateView_datePaddingsHorizontal, PADDING_HORIZONTAL_DEFAULT)
        bubbleColor = typedArray.getColor(
            R.styleable.DateView_dateBubbleColor,
            ContextCompat.getColor(context, R.color.black_4)
        )
        textColor = typedArray.getColor(
            R.styleable.DateView_dateTextColor,
            ContextCompat.getColor(context, R.color.light_gray)
        )
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
        typedArray.recycle()

        setPadding(paddingsHorizontal, paddingsVertical, paddingsHorizontal, paddingsVertical)
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        (layoutParams as MarginLayoutParams).setMargins(0, marginTop, 0, 0)

        textPaint.getTextBounds(text, 0, text.length, textBounds)

        val resultWidth =
            resolveSize(paddingLeft + textBounds.width() + paddingRight, widthMeasureSpec)
        val resultHeight =
            resolveSize(paddingTop + textBounds.height() + paddingBottom, heightMeasureSpec)

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textCoordinate.x = w / 2f
        textCoordinate.y = h / 2f + textBounds.height() / 2
    }

    override fun onDraw(canvas: Canvas) {
        // Draw background
        textPaint.color = bubbleColor
        canvas.drawRoundRect(
            textCoordinate.x - textBounds.width(),
            0f,
            textCoordinate.x + textBounds.width(),
            height.toFloat(),
            height.toFloat(),
            height.toFloat(),
            textPaint
        )

        // Draw text
        textPaint.color = textColor
        canvas.drawText(text, textCoordinate.x, textCoordinate.y, textPaint)
    }

    companion object {
        private const val SIZE_DEFAULT = 34f
        private const val PADDING_VERTICAL_DEFAULT = 24
        private const val PADDING_HORIZONTAL_DEFAULT = 56
    }

}