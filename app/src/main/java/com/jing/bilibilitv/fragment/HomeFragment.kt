package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.jing.bilibilitv.databinding.HomeFragmentLayoutBinding
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

    private fun getSelectTabview(): View? =
        viewBinding.tabContainer.getTabAt(viewBinding.tabContainer.selectedTabPosition)?.view

    private val defaultSelectTabIndex = 1


    private val pagerFragmentList by lazy<List<Pair<String, Fragment>>> {
        Log.d(TAG, "create pager fragment: ")
        listOf(
            Pair("历史", VideoHistoryFragment(this::getSelectTabview)),
            Pair("推荐", LeanbackRecommendationFragment(this::getSelectTabview)),
            Pair("动态", LeanbackDynamicFragment(this::getSelectTabview))
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
        viewBinding.tabContainer.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val target = pagerFragmentList[viewBinding.tabContainer.selectedTabPosition].second
                if (target is IVPShowAwareFragment && target.activity != null) target.onVPShow()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        viewBinding.tabContainer.addOnUnhandledKeyEventListener { _, event ->
            var result = false
            if (event.keyCode == KeyEvent.KEYCODE_MENU) {
                val target = pagerFragmentList[viewBinding.tabContainer.selectedTabPosition].second
                if (target is IRefreshableFragment && target.activity != null) {
                    target.doRefresh()
                    result = true
                }
            }
            result
        }
        viewBinding.tabContainer.setupWithViewPager(viewBinding.pager)
        viewBinding.tabContainer.getTabAt(defaultSelectTabIndex)?.select()
        return viewBinding.root
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

}