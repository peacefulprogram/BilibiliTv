package com.jing.bilibilitv.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.leanback.widget.VerticalGridView

class CustomGridView(context: Context, attr: AttributeSet) :
    VerticalGridView(context, attr) {

    private val TAG = CustomGridView::class.java.simpleName

    private var columnCount = 1

    override fun setNumColumns(numColumns: Int) {
        super.setNumColumns(numColumns)
        columnCount = numColumns
    }

    /**
     * 当
     */
    var findSelectedTabView: (direction: Int) -> View? = { null }

    /**
     * 实现在第一行按上键时选中的tab获得焦点
     */
    override fun focusSearch(focused: View?, direction: Int): View {
        if (direction == View.FOCUS_UP && focused != null && getChildLayoutPosition(focused) < columnCount) {
            val result = findSelectedTabView(direction)
            if (result != null) {
                return result
            }
        }
        return super.focusSearch(focused, direction)
    }

    override fun requestChildFocus(child: View?, focused: View?) {
        super.requestChildFocus(child, focused)
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return super.requestFocus(direction, previouslyFocusedRect)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "onKeyLongPress:")
        return super.onKeyLongPress(keyCode, event)
    }

}