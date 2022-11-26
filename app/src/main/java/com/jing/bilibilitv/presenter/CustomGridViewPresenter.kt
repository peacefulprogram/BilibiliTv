package com.jing.bilibilitv.presenter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.VerticalGridPresenter
import com.jing.bilibilitv.CustomGridView
import com.jing.bilibilitv.R

class CustomGridViewPresenter(
    focusZoomFactor: Int,
    useFocusDimmer: Boolean,
    private val getSelectedTabView: () -> View? = { null }
) :
    VerticalGridPresenter(focusZoomFactor, useFocusDimmer) {

    /**
     * 改为自定义grid view
     */
    override fun createGridViewHolder(parent: ViewGroup?): ViewHolder {
        val gridView = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.custom_lb_grid_view_layout, parent, false) as CustomGridView
        gridView.findSelectedTabView = {
            getSelectedTabView()
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