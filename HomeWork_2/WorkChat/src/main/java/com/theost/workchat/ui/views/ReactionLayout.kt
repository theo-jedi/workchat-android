package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View.MeasureSpec.getSize
import android.view.ViewGroup
import com.theost.workchat.R

class ReactionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var percentWidth = 0f
        set(value) {
            field = value
            requestLayout()
        }
    var reactionHorizontalMargin = 0
        set(value) {
            field = value
            requestLayout()
        }
    var reactionVerticalMargin = 0
        set(value) {
            field = value
            requestLayout()
        }

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ReactionLayout,
            defStyleAttr,
            defStyleRes
        )
        percentWidth = typedArray.getFloat(R.styleable.ReactionLayout_percentWidth, 1.0f)
        reactionHorizontalMargin = typedArray.getDimension(R.styleable.ReactionLayout_reactionHorizontalMargin, 20f).toInt()
        reactionVerticalMargin = typedArray.getDimension(R.styleable.ReactionLayout_reactionVerticalMargin, 20f).toInt()
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (getSize(widthMeasureSpec) - paddingLeft - paddingRight - reactionHorizontalMargin) * percentWidth

        var totalWidth = 0
        var totalHeight = 0
        var currentWidth = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, heightMeasureSpec)
            if (currentWidth != 0) currentWidth += reactionHorizontalMargin
            if (currentWidth + child.measuredWidth > width) {
                currentWidth = 0
                totalHeight += reactionVerticalMargin + child.measuredHeight
            } else {
                currentWidth += child.measuredWidth
                totalWidth = maxOf(totalWidth, currentWidth)
                totalHeight = maxOf(totalHeight, reactionVerticalMargin + child.measuredHeight)
            }
        }

        val resultWidth = resolveSize(paddingStart + totalWidth + paddingEnd, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentX = 0
        var currentY = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (currentX != 0) currentX += reactionHorizontalMargin
            val isNextRow = currentX + child.measuredWidth > width
            if (isNextRow) {
                currentX = 0
                currentY += reactionVerticalMargin + child.measuredHeight
            }
            child.layout(
                currentX,
                currentY,
                currentX + child.measuredWidth,
                currentY + child.measuredHeight
            )
            currentX += child.measuredWidth
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
