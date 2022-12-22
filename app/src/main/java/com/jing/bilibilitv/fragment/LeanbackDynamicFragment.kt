package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.paging.PagingDataAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.VideoCardLbLayoutBinding
import com.jing.bilibilitv.home.getHomeGridViewKeyInterceptor
import com.jing.bilibilitv.http.data.DynamicItem
import com.jing.bilibilitv.model.DynamicViewModel
import com.jing.bilibilitv.presenter.CustomGridViewPresenter
import com.jing.bilibilitv.user.UserSpaceActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeanbackDynamicFragment(private val getSelectTabView: () -> View? = { null }) :
    VerticalGridSupportFragment(), IVPShowAwareFragment, IRefreshableFragment {

    private val TAG = LeanbackDynamicFragment::class.java.simpleName

    private val viewModel by activityViewModels<DynamicViewModel>()

    private var pagingAdapter: PagingDataAdapter<DynamicItem>? = null

    private lateinit var mGridPresenter: CustomGridViewPresenter

    private var startRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (pagingAdapter == null) {
            pagingAdapter = PagingDataAdapter(DynamicItemPresenter(), DynamicComparator).apply {
                this.addLoadStateListener {
                    when (it.refresh) {
                        LoadState.Loading -> startRefresh = true
                        is LoadState.NotLoading -> if (startRefresh) {
                            startRefresh = false
                            mGridPresenter.gridView?.selectedPosition = 0
                            Log.d(TAG, "dynamic refresh finish,thread ${Thread.currentThread()}")
                        }
                        else -> {}
                    }
                }
            }
        }
        adapter = pagingAdapter
        val columnsCount = 4
        mGridPresenter =
            CustomGridViewPresenter(
                focusZoomFactor = FocusHighlight.ZOOM_FACTOR_NONE,
                useFocusDimmer = false,
                interceptorFocusSearch = { direction, position, focused ->
                    if (position % columnsCount == 0 && direction == RecyclerView.FOCUS_LEFT) {
                        focused
                    } else if (position < columnsCount && direction == RecyclerView.FOCUS_UP) {
                        getSelectTabView()
                    } else {
                        null
                    }
                },
                keyEventInterceptor = getHomeGridViewKeyInterceptor(
                    getSelectedTabView = getSelectTabView,
                    refreshData = this::onVPShow
                )
            ).apply {
                numberOfColumns = columnsCount
                setOnLongClickListener { _, item ->
                    if (item != null) {
                        with(item as DynamicItem) {
                            UserSpaceActivity.navigateTo(modules.moduleAuthor.mid, requireContext())
                        }
                        true
                    } else {
                        false
                    }
                }
            }
        gridPresenter = mGridPresenter
        progressBarManager.enableProgressBar()
        progressBarManager.initialDelay = 0
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pager.collectLatest {
                pagingAdapter!!.submitData(it)
            }
        }
        onItemViewClickedListener =
            OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
                with(item as DynamicItem) {
                    val archive = this.modules.moduleDynamic.major!!.archive!!
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToVideoPlayActivity(
                            archive.aid,
                            archive.bvid,
                            archive.title
                        )
                    )
                }
            }

    }

    private class DynamicViewHolder(val viewBinding: VideoCardLbLayoutBinding) :
        Presenter.ViewHolder(viewBinding.root)

    private object DynamicComparator : DiffUtil.ItemCallback<DynamicItem>() {
        override fun areItemsTheSame(oldItem: DynamicItem, newItem: DynamicItem): Boolean {
            return oldItem.idStr == newItem.idStr
        }

        override fun areContentsTheSame(oldItem: DynamicItem, newItem: DynamicItem): Boolean {
            val oldArchive = oldItem.modules.moduleDynamic.major!!.archive!!
            val newArchive = newItem.modules.moduleDynamic.major!!.archive!!
            return oldItem.idStr == newItem.idStr
                    && oldArchive.stat.play == newArchive.stat.play
                    && oldArchive.stat.danmaku == newArchive.stat.danmaku
                    && oldItem.modules.moduleAuthor.pubTime == newItem.modules.moduleAuthor.pubTime
        }
    }

    private class DynamicItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val viewBinding = VideoCardLbLayoutBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
            return DynamicViewHolder(viewBinding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val viewBinding = (viewHolder as DynamicViewHolder).viewBinding
            val dynamic = item as DynamicItem
            val archive = dynamic.modules.moduleDynamic.major!!.archive!!
            val author = dynamic.modules.moduleAuthor
            with(viewBinding) {
                title.text = archive.title
                cover.load(archive.cover) {
                    transformations(
                        RoundedCornersTransformation(
                            root.context.resources.getDimension(
                                R.dimen.video_card_radius
                            )
                        )
                    )
                }
                root.setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        cover.background =
                            ContextCompat.getDrawable(
                                view.context,
                                R.drawable.video_card_border
                            )
                    } else {
                        cover.background = null
                    }
                }
                playCount.text = archive.stat.play
                danmuCount.text = archive.stat.danmaku
                videoDuration.text = archive.durationText
                username.text = author.name
                pubDate.text = author.pubTime
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }

    private fun refreshData() {
        setSelectedPosition(0)
        pagingAdapter?.refresh()
    }

    override fun onVPShow() {
        refreshData()
    }

    override fun doRefresh() {
        refreshData()
    }

}