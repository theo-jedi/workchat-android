package com.theost.workchat.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.theost.workchat.R

class OutcomeMessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var message: String = ""
        set(value) {
            field = value
            val messageTextView = getChildAt(0) as TextView
            messageTextView.text = value
        }
    var time: String = ""
        set(value) {
            field = value
            val timeTextView = getChildAt(1) as TextView
            timeTextView.text = value
        }
    var bubble: Int = PARAMETER_UNSET
        set(value) {
            field = value
            paint.color = value
        }
    var corners = 0f

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        inflate(context, R.layout.layout_message_outcome, this)

        val messageTextView = getChildAt(0) as TextView
        val timeTextView = getChildAt(1) as TextView

        timeTextView.text = time
        messageTextView.text = message
        paint.color = bubble
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val messageTextView = getChildAt(0)
        val timeTextView = getChildAt(1)

        var totalWidth = 0
        var totalHeight = 0

        measureChildWithMargins(
            messageTextView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        // Support margin
        val messageMargin = (messageTextView.layoutParams as MarginLayoutParams)

        totalWidth += messageTextView.measuredWidth + messageMargin.rightMargin
        totalHeight += messageTextView.measuredHeight

        measureChildWithMargins(
            timeTextView,
            widthMeasureSpec,
            totalWidth,
            heightMeasureSpec,
            0
        )

        // Support margin
        val timeMargin = (timeTextView.layoutParams as MarginLayoutParams)

        totalWidth += timeMargin.leftMargin + timeTextView.measuredWidth
        totalHeight = maxOf(totalHeight, timeTextView.measuredHeight)

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            0f,
            0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            corners,
            corners,
            paint
        )
        super.dispatchDraw(canvas)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val messageTextView = getChildAt(0)
        val timeTextView = getChildAt(1)

        // Support margin
        val messageMargin = (messageTextView.layoutParams as MarginLayoutParams)
        val timeMargin = (timeTextView.layoutParams as MarginLayoutParams)

        messageTextView.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + messageTextView.measuredWidth,
            paddingTop + messageTextView.measuredHeight
        )

        timeTextView.layout(
            messageTextView.right + messageMargin.rightMargin + timeMargin.leftMargin,
            paddingTop,
            messageTextView.right + messageMargin.rightMargin + timeMargin.leftMargin +  timeTextView.measuredWidth,
            paddingTop + timeTextView.measuredHeight
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    companion object {
        private const val PARAMETER_UNSET = -1
    }

}