package com.github.dragon925.dailies.ui.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecorator(context: Context) : RecyclerView.ItemDecoration() {

    private val divider: Drawable?

    init {
        val attrs = intArrayOf(android.R.attr.listDivider)
        val ta = context.obtainStyledAttributes(attrs)
        divider = ta.getDrawable(0)
        ta.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        if (divider == null) return

        for (i in 0..< parent.childCount - 1) {
            val view = parent.getChildAt(i)
            val params = view.layoutParams as RecyclerView.LayoutParams

            val left = parent.paddingStart
            val right = parent.width - parent.paddingEnd
            val top = view.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}