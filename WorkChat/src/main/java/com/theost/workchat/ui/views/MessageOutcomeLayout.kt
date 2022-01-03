package com.theost.workchat.ui.views

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.theost.workchat.R

class MessageOutcomeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var message: Spanned = SpannableString("")
        set(value) {
            field = value
            (getChildAt(0) as TextView).text = value
        }
    var time: String = ""
        set(value) {
            field = value
            (getChildAt(1) as TextView).text = value
        }

    private var isMaxWidth: Boolean = false

    init {
        inflate(context, R.layout.layout_message_outcome, this)

        val messageTextView = getChildAt(0) as TextView
        val timeTextView = getChildAt(1) as TextView

        messageTextView.text = message
        timeTextView.text = time
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight

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

        // Remeasure time if message content is match parent
        if (totalWidth + timeMargin.leftMargin + timeTextView.measuredWidth >= width) {
            measureChildWithMargins(
                timeTextView,
                widthMeasureSpec,
                0,
                heightMeasureSpec,
                totalHeight
            )

            isMaxWidth = true

            totalWidth = maxOf(totalWidth, timeMargin.leftMargin + timeTextView.measuredWidth)
            totalHeight += timeTextView.measuredHeight
        } else {
            isMaxWidth = false

            totalWidth += timeMargin.leftMargin + timeTextView.measuredWidth
            totalHeight = maxOf(totalHeight, timeTextView.measuredHeight)
        }

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
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

        if (isMaxWidth) {
            timeTextView.layout(
                measuredWidth - paddingRight - timeTextView.measuredWidth,
                paddingTop + messageTextView.measuredHeight,
                measuredWidth - paddingRight,
                paddingTop + messageTextView.measuredHeight + timeTextView.measuredHeight
            )
        } else {
            timeTextView.layout(
                messageTextView.right + messageMargin.rightMargin + timeMargin.leftMargin,
                paddingTop,
                messageTextView.right + messageMargin.rightMargin + timeMargin.leftMargin + timeTextView.measuredWidth,
                paddingTop + timeTextView.measuredHeight
            )
        }
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

}