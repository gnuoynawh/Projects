package com.gnuoynawh.exam.calendar.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView

class OverlapImageView: RelativeLayout {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    private val baseImageView: AppCompatImageView = AppCompatImageView(context)

    private var drawType: TypeImageView.DrawType = TypeImageView.DrawType.VERTICAL

    fun setDrawType(drawType: TypeImageView.DrawType) {
        this.drawType = drawType
    }

    fun changeDrawType(drawType: TypeImageView.DrawType) {
        this.drawType = drawType
        changeTypeImageView()
    }

    init {
        baseImageView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        baseImageView.setImageDrawable(null)
        baseImageView.scaleType = ImageView.ScaleType.FIT_XY
        baseImageView.tag = "image1"
        addView(baseImageView)
    }

    fun setImageResource(@DrawableRes resId: Int) {
        Log.e("TEST", "setImageResource [$resId] = ${baseImageView.drawable == null}")
        if (baseImageView.drawable == null) {
            baseImageView.setImageResource(resId)
        } else {
            val view = makeTypeImageView()
            view.setImageResource(resId)
            addView(view)
        }
    }

    fun setImageDrawable(drawable: Drawable?) {
        if (baseImageView.drawable == null) {
            baseImageView.setImageDrawable(drawable)
        } else {
            val view = makeTypeImageView()
            view.setImageDrawable(drawable)
            addView(view)
        }
    }

    fun setImageBitmap(bm: Bitmap?) {
        if (baseImageView.drawable == null) {
            baseImageView.setImageBitmap(bm)
        } else {
            val view = makeTypeImageView()
            view.setImageBitmap(bm)
            addView(view)
        }
    }

    private fun makeTypeImageView(): TypeImageView {
        val before = findViewWithTag<TypeImageView>("image2")
        if (before != null)
            removeView(before)

        val after = TypeImageView(context)
        after.drawType = drawType
        after.tag = "image2"
        after.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        return after
    }

    private fun changeTypeImageView() {
        val before = findViewWithTag<TypeImageView>("image2") ?: return

        val drawable = before.drawable
        removeView(before)

        val after = TypeImageView(context)
        after.setImageDrawable(drawable)
        after.drawType = drawType
        after.tag = "image2"
        after.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        addView(after)
    }
}