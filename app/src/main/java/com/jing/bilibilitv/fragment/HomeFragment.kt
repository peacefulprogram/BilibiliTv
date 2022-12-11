package com.jing.bilibilitv.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager.widget.PagerAdapter
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.HomeFragmentLayoutBinding
import com.jing.bilibilitv.ext.dpToPx
import com.jing.bilibilitv.http.api.BilibiliApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName

    private var pagerAdapter: PagerAdapter? = null

    @Inject
    lateinit var bilibiliApi: BilibiliApi

    private lateinit var viewBinding: HomeFragmentLayoutBinding

    private var defaultSelectTabIndex = 1

    private lateinit var tabItems: List<CustomTabItem>

    private var selectedTabColor: Int = 0
    private var unselectedTabColor: Int = 0


    private val pagerFragmentList by lazy<List<Pair<String, Fragment>>> {
        listOf(
            Pair("推荐", LeanbackRecommendationFragment { viewBinding.tabContainer }),
            Pair("动态", LeanbackDynamicFragment { viewBinding.tabContainer }),
            Pair("历史", VideoHistoryFragment { viewBinding.tabContainer })
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = HomeFragmentLayoutBinding.inflate(inflater, container, false)
        if (pagerAdapter == null) {
            pagerAdapter = buildPagerAdapter()
        }
        viewBinding.pager.adapter = pagerAdapter
        initTabItems()
        selectedTabColor = ContextCompat.getColor(requireContext(), R.color.gray50)
        unselectedTabColor = ContextCompat.getColor(requireContext(), R.color.gray400)

        viewBinding.tabContainer.apply {
            lastFocusedPosition = defaultSelectTabIndex
            addItemDecoration(object : ItemDecoration() {
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
                    (newView as TextView).setTextColor(selectedTabColor)
                    newView.scaleX = scale
                    newView.scaleY = scale
                }
                if (oldView?.parent == viewBinding.tabContainer) {
                    oldView.scaleX = 1f
                    oldView.scaleY = 1f
                    if (newViewInTab) {
                        (oldView as TextView).setTextColor(unselectedTabColor)
                    }
                }
            }
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = TabItemAdapter {
                if (viewBinding.pager.currentItem != it) {
                    viewBinding.pager.currentItem = it
                }
            }
        }
        return viewBinding.root
    }

    private fun initTabItems() {
        tabItems = listOf(
            CustomTabItem.NavigationItem(
                text = "搜索"
            ) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchActivity())
            },
            CustomTabItem.PagerItem("推荐", 0),
            CustomTabItem.PagerItem("动态", 1),
            CustomTabItem.PagerItem("历史", 2),
        )
        defaultSelectTabIndex = 1
    }

    private fun buildPagerAdapter(): PagerAdapter =
        object : FragmentPagerAdapter(childFragmentManager) {
            override fun getCount(): Int {
                return pagerFragmentList.size
            }

            override fun getItem(position: Int): Fragment {
                return pagerFragmentList[position].second
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return pagerFragmentList[position].first
            }

        }


    private inner class TabItemAdapter(
        private val switchPage: (Int) -> Unit,
    ) :
        RecyclerView.Adapter<ViewHolder>() {

        private var init = false

        override fun getItemViewType(position: Int): Int = when (tabItems[position]) {
            is CustomTabItem.PagerItem -> 0
            is CustomTabItem.NavigationItem -> 1
        }

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
                    selectedTabColor
                } else {
                    unselectedTabColor
                }
                val item = tabItems[position]
                setTextColor(color)
                when (item) {
                    is CustomTabItem.PagerItem -> {
                        text = item.text
                        setOnFocusChangeListener { _, hasFocus ->
                            if (hasFocus) {
                                switchPage(item.pageIndex)
                            }
                        }
                    }
                    is CustomTabItem.NavigationItem -> {
                        text = item.text
                        setOnKeyListener { _, keyCode, _ ->
                            keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                        }
                        setOnClickListener {
                            item.onClick()
                        }
                    }
                }

            }
        }

        override fun getItemCount(): Int = tabItems.size

    }

}

sealed class CustomTabItem {
    data class PagerItem(val text: String, val pageIndex: Int) : CustomTabItem()
    data class NavigationItem(val text: String, val onClick: () -> Unit) : CustomTabItem()
}