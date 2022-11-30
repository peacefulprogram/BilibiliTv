package com.jing.bilibilitv.presenter

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import com.jing.bilibilitv.CustomGridView
import com.jing.bilibilitv.R

class CustomGridViewPresenter(
    focusZoomFactor: Int,
    useFocusDimmer: Boolean,
    private val getSelectedTabView: () -> View? = { null },
    private val keyEventInterceptor: (keyEvent: KeyEvent, gridView: VerticalGridView) -> Boolean = { _, _ -> false }
) :
    VerticalGridPresenter(focusZoomFactor, useFocusDimmer) {

    private val TAG = CustomGridViewPresenter::class.java.simpleName

    var gridView: CustomGridView? = null

    /**
     * 改为自定义grid view
     */
    override fun createGridViewHolder(parent: ViewGroup?): ViewHolder {
        gridView = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.custom_lb_grid_view_layout, parent, false) as CustomGridView
        gridView!!.findSelectedTabView = {
            getSelectedTabView()
        }
        gridView!!.setOnKeyInterceptListener { keyEvent ->
            keyEventInterceptor(keyEvent, gridView!!)
        }
        return ViewHolder(gridView)
    }

    /**
     * 设置间距
     */
    override fun initializeGridViewHolder(vh: ViewHolder?) {
        super.initializeGridViewHolder(vh)
        val gridView = vh!!.gridView
        val top = 20
        val left = gridView.paddingLeft
        val right = gridView.paddingRight
        val bottom = gridView.paddingBottom
        gridView.setPadding(left, top, right, bottom)
        gridView.verticalSpacing = 50
        gridView.horizontalSpacing = 50
    }
}