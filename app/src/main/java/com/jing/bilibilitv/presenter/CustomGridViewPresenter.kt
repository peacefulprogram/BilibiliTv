package com.jing.bilibilitv.presenter

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.ItemBridgeAdapter.AdapterListener
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import com.jing.bilibilitv.CustomGridView
import com.jing.bilibilitv.R

class CustomGridViewPresenter(
    focusZoomFactor: Int,
    useFocusDimmer: Boolean,
    private val getSelectedTabView: () -> View? = { null },
    private val keyEventInterceptor: (keyEvent: KeyEvent, gridView: VerticalGridView) -> Boolean = { _, _ -> false },
) :
    VerticalGridPresenter(focusZoomFactor, useFocusDimmer) {

    private val TAG = CustomGridViewPresenter::class.java.simpleName

    private var _longClickListener: ((view: View, item: Any?) -> Boolean)? = null

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
    override fun initializeGridViewHolder(vh: ViewHolder) {
        val adapterField = vh.javaClass.getDeclaredField("mItemBridgeAdapter").apply {
            isAccessible = true
        }
        val adapter = adapterField.get(vh) as ItemBridgeAdapter
        adapter.setAdapterListener(object : AdapterListener() {
            override fun onBind(viewHolder: ItemBridgeAdapter.ViewHolder) {
                viewHolder.viewHolder.view.setOnLongClickListener { v ->
                    _longClickListener?.invoke(v, viewHolder.item) ?: false
                }
            }
        })

        super.initializeGridViewHolder(vh)
        val gridView = vh.gridView
        val top = 20
        val left = gridView.paddingLeft
        val right = gridView.paddingRight
        val bottom = gridView.paddingBottom
        gridView.setPadding(left, top, right, bottom)
        gridView.verticalSpacing = 50
        gridView.horizontalSpacing = 50
        super.initializeGridViewHolder(vh)
    }

    fun setOnLongClickListener(listener: (view: View, item: Any?) -> Boolean) {
        _longClickListener = listener
    }
}