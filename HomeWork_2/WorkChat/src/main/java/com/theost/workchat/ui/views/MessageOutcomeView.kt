package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.theost.workchat.R

class MessageOutcomeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var message: Spanned = SpannableString("")
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as MessageOutcomeLayout
            messageLayout.message = value
        }
    var time: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as MessageOutcomeLayout
            messageLayout.time = value
        }
    var bubble: Int = PARAMETER_UNSET
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as MessageOutcomeLayout
            messageLayout.bubble = value
        }
    var corners: Float = CORNERS_DEFAULT
        set(value) {
            field = value
            val messageLayout = getChildAt(0) as MessageOutcomeLayout
            messageLayout.corners = corners
        }
    var marginTop = 0
        set(value) {
            field = value
            requestLayout()
        }
    var messageLayout: MessageOutcomeLayout
    var reactionsLayout: ReactionsLayout

    init {
        inflate(context, R.layout.item_message_outcome, this)

        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.MessageOutcomeView,
            defStyleAttr,
            defStyleRes
        )
        message = SpannableString(typedArray.getString(R.styleable.MessageOutcomeView_message).orEmpty())
        time = typedArray.getString(R.styleable.MessageOutcomeView_time).orEmpty()
        bubble = typedArray.getColor(R.styleable.MessageOutcomeView_bubble, PARAMETER_UNSET)
        corners = typedArray.getDimension(R.styleable.MessageOutcomeView_corners, CORNERS_DEFAULT)
        marginTop = typedArray.getInteger(R.styleable.MessageOutcomeView_margins, context.resources.getDimension(R.dimen.message_margin).toInt())
        typedArray.recycle()

        messageLayout = getChildAt(0) as MessageOutcomeLayout
        reactionsLayout = getChildAt(1) as ReactionsLayout

        if (bubble == PARAMETER_UNSET) bubble =
            ContextCompat.getColor(context, R.color.message_bubble_outcome)

        val messageLayout = getChildAt(0) as MessageOutcomeLayout
        messageLayout.message = message
        messageLayout.time = time
        messageLayout.bubble = bubble
        messageLayout.corners = corners
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        (layoutParams as MarginLayoutParams).setMargins(0, marginTop, 0, 0)

        val messageLayout = getChildAt(0)
        val reactionsLayout = getChildAt(1)

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
            reactionsLayout,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            totalHeight
        )

        // Support margin
        val reactionMargin = (reactionsLayout.layoutParams as MarginLayoutParams)

        totalWidth = maxOf(totalWidth, reactionMargin.leftMargin + reactionsLayout.measuredWidth)
        if (reactionsLayout.measuredHeight != 0) totalHeight += reactionMargin.topMargin + reactionsLayout.measuredHeight

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val messageLayout = getChildAt(0)
        val reactionsLayout = getChildAt(1)

        // Support margin
        val messageMargin = (messageLayout.layoutParams as MarginLayoutParams)
        val reactionMargin = (reactionsLayout.layoutParams as MarginLayoutParams)

        messageLayout.layout(
            measuredWidth - paddingRight - messageLayout.measuredWidth,
            paddingTop,
            measuredWidth - paddingRight,
            paddingTop + messageLayout.measuredHeight
        )

        reactionsLayout.layout(
            measuredWidth - paddingRight - reactionsLayout.measuredWidth,
            messageLayout.bottom + messageMargin.bottomMargin + reactionMargin.topMargin,
            measuredWidth - paddingRight,
            messageLayout.bottom + messageMargin.bottomMargin + reactionMargin.topMargin + reactionsLayout.measuredHeight
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