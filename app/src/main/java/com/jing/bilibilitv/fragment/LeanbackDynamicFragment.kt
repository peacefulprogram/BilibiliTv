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
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import coil.transform.RoundedCornersTransformation
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.VideoCardLbLayoutBinding
import com.jing.bilibilitv.http.data.DynamicItem
import com.jing.bilibilitv.model.DynamicViewModel
import com.jing.bilibilitv.presenter.CustomGridViewPresenter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeanbackDynamicFragment(private val getSelectTabView: () -> View? = { null }) :
    VerticalGridSupportFragment(), IRefreshableFragment {

    private val viewModel by activityViewModels<DynamicViewModel>()

    private var pagingAdapter: PagingDataAdapter<DynamicItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (pagingAdapter == null) {
            pagingAdapter = PagingDataAdapter(DynamicItemPresenter(), DynamicComparator)
        }
//        pagingAdapter!!.addLoadStateListener {
//        }
        adapter = pagingAdapter
        gridPresenter =
            CustomGridViewPresenter(
                FocusHighlight.ZOOM_FACTOR_NONE,
                false,
                getSelectTabView
            ).apply {
                numberOfColumns = 4
            }
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

    override fun onRefresh(): Boolean {
        pagingAdapter?.refresh()
        return false
    }

}