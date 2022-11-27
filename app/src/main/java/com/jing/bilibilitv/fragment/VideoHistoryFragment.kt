package com.jing.bilibilitv.fragment

import android.os.Bundle
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import coil.load
import coil.transform.RoundedCornersTransformation
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.HistoryCardLbLayoutBinding
import com.jing.bilibilitv.ext.secondsToDuration
import com.jing.bilibilitv.home.getHomeGridViewKeyInterceptor
import com.jing.bilibilitv.http.data.HistoryItem
import com.jing.bilibilitv.model.VideoHistoryViewModel
import com.jing.bilibilitv.presenter.CustomGridViewPresenter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VideoHistoryFragment(private val getSelectTabView: () -> View? = { null }) :
    VerticalGridSupportFragment(), IVPShowAwareFragment, IRefreshableFragment {

    private val viewModel by activityViewModels<VideoHistoryViewModel>()

    private var pagingAdapter: PagingDataAdapter<HistoryItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (pagingAdapter == null) {
            pagingAdapter = PagingDataAdapter(VideoHistoryPresenter(), VideoHistoryDiff)
        }
        gridPresenter =
            CustomGridViewPresenter(
                focusZoomFactor = FocusHighlight.ZOOM_FACTOR_NONE,
                useFocusDimmer = false,
                getSelectedTabView = getSelectTabView,
                keyEventInterceptor = getHomeGridViewKeyInterceptor(
                    getSelectedTabView = getSelectTabView,
                    refreshData = this::refreshData
                )
            ).apply {
                numberOfColumns = 4
            }
        adapter = pagingAdapter
        progressBarManager.enableProgressBar()
        progressBarManager.initialDelay = 0
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pager.collectLatest {
                    pagingAdapter!!.submitData(it)
                }
            }

        }
        onItemViewClickedListener =
            OnItemViewClickedListener { _, item, _, _ ->
                with(item as HistoryItem) {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToVideoPlayActivity(
                            history.oid.toString(),
                            history.bvid,
                            history.part
                        )
                    )
                }
            }

    }

    private class HistoryViewHolder(val viewBinding: HistoryCardLbLayoutBinding) :
        Presenter.ViewHolder(viewBinding.root)

    private object VideoHistoryDiff : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.history.oid == newItem.history.oid
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.history.oid == newItem.history.oid
                    && oldItem.progress == newItem.progress
        }
    }

    private class VideoHistoryPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val viewBinding = HistoryCardLbLayoutBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
            return HistoryViewHolder(viewBinding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val viewBinding = (viewHolder as HistoryViewHolder).viewBinding
            val historyItem = item as HistoryItem
            with(viewBinding) {
                title.text = historyItem.title
                cover.load(historyItem.cover) {
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
                videoDuration.text =
                    historyItem.progress.secondsToDuration() + "/" + historyItem.duration.secondsToDuration()
                username.text = historyItem.authorName
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }

    private fun refreshData() {
        pagingAdapter?.refresh()
    }

    override fun onVPShow() {
    }

    override fun doRefresh() {
        refreshData()
    }

}