package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.theost.workchat.R

class MessageIncomeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var reactionsLayout: ReactionsLayout
    var messageLayout: MessageIncomeLayout

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
    var name: String = ""
        set(value) {
            field = value
            val messageLayout = getChildAt(1) as MessageIncomeLayout
            messageLayout.name = value
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

    init {
        inflate(context, R.layout.item_message_view_income, this)

        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.MessageIncomeView,
            defStyleAttr,
            defStyleRes
        )
        avatar = typedArray.getString(R.styleable.MessageIncomeView_avatar) ?: ""
        name = typedArray.getString(R.styleable.MessageIncomeView_name).orEmpty()
        message = SpannableString(typedArray.getString(R.styleable.MessageIncomeView_message).orEmpty())
        time = typedArray.getString(R.styleable.MessageIncomeView_time).orEmpty()
        typedArray.recycle()

        val avatarImageView = (getChildAt(0) as CardView).getChildAt(0) as ImageView
        Glide.with(this)
            .load(avatar)
            .placeholder(R.drawable.ic_loading_avatar)
            .error(R.drawable.ic_error_avatar)
            .into(avatarImageView)

        reactionsLayout = getChildAt(2) as ReactionsLayout
        messageLayout = (getChildAt(1) as MessageIncomeLayout).apply {
            this.name = name
            this.message = message
            this.time = time
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val avatarImageView = getChildAt(0)
        val messageLayout = getChildAt(1)
        val reactionsLayout = getChildAt(2)

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
            reactionsLayout,
            widthMeasureSpec,
            avatarImageView.measuredWidth,
            heightMeasureSpec,
            totalHeight
        )

        // Support margin
        val reactionMargin = (reactionsLayout.layoutParams as MarginLayoutParams)

        totalWidth = maxOf(totalWidth, avatarImageView.measuredWidth + avatarMargin.rightMargin + messageMargin.leftMargin + reactionsLayout.measuredWidth)
        if (reactionsLayout.measuredHeight != 0) totalHeight += reactionMargin.topMargin + reactionsLayout.measuredHeight

        val resultWidth = resolveSize(paddingLeft + totalWidth + paddingRight, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val avatarImageView = getChildAt(0)
        val messageLayout = getChildAt(1)
        val reactionsLayout = getChildAt(2)

        // Support margin
        val avatarMargin = (avatarImageView.layoutParams as MarginLayoutParams)
        val messageMargin = (messageLayout.layoutParams as MarginLayoutParams)
        val reactionMargin = (reactionsLayout.layoutParams as MarginLayoutParams)

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

        reactionsLayout.layout(
            messageLayout.left,
            messageLayout.bottom + messageMargin.bottomMargin + reactionMargin.topMargin,
            messageLayout.left + reactionsLayout.measuredWidth,
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

}