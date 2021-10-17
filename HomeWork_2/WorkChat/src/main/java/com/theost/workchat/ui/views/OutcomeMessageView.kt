package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.theost.workchat.R

class OutcomeMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var message: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as OutcomeMessageLayout
            messageLayout.message = value
        }
    var time: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as OutcomeMessageLayout
            messageLayout.time = value
        }
    var bubble: Int = PARAMETER_UNSET
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as OutcomeMessageLayout
            messageLayout.bubble = value
        }
    var corners: Float = CORNERS_DEFAULT
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as OutcomeMessageLayout
            messageLayout.corners = corners
        }

    init {
        inflate(context, R.layout.item_message_outcome, this)

        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.OutcomeMessageView,
            defStyleAttr,
            defStyleRes
        )
        message = typedArray.getString(R.styleable.IncomeMessageView_message).orEmpty()
        time = typedArray.getString(R.styleable.IncomeMessageView_time).orEmpty()
        bubble = typedArray.getColor(R.styleable.IncomeMessageView_bubble, PARAMETER_UNSET)
        corners = typedArray.getDimension(R.styleable.IncomeMessageView_corners, CORNERS_DEFAULT)
        typedArray.recycle()

        if (bubble == PARAMETER_UNSET) bubble =
            ContextCompat.getColor(context, R.color.message_bubble_outcome)

        val messageLayout = getChildAt(0) as OutcomeMessageLayout
        messageLayout.message = message
        messageLayout.time = time
        messageLayout.bubble = bubble
        messageLayout.corners = corners
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val messageLayout = getChildAt(0)
        val reactionLayout = getChildAt(1)

        var totalWidth = 0
        var totalHeight = 0

        measureChildWithMargins(
            messageLayout,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        // Support margin
        val messageMargin = (messageLayout.layoutParams as MarginLayoutParams)

        totalWidth += messageLayout.measuredWidth
        totalHeight += messageLayout.measuredHeight + messageMargin.bottomMargin

        measureChildWithMargins(
            reactionLayout,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            totalHeight
        )

        // Support margin
        val reactionMargin = (reactionLayout.layoutParams as MarginLayoutParams)

        totalWidth = maxOf(totalWidth, reactionMargin.leftMargin + reactionLayout.measuredWidth)
        totalHeight += reactionMargin.topMargin + reactionLayout.measuredHeight

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val messageLayout = getChildAt(0)
        val reactionLayout = getChildAt(1)

        // Support margin
        val messageMargin = (messageLayout.layoutParams as MarginLayoutParams)
        val reactionMargin = (reactionLayout.layoutParams as MarginLayoutParams)

        messageLayout.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + messageLayout.measuredWidth,
            paddingTop + messageLayout.measuredHeight
        )

        reactionLayout.layout(
            paddingLeft,
            messageLayout.bottom + messageMargin.bottomMargin + reactionMargin.topMargin,
            paddingLeft + reactionLayout.measuredWidth,
            messageLayout.bottom + messageMargin.bottomMargin + reactionMargin.topMargin + reactionLayout.measuredHeight
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
        private const val CORNERS_DEFAULT = 50f
    }

}