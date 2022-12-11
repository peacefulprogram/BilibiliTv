package com.jing.bilibilitv.layout

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

class FocusSearchInterceptorLinearLayoutManager(
    context: Context,
    private val focusSearchInterceptor: (focused: View, direction: Int) -> View? = { _, _ -> null }
) :
    LinearLayoutManager(context) {

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        return focusSearchInterceptor(focused, direction) ?: super.onInterceptFocusSearch(
            focused,
            direction
        )
    }
}