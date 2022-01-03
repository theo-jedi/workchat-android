package com.theost.workchat.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View.MeasureSpec.getSize
import android.view.ViewGroup
import com.theost.workchat.R

class ReactionsLayout @JvmOverloads constructor(
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
    var horizontalMargin = 0
        set(value) {
            field = value
            requestLayout()
        }
    var verticalMargin = 0
        set(value) {
            field = value
            requestLayout()
        }

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ReactionsLayout,
            defStyleAttr,
            defStyleRes
        )
        percentWidth = typedArray.getFloat(R.styleable.ReactionsLayout_percentWidth, 1.0f)
        horizontalMargin = typedArray.getDimension(R.styleable.ReactionsLayout_horizontalMargins, 0f).toInt()
        verticalMargin = typedArray.getDimension(R.styleable.ReactionsLayout_verticalMargins, 0f).toInt()
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (getSize(widthMeasureSpec) - paddingLeft - paddingRight) * percentWidth

        var totalWidth = 0
        var totalHeight = 0
        var currentX = 0
        var currentY = 0
        var rowMaxY = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            child.measure(widthMeasureSpec, heightMeasureSpec)

            if (currentX + child.measuredWidth + horizontalMargin > width) {
                currentX = 0
                currentY += rowMaxY
            }

            val isFirstRow = currentY == 0
            val isFirstColumn = currentX == 0

            if (!isFirstColumn) currentX += horizontalMargin
            currentX += child.measuredWidth

            rowMaxY = if (!isFirstRow) {
                maxOf(rowMaxY, verticalMargin + child.measuredHeight)
            } else {
                maxOf(rowMaxY, child.measuredHeight)
            }

            totalWidth = maxOf(totalWidth, currentX)
            totalHeight = maxOf(totalHeight, currentY + rowMaxY)
        }

        val resultWidth = resolveSize(paddingStart + totalWidth + paddingEnd, widthMeasureSpec)
        val resultHeight = resolveSize(paddingTop + totalHeight + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentX = 0
        var currentY = 0
        var rowMaxY = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (currentX + child.measuredWidth + horizontalMargin > measuredWidth) {
                currentX = 0
                currentY += rowMaxY
            }

            val isFirstRow = currentY == 0
            val isFirstColumn = currentX == 0

            if (!isFirstColumn) currentX += horizontalMargin

            val top = if (isFirstRow) currentY else currentY + verticalMargin
            val left = currentX

            currentX += child.measuredWidth

            rowMaxY = if (!isFirstRow) {
                maxOf(rowMaxY, verticalMargin + child.measuredHeight)
            } else {
                maxOf(rowMaxY, child.measuredHeight)
            }

            child.layout(
                left,
                paddingTop + top,
                left + child.measuredWidth,
                paddingTop + top + child.measuredHeight
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
