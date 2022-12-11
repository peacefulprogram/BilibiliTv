package com.jing.bilibilitv.layout

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager

class FocusSearchInterceptorGridLayoutManager(
    context: Context,
    spanCount: Int,
    private val focusSearchInterceptor: (focused: View, direction: Int) -> View? = { _, _ -> null }
) :
    GridLayoutManager(context, spanCount) {

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        return focusSearchInterceptor(focused, direction) ?: super.onInterceptFocusSearch(
            focused,
            direction
        )
    }
}