package com.jing.bilibilitv.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import coil.load
import com.jing.bilibilitv.databinding.LiveSubAreaLayoutBinding
import com.jing.bilibilitv.ext.showLongToast
import com.jing.bilibilitv.http.data.LiveRoomArea
import com.jing.bilibilitv.http.data.LiveRoomSubArea
import com.jing.bilibilitv.resource.Resource
import com.jing.bilibilitv.view.CustomRecyclerview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LiveRoomAreaFragment(
    private val viewModel: BilibiliLiveViewModel,
    private val tabContainer: CustomRecyclerview
) : RowsSupportFragment() {

    private lateinit var progressBarManager: ProgressBarManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            val area = item as LiveRoomSubArea
        }
        progressBarManager = ProgressBarManager().apply {
            enableProgressBar()
            initialDelay = 10L
        }
        lifecycleScope.launch {
            viewModel.areaList.collectLatest {
                when (it) {
                    is Resource.Loading -> progressBarManager.show()
                    is Resource.Error -> {
                        progressBarManager.hide()
                        requireContext().showLongToast(it.getErrorMessage())
                    }
                    is Resource.Success -> {
                        progressBarManager.hide()
                        setupAdapter(it.data)
                    }
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            progressBarManager.setRootView(this as ViewGroup)
        }
    }

    private fun setupAdapter(areaList: List<LiveRoomArea>) {
        val rowsAdapter = ArrayObjectAdapter().apply {
            presenterSelector = ClassPresenterSelector().apply {
                addClassPresenter(
                    ListRow::class.java,
                    ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM, false).apply {
                        this.setNumRows(2)
                        this.shadowEnabled = false
                        this.selectEffectEnabled = false
                    })
            }
        }
        areaList.forEach { area ->
            val subAreaAdapter =
                ArrayObjectAdapter(SubAreaPresenter()).apply { area.list.forEach(this::add) }
            rowsAdapter.add(ListRow(HeaderItem(area.id, area.name), subAreaAdapter))
        }
        adapter = rowsAdapter
    }

    private inner class SubAreaPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val vb = LiveSubAreaLayoutBinding.inflate(LayoutInflater.from(parent!!.context))
            vb.root.tag = vb
            return ViewHolder(vb.root)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val area = item as LiveRoomSubArea
            with(viewHolder!!.view.tag as LiveSubAreaLayoutBinding) {
                areaImage.load(area.pic)
                areaName.text = area.name
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }


}