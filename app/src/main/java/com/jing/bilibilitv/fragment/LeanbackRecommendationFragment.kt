package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.Presenter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.VideoCardLbLayoutBinding
import com.jing.bilibilitv.ext.secondsToDateString
import com.jing.bilibilitv.ext.toShortText
import com.jing.bilibilitv.home.getHomeGridViewKeyInterceptor
import com.jing.bilibilitv.http.data.VideoInfo
import com.jing.bilibilitv.model.RecommendationViewModel
import com.jing.bilibilitv.presenter.CustomGridViewPresenter
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LeanbackRecommendationFragment(private val getSelectTabView: () -> View? = { null }) :
    VerticalGridSupportFragment(), IVPShowAwareFragment, IRefreshableFragment {

    private val viewModel by activityViewModels<RecommendationViewModel>()

    private var mAdapter: ArrayObjectAdapter? = null

    private val diffCallback = object : DiffCallback<VideoInfo>() {
        override fun areItemsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoInfo, newItem: VideoInfo): Boolean {
            return oldItem.stat.view == newItem.stat.view
                    && oldItem.stat.danmuku == newItem.stat.view
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = ArrayObjectAdapter(VideoCardPresenter())
        gridPresenter = CustomGridViewPresenter(
            focusZoomFactor = FocusHighlight.ZOOM_FACTOR_NONE,
            useFocusDimmer = false,
            getSelectedTabView = getSelectTabView,
            keyEventInterceptor = getHomeGridViewKeyInterceptor(
                getSelectedTabView = getSelectTabView,
                refreshData = this::onVPShow
            )
        ).apply {
            this.numberOfColumns = 4
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        progressBarManager.enableProgressBar()
        progressBarManager.initialDelay = 0
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recommendationState.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            buildAdapter(it.data)
                            progressBarManager.hide()
                        }
                        is Resource.Loading -> progressBarManager.show()
                        else -> {
                            progressBarManager.hide()
                        }
                    }
                }
            }
        }
        adapter = mAdapter
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun buildAdapter(videoList: List<VideoInfo>) {
        mAdapter?.setItems(videoList, diffCallback)
    }


    private class VideoCardPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
            val vb =
                VideoCardLbLayoutBinding.inflate(
                    LayoutInflater.from(parent!!.context),
                    parent,
                    false
                )
            vb.root.tag = vb
            return ViewHolder(vb.root)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
            val video = item as VideoInfo
            with(viewHolder!!.view.tag as VideoCardLbLayoutBinding) {
                title.text = video.title
                cover.load(video.pic) {
                    transformations(
                        RoundedCornersTransformation(
                            root.context.resources.getDimension(
                                R.dimen.video_card_radius
                            )
                        )
                    )
                }
                root.setOnClickListener {
                    it.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToVideoPlayActivity(
                            video.id.toString(),
                            video.bvid,
                            video.title
                        )
                    )
                }
                root.setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        cover.background =
                            ContextCompat.getDrawable(view.context, R.drawable.video_card_border)
                    } else {
                        cover.background = null
                    }
                }
                playCount.text = video.stat.view.toShortText()
                danmuCount.text = video.stat.danmuku.toShortText()
                username.text = video.owner.name
                pubDate.text = video.pubDate.secondsToDateString()
                videoDuration.text = video.getDurationText()
            }

        }



        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }


    private fun refreshData() {
        viewModel.loadRecommendation()
    }

    override fun onVPShow() {
        refreshData()
    }

    override fun doRefresh() {
        refreshData()
    }
}