package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.theost.workchat.R

class MessageIncomeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var avatar: String = ""
        set(value) {
            field = value
            val avatarImageView = (getChildAt(0) as CardView).getChildAt(0) as ImageView
            Glide.with(this)
                .load(avatar)
                .placeholder(R.drawable.ic_loading_avatar)
                .error(R.drawable.ic_error_avatar)
                .into(avatarImageView)
        }
    var username: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as MessageIncomeLayout
            messageLayout.username = value
        }
    var time: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as MessageIncomeLayout
            messageLayout.time = value
        }
    var message: Spanned = SpannableString("")
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as MessageIncomeLayout
            messageLayout.message = value
        }
    var bubble: Int = PARAMETER_UNSET
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as MessageIncomeLayout
            messageLayout.bubble = value
        }
    var corners: Float = CORNERS_DEFAULT
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as MessageIncomeLayout
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
            R.styleable.MessageIncomeView,
            defStyleAttr,
            defStyleRes
        )
        avatar = typedArray.getString(R.styleable.MessageIncomeView_avatar) ?: ""
        username = typedArray.getString(R.styleable.MessageIncomeView_username).orEmpty()
        message = SpannableString(typedArray.getString(R.styleable.MessageIncomeView_message).orEmpty())
        time = typedArray.getString(R.styleable.MessageIncomeView_time).orEmpty()
        bubble = typedArray.getColor(R.styleable.MessageIncomeView_bubble, PARAMETER_UNSET)
        corners = typedArray.getDimension(R.styleable.MessageIncomeView_corners, CORNERS_DEFAULT)
        marginTop = typedArray.getInteger(R.styleable.MessageIncomeView_margins, context.resources.getDimension(R.dimen.message_margin).toInt())
        typedArray.recycle()

        if (bubble == PARAMETER_UNSET) bubble =
            ContextCompat.getColor(context, R.color.message_bubble_income)

        val messageLayout = getChildAt(1) as MessageIncomeLayout
        messageLayout.username = username
        messageLayout.message = message
        messageLayout.time = time
        messageLayout.bubble = bubble
        messageLayout.corners = corners

        val avatarImageView = (getChildAt(0) as CardView).getChildAt(0) as ImageView
        Glide.with(this)
            .load(avatar)
            .placeholder(R.drawable.ic_loading_avatar)
            .error(R.drawable.ic_error_avatar)
            .into(avatarImageView)
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