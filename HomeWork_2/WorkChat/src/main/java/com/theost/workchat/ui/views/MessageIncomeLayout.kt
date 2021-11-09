package com.theost.workchat.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.theost.workchat.R

class MessageIncomeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var username: String = ""
        set(value) {
            field = value
            val nameTextView = getChildAt(0) as TextView
            nameTextView.text = value
        }
    var time: String = ""
        set(value) {
            field = value
            val timeTextView = getChildAt(1) as TextView
            timeTextView.text = value
        }
    var message: Spanned = SpannableString("")
        set(value) {
            field = value
            val messageTextView = getChildAt(2) as TextView
            messageTextView.text = value
        }
    var bubble: Int = PARAMETER_UNSET
        set(value) {
            field = value
            paint.color = value
        }
    var corners = 0f

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        inflate(context, R.layout.layout_message_income, this)

        val nameTextView = getChildAt(0) as TextView
        val timeTextView = getChildAt(1) as TextView
        val messageTextView = getChildAt(2) as TextView

        nameTextView.text = username
        timeTextView.text = time
        messageTextView.text = message
        paint.color = bubble
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val nameTextView = getChildAt(0)
        val timeTextView = getChildAt(1)
        val messageTextView = getChildAt(2)

        var totalWidth = 0
        var totalHeight = 0

        measureChildWithMargins(
            nameTextView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        // Support margin
        val nameMargin = (nameTextView.layoutParams as MarginLayoutParams)

        totalWidth += nameTextView.measuredWidth + nameMargin.rightMargin
        totalHeight += nameTextView.measuredHeight + nameMargin.bottomMargin

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
        totalHeight = maxOf(
            totalHeight,
            timeTextView.measuredHeight + maxOf(nameMargin.bottomMargin, timeMargin.bottomMargin)
        )

        measureChildWithMargins(
            messageTextView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            totalHeight
        )

        // Support margin
        val messageMargin = (messageTextView.layoutParams as MarginLayoutParams)

        totalWidth = maxOf(totalWidth, messageTextView.measuredWidth)
        totalHeight += messageMargin.topMargin + messageTextView.measuredHeight

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
        val nameTextView = getChildAt(0)
        val timeTextView = getChildAt(1)
        val messageTextView = getChildAt(2)

        // Support margin
        val nameMargin = (nameTextView.layoutParams as MarginLayoutParams)
        val timeMargin = (timeTextView.layoutParams as MarginLayoutParams)
        val messageMargin = (messageTextView.layoutParams as MarginLayoutParams)

        nameTextView.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + nameTextView.measuredWidth,
            paddingTop + nameTextView.measuredHeight
        )

        timeTextView.layout(
            nameTextView.right + nameMargin.rightMargin + timeMargin.leftMargin,
            paddingTop,
            nameTextView.right + nameMargin.rightMargin + timeMargin.leftMargin + timeTextView.measuredWidth,
            paddingTop + timeTextView.measuredHeight
        )

        messageTextView.layout(
            paddingLeft + messageMargin.leftMargin,
            maxOf(nameTextView.bottom, timeTextView.bottom),
            paddingLeft + messageMargin.leftMargin + messageTextView.measuredWidth,
            maxOf(nameTextView.bottom, timeTextView.bottom) + maxOf(
                nameMargin.bottomMargin,
                timeMargin.bottomMargin
            ) + messageMargin.topMargin + messageTextView.measuredHeight
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