package com.jing.bilibilitv.live.playback

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.R
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.jing.bilibilitv.BuildConfig
import com.jing.bilibilitv.ext.showLongToast
import com.jing.bilibilitv.http.data.LiveStreamUrlDurl
import com.jing.bilibilitv.playback.ProgressTransportControlGlue
import com.jing.bilibilitv.playback.createDefaultDanmakuContext
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.ui.widget.DanmakuView
import okhttp3.OkHttpClient

class LiveRoomPlaybackFragment(
    private val viewModel: LiveRoomPlaybackViewModel,
    private val okHttpClient: OkHttpClient
) :
    VideoSupportFragment() {
    private var exoPlayer: ExoPlayer? = null
    private val exoPlayerDataSourceFactory = OkHttpDataSource.Factory { okHttpClient.newCall(it) }
    private var danmakuView: DanmakuView? = null
    private lateinit var playbackRoot: ViewGroup
    private lateinit var danmakuContext: DanmakuContext
    private val danmukuParser = object : BaseDanmakuParser() {
        override fun parse(): IDanmakus = Danmakus()
    }

    private lateinit var glue: ProgressTransportControlGlue<LeanbackPlayerAdapter>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initExoplayer()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.liveStreamUrl.collectLatest {
                    when (it) {
                        is Resource.Success -> setupStreamUrl(it.data.durl)
                        is Resource.Error -> requireContext().showLongToast(it.getErrorMessage())
                        else -> {}
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.danmuFlow.collect {
                    if (danmakuView?.isEnabled == true) {
                        appendDanmu(it)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.liveRoomDetail.collectLatest {
                when (it) {
                    is Resource.Success -> glue.title = it.data.title
                    is Resource.Loading -> {}
                    is Resource.Error -> requireContext().showLongToast(it.getErrorMessage())
                }
            }
        }
        val root = super.onCreateView(inflater, container, savedInstanceState)
        danmakuView = DanmakuView(requireContext())
        playbackRoot = root!!.findViewById(R.id.playback_fragment_root)
        playbackRoot.addView(danmakuView, 1)
        initDanmu()
        return root
    }

    private fun appendDanmu(danmu: LiveRoomDanmu) {
        if (danmakuView?.isPrepared != true) {
            return
        }
        val danmaku =
            danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL) ?: return
        val textSize = danmu.fontSize * (danmukuParser.displayer.density - 0.6f)
        danmaku.text = danmu.text
        danmaku.padding = 0
        danmaku.priority = 0 // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = true
        danmaku.time = danmakuView!!.currentTime + 500
        danmaku.textSize = textSize
        danmaku.textColor = danmu.color
        danmaku.textShadowColor =
            if (danmu.transparentShadow) Color.TRANSPARENT else Color.BLACK
        danmakuView!!.addDanmaku(danmaku)
    }

    private fun initDanmu() {
        danmakuContext = createDefaultDanmakuContext()
        val dmView = danmakuView!!
        dmView.enableDanmakuDrawingCache(true)
        dmView.showFPS(BuildConfig.DEBUG)
        dmView.show()
        dmView.setCallback(object : DrawHandler.Callback {
            override fun prepared() {
                dmView.start()
            }

            override fun updateTimer(danmakuTimer: DanmakuTimer) {}
            override fun danmakuShown(baseDanmaku: BaseDanmaku) {
            }

            override fun drawingFinished() {}
        })
        dmView.prepare(danmukuParser, danmakuContext)
    }


    private fun setupStreamUrl(durl: List<LiveStreamUrlDurl>) {
        val player = exoPlayer
        player ?: return
        if (durl.isEmpty()) {
            requireContext().showLongToast("未获取到视频流")
            return
        }
        val mediaItemList = durl.map { url ->
            MediaItem.Builder()
                .setUri(url.url)
                .setLiveConfiguration(
                    MediaItem.LiveConfiguration.Builder()
                        .setMaxPlaybackSpeed(1.02f)
                        .build()
                )
                .build()
        }
        player.setMediaItems(mediaItemList)
        player.repeatMode = Player.REPEAT_MODE_ALL

        glue.play()
    }

    fun initExoplayer() {
        val mediaSourceFactory = DefaultMediaSourceFactory(requireContext()).apply {
            setDataSourceFactory(exoPlayerDataSourceFactory).apply {
                setLiveTargetOffsetMs(5000)
            }
        }
        val player = ExoPlayer.Builder(requireContext())
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                playWhenReady = true
                prepareGlue(this)
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        requireContext().showLongToast("播放出错:${error.message}")
                    }
                })
            }
        exoPlayer = player
    }

    private fun prepareGlue(player: ExoPlayer) {
        glue = ProgressTransportControlGlue(
            requireContext(),
            LeanbackPlayerAdapter(requireContext(), player, 100),
        ).apply {
            host = VideoSupportFragmentGlueHost(this@LiveRoomPlaybackFragment)
            isControlsOverlayAutoHideEnabled = true
            isSeekEnabled = false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        danmakuView?.release()
        danmakuView = null
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.release()
        exoPlayer = null
    }
}