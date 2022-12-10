package com.jing.bilibilitv.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerview(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    RecyclerView(context, attrs, defStyleAttr) {

    var lastFocusedPosition = 0

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    init {
        descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        focusable = View.FOCUSABLE
    }

    override fun requestChildFocus(child: View?, focused: View?) {
        super.requestChildFocus(child, focused)
        child?.let {
            lastFocusedPosition = getChildViewHolder(child).bindingAdapterPosition
        }
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        layoutManager?.findViewByPosition(lastFocusedPosition)?.requestFocus()
        return false
    }
}