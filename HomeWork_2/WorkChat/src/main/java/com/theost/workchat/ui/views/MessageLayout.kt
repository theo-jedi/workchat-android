package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.theost.workchat.R
import com.theost.workchat.data.models.MessageType

class MessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var incomeBubbleColor = ContextCompat.getColor(context, R.color.message_income_bubble)
    private var outcomeMessageColor =
        ContextCompat.getColor(context, R.color.message_outcome_bubble)

    var backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var cornerRadius = 0f
        set(value) {
            field = value
            requestLayout()
        }
    var bubbleColor = -1
        set(value) {
            field = value
            requestLayout()
        }
    var messageType: MessageType = MessageType.INCOME
        set(value) {
            field = value
            requestLayout()
        }

    init {
        inflate(context, R.layout.message_layout, this)

        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.MessageLayout,
            defStyleAttr,
            defStyleRes
        )
        bubbleColor = typedArray.getColor(R.styleable.MessageLayout_bubbleColor, -1)
        cornerRadius = typedArray.getDimension(R.styleable.MessageLayout_cornerRadius, 50f)
        typedArray.recycle()
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
        totalHeight = maxOf(totalHeight, nameMargin.topMargin + nameTextView.measuredHeight + nameMargin.bottomMargin)

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
        totalHeight = maxOf(totalHeight, timeMargin.topMargin + timeTextView.measuredHeight + nameMargin.bottomMargin)

        val heightUsed = if (messageType == MessageType.OUTCOME) 0 else totalHeight

        measureChildWithMargins(
            messageTextView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            heightUsed
        )

        // Support margin
        val messageMargin = (messageTextView.layoutParams as MarginLayoutParams)

        if (messageType == MessageType.OUTCOME) {
            totalWidth = maxOf(
                totalWidth,
                messageTextView.measuredWidth + timeMargin.leftMargin + timeTextView.measuredWidth
            )
            totalHeight = maxOf(
                messageMargin.topMargin + messageTextView.measuredHeight,
                timeMargin.topMargin + timeTextView.measuredHeight
            )
        } else {
            totalWidth = maxOf(totalWidth, messageTextView.measuredWidth)
            totalHeight += messageMargin.topMargin + messageTextView.measuredHeight
        }

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (bubbleColor != -1) {
            backgroundPaint.color = bubbleColor
        } else {
            when (messageType) {
                MessageType.INCOME -> backgroundPaint.color = incomeBubbleColor
                MessageType.OUTCOME -> backgroundPaint.color = outcomeMessageColor
            }
        }

        canvas.drawRoundRect(
            0f,
            0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            cornerRadius,
            cornerRadius,
            backgroundPaint
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

        if (messageType == MessageType.INCOME) {
            nameTextView.layout(
                paddingLeft + nameMargin.leftMargin,
                paddingTop + nameMargin.topMargin,
                paddingLeft + nameMargin.leftMargin + nameTextView.measuredWidth,
                paddingTop + nameMargin.topMargin + nameTextView.measuredHeight
            )
        }

        timeTextView.layout(
            width - paddingRight - timeTextView.measuredWidth,
            paddingTop + timeMargin.topMargin,
            width - paddingRight,
            paddingTop + timeMargin.topMargin + timeTextView.measuredWidth
        )

        val messageTop = if (messageType == MessageType.OUTCOME)
                paddingTop + messageMargin.topMargin
            else
                nameTextView.bottom + nameMargin.bottomMargin + messageMargin.topMargin

        messageTextView.layout(
            paddingLeft + messageMargin.leftMargin,
            messageTop,
            paddingLeft + messageMargin.leftMargin + messageTextView.measuredWidth,
            messageTop + nameTextView.measuredHeight
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

}