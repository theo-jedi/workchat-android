package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.theost.workchat.R

class IncomeMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var avatar: Int = PARAMETER_UNSET
        set(value) {
            field = value
            val avatarImageView = (getChildAt(0) as CardView).getChildAt(0) as ImageView
            if (avatar != PARAMETER_UNSET) avatarImageView.setImageResource(value)
        }
    var username: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as IncomeMessageLayout
            messageLayout.username = value
        }
    var time: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as IncomeMessageLayout
            messageLayout.time = value
        }
    var message: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as IncomeMessageLayout
            messageLayout.message = value
        }
    var bubble: Int = PARAMETER_UNSET
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as IncomeMessageLayout
            messageLayout.bubble = value
        }
    var corners: Float = CORNERS_DEFAULT
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as IncomeMessageLayout
            messageLayout.corners = value
        }
    var marginTop = 0
        set(value) {
            field = value
            requestLayout()
        }

    init {
        inflate(context, R.layout.item_message_income, this)

        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.IncomeMessageView,
            defStyleAttr,
            defStyleRes
        )
        avatar = typedArray.getResourceId(R.styleable.IncomeMessageView_avatar, PARAMETER_UNSET)
        username = typedArray.getString(R.styleable.IncomeMessageView_username).orEmpty()
        message = typedArray.getString(R.styleable.IncomeMessageView_message).orEmpty()
        time = typedArray.getString(R.styleable.IncomeMessageView_time).orEmpty()
        bubble = typedArray.getColor(R.styleable.IncomeMessageView_bubble, PARAMETER_UNSET)
        corners = typedArray.getDimension(R.styleable.IncomeMessageView_corners, CORNERS_DEFAULT)
        marginTop = typedArray.getInteger(R.styleable.IncomeMessageView_margins, context.resources.getDimension(R.dimen.message_margin).toInt())
        typedArray.recycle()

        if (bubble == PARAMETER_UNSET) bubble =
            ContextCompat.getColor(context, R.color.message_bubble_income)

        val messageLayout = getChildAt(1) as IncomeMessageLayout
        messageLayout.username = username
        messageLayout.message = message
        messageLayout.time = time
        messageLayout.bubble = bubble
        messageLayout.corners = corners

        val avatarImageView = (getChildAt(0) as CardView).getChildAt(0) as ImageView
        if (avatar != PARAMETER_UNSET) avatarImageView.setImageResource(avatar)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        (layoutParams as MarginLayoutParams).setMargins(0, marginTop, 0, 0)

        val avatarImageView = getChildAt(0)
        val messageLayout = getChildAt(1)
        val reactionLayout = getChildAt(2)

        var totalWidth = 0
        var totalHeight = 0

        measureChildWithMargins(
            avatarImageView,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        // Support margin
        val avatarMargin = (avatarImageView.layoutParams as MarginLayoutParams)

        totalWidth = maxOf(totalWidth, avatarImageView.measuredWidth + avatarMargin.rightMargin)
        totalHeight = maxOf(
            totalHeight,
            avatarMargin.topMargin + avatarImageView.measuredHeight + avatarMargin.bottomMargin
        )

        measureChildWithMargins(
            messageLayout,
            widthMeasureSpec,
            totalWidth,
            heightMeasureSpec,
            0
        )

        // Support margin
        val messageMargin = (messageLayout.layoutParams as MarginLayoutParams)

        totalWidth += messageMargin.leftMargin + messageLayout.measuredWidth
        totalHeight = maxOf(totalHeight, messageLayout.measuredHeight + messageMargin.bottomMargin)

        measureChildWithMargins(
            reactionLayout,
            widthMeasureSpec,
            avatarImageView.measuredWidth,
            heightMeasureSpec,
            totalHeight
        )

        // Support margin
        val reactionMargin = (reactionLayout.layoutParams as MarginLayoutParams)

        totalWidth = maxOf(totalWidth, avatarImageView.measuredWidth + avatarMargin.rightMargin + messageMargin.leftMargin + reactionLayout.measuredWidth)
        if (reactionLayout.measuredHeight != 0) totalHeight += reactionMargin.topMargin + reactionLayout.measuredHeight

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val avatarImageView = getChildAt(0)
        val messageLayout = getChildAt(1)
        val reactionLayout = getChildAt(2)

        // Support margin
        val avatarMargin = (avatarImageView.layoutParams as MarginLayoutParams)
        val messageMargin = (messageLayout.layoutParams as MarginLayoutParams)
        val reactionMargin = (reactionLayout.layoutParams as MarginLayoutParams)

        avatarImageView.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + avatarImageView.measuredWidth,
            paddingTop + avatarImageView.measuredHeight
        )

        messageLayout.layout(
            avatarImageView.right + avatarMargin.rightMargin + messageMargin.leftMargin,
            paddingTop,
            avatarImageView.right + avatarMargin.rightMargin + messageMargin.leftMargin + messageLayout.measuredWidth,
            paddingTop + messageLayout.measuredHeight
        )

        reactionLayout.layout(
            messageLayout.left,
            messageLayout.bottom + messageMargin.bottomMargin + reactionMargin.topMargin,
            messageLayout.left + reactionLayout.measuredWidth,
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