package com.jing.bilibilitv.home

import android.view.KeyEvent
import android.view.View
import androidx.leanback.widget.VerticalGridView

fun getHomeGridViewKeyInterceptor(
    getSelectedTabView: () -> View?,
    refreshData: () -> Unit = {}
): (KeyEvent, VerticalGridView) -> Boolean =
    { keyEvent, gridView ->
        var result = false
        if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
            val tab = getSelectedTabView()
            if (tab != null) {
                tab.requestFocus()
                gridView.smoothScrollToPosition(0)
                result = true
            }
        } else if (keyEvent.keyCode == KeyEvent.KEYCODE_MENU && keyEvent.action == KeyEvent.ACTION_UP) {
            val tab = getSelectedTabView()
            if (tab != null) {
                tab.requestFocus()
                refreshData()
                result = true
            }
        }
        result
    }