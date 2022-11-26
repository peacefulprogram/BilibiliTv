package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.activityViewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
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
import com.jing.bilibilitv.http.data.VideoInfo
import com.jing.bilibilitv.model.RecommendationViewModel
import com.jing.bilibilitv.playback.VideoPlayActivity
import com.jing.bilibilitv.presenter.CustomGridViewPresenter
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LeanbackRecommendationFragment(private val getSelectTabView: () -> View? = { null }) :
    VerticalGridSupportFragment(), IRefreshableFragment {

    private val viewModel by activityViewModels<RecommendationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridPresenter = CustomGridViewPresenter(
            FocusHighlight.ZOOM_FACTOR_MEDIUM,
            false,
            getSelectTabView
        ).apply {
            this.numberOfColumns = 4
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    private fun buildAdapter(videoList: List<VideoInfo>) {

        val adapter = ArrayObjectAdapter(VideoCardPresenter())
        adapter.addAll(0, videoList)
        this.adapter = adapter
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
                pubDate.text = video.pubDate.toDateString()
                videoDuration.text = video.getDurationText()
            }

        }


        companion object {
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd")
        }

        private fun Int.toShortText(): String {
            if (this > 10000) {
                return this.floorDiv(1000).toDouble().div(10).toString() + "万"
            }
            return toString()
        }

        private fun Long.toDateString(): String {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
                .format(
                    dateFormatter
                )
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        }

    }

    override fun onRefresh(): Boolean {
        viewModel.loadRecommendation()
        return true
    }
}