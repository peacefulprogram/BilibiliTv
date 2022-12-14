package com.jing.bilibilitv.live

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.LiveActivityLayoutBinding
import com.jing.bilibilitv.ext.dpToPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BilibiliLiveActivity : FragmentActivity() {
    private lateinit var viewBinding: LiveActivityLayoutBinding

    private lateinit var vpFragmentList: List<Lazy<Fragment>>

    private val viewModel by viewModels<BilibiliLiveViewModel>()

    private val tabItemNames = listOf("我的关注", "分区")

    private var selectTabColor: Int = 0
    private var unselectTabColor: Int = 0

    private var defaultSelectTabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = LiveActivityLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding.root)
        initVp()
        initTab()
    }

    private fun initTab() {
        selectTabColor = ContextCompat.getColor(this, R.color.gray50)
        unselectTabColor = ContextCompat.getColor(this, R.color.gray400)
        viewBinding.tabContainer.apply {
            lastFocusedPosition = defaultSelectTabIndex
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val gap = 20.dpToPx.toInt()
                    outRect.left = gap
                    outRect.right = gap
                    outRect.top = gap
                }
            })
            viewTreeObserver.addOnGlobalFocusChangeListener { oldView, newView ->
                var newViewInTab = false
                val scale = 1.5f
                if (newView?.parent == viewBinding.tabContainer) {
                    newViewInTab = true
                    (newView as TextView).setTextColor(selectTabColor)
                    newView.scaleX = scale
                    newView.scaleY = scale
                }
                if (oldView?.parent == viewBinding.tabContainer) {
                    oldView.scaleX = 1f
                    oldView.scaleY = 1f
                    if (newViewInTab) {
                        (oldView as TextView).setTextColor(unselectTabColor)
                    }
                }
            }
            layoutManager =
                LinearLayoutManager(
                    this@BilibiliLiveActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter = TabItemAdapter()
        }
    }

    private fun initVp() {
        vpFragmentList = listOf(
            lazy { FollowedLiveRoomFragment(viewModel, viewBinding.tabContainer) },
            lazy { LiveRoomAreaFragment(viewModel, viewBinding.tabContainer) }
        )
        viewBinding.viewPager.adapter = ViewPagerAdapter()
    }

    private inner class TabItemAdapter : Adapter<ViewHolder>() {

        private var init = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val textView = TextView(parent.context).apply {
                focusable = View.FOCUSABLE
            }
            return object : ViewHolder(textView) {}
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.itemView as TextView) {
                val color = if (!init && position == defaultSelectTabIndex) {
                    init = true
                    selectTabColor
                } else {
                    unselectTabColor
                }
                setTextColor(color)
                text = tabItemNames[position]
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        with(viewBinding.viewPager) {
                            if (currentItem != position) {
                                currentItem = position
                            }
                        }
                    }
                }
                if (color == selectTabColor) {
                    requestFocus()
                }
            }
        }

        override fun getItemCount(): Int {
            return tabItemNames.size
        }

    }


    private inner class ViewPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {
        override fun getCount(): Int = vpFragmentList.size

        override fun getItem(position: Int): Fragment = vpFragmentList[position].value

    }


}