package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.leanback.R
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.ImageLoader
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.jing.bilibilitv.BuildConfig
import com.jing.bilibilitv.danmaku.VideoDanmakuParser
import com.jing.bilibilitv.danmaku.proto.DanmakuProto
import com.jing.bilibilitv.dialog.ChooseVideoQualityDialog
import com.jing.bilibilitv.ext.secondsToDuration
import com.jing.bilibilitv.http.data.VideoUrlAudio
import com.jing.bilibilitv.http.data.VideoUrlResponse
import com.jing.bilibilitv.http.data.VideoUrlVideo
import com.jing.bilibilitv.model.VideoPlayBackViewModel
import com.jing.bilibilitv.model.VideoPlayerDelegate
import com.jing.bilibilitv.playback.AsyncSeekDataProvider
import com.jing.bilibilitv.playback.ProgressTransportControlGlue
import com.jing.bilibilitv.playback.createDefaultDanmakuContext
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.ui.widget.DanmakuView
import okhttp3.OkHttpClient

class LbVideoPlaybackFragment(
    private val avid: String?,
    private val bvid: String?,
    private val okHttpClient: OkHttpClient
) : VideoSupportFragment(), IBackPressAwareFragment {

    private val TAG = LbVideoPlaybackFragment::class.java.simpleName

    private val viewModel by activityViewModels<VideoPlayBackViewModel>()

    private lateinit var playerDelegate: VideoPlayerDelegate

    private val exoPlayerDataSourceFactory = OkHttpDataSource.Factory { okHttpClient.newCall(it) }

    private var exoPlayer: ExoPlayer? = null

    private var exoPlayerGlue: ProgressTransportControlGlue<LeanbackPlayerAdapter>? = null

    private var qualityList: List<Pair<Int, String>> = emptyList()

    private var videoList: List<VideoUrlVideo> = emptyList()

    private var audioList: List<VideoUrlAudio> = emptyList()

    private lateinit var snapshotLoader: ImageLoader

    private var seekDataProvider: AsyncSeekDataProvider? = null

    private var currentQuality: Int = -1

    private var backPressed = false

    private var danmakuView: DanmakuView? = null

    private var danmakuContext: DanmakuContext? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(avid = avid, bvid = bvid)
        playerDelegate = viewModel.playerDelegate
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        danmakuView = DanmakuView(requireContext())
        val playbackRoot = root!!.findViewById<ViewGroup>(R.id.playback_fragment_root)
        playbackRoot.addView(danmakuView, 1)
        return root
    }

    private fun initDanmaku(danmakuElList: List<DanmakuProto.DanmakuElem>) {
        if (danmakuContext != null) {
            return
        }
        danmakuContext = createDefaultDanmakuContext()
        danmakuView!!.enableDanmakuDrawingCache(true)
        danmakuView!!.showFPS(BuildConfig.DEBUG)
        danmakuView!!.show()
        danmakuView!!.setCallback(object : DrawHandler.Callback {
            override fun prepared() {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (exoPlayer?.isPlaying == true) {
                        with(danmakuView!!) {
                            seekTo(exoPlayer?.currentPosition)
                            danmakuView?.start()
                        }
                    }
                }
            }

            override fun updateTimer(danmakuTimer: DanmakuTimer) {}
            override fun danmakuShown(baseDanmaku: BaseDanmaku) {}
            override fun drawingFinished() {}
        })
        danmakuView?.prepare(VideoDanmakuParser(danmakuElList), danmakuContext)
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snapshotLoader = ImageLoader(requireContext())
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewUrlState.collectLatest {
                    if (it is Resource.Success) {
                        Log.d(TAG, "onViewCreated: video url load success")
                        onUrlResponse(it.data)
                        var seekTo = -1L
                        var showToast = false
                        if (playerDelegate.resumePosition > 0) {
                            seekTo = playerDelegate.resumePosition
                        } else if (it.data.lastPlayTime > 0 && it.data.lastPlayTime / 1000 < it.data.dash!!.duration - 10) {
                            seekTo = it.data.lastPlayTime
                            showToast = true
                        }
                        buildMediaSourceAndPlay(seekTo)
                        if (showToast) {
                            Toast.makeText(
                                requireContext(),
                                "已定位到上次播放位置: ${(seekTo / 1000).secondsToDuration()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.snapshotResponse.collectLatest {
                    if (it is Resource.Success) {
                        seekDataProvider?.snapshotResponse = it.data
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.danmakuData.collectLatest { it ->
                    when (it) {
                        is Resource.Success -> {
                            Log.d(TAG, "onViewCreated: danma get")
                            initDanmaku(it.data)
                        }
                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.titleState.collectLatest {
                    exoPlayerGlue?.title = it
                }
            }
        }
    }

    private fun onUrlResponse(videoUrlResponse: VideoUrlResponse) {
        qualityList = videoUrlResponse.supportFormats.map { Pair(it.quality, it.newDescription) }
        videoList = videoUrlResponse.dash!!.video
        audioList = videoUrlResponse.dash.audio
        currentQuality = if (videoList.isNotEmpty()) {
            videoList.find { it.id < 120 }?.id ?: videoList[0].id
        } else {
            -1
        }
    }

    private fun buildMediaSourceAndPlay(seekTo: Long = -1) {
        val video = videoList.find { it.id == currentQuality }
        if (video == null) {
            Toast.makeText(requireContext(), "未发现视频", Toast.LENGTH_LONG).show()
            return
        }
        val factory = ProgressiveMediaSource.Factory(exoPlayerDataSourceFactory)
        val sources = arrayOf(
            factory.createMediaSource(MediaItem.fromUri(video.baseUrl)), factory.createMediaSource(
                MediaItem.fromUri(audioList.minBy { it.bandwidth }.baseUrl)
            )
        )
        exoPlayer!!.setMediaSource(MergingMediaSource(*sources))
        exoPlayer!!.prepare()
        if (seekTo > 0) {
            exoPlayer!!.seekTo(seekTo)
        }
        exoPlayer!!.play()
    }


    private fun initPlayer() {
        Log.d(TAG, "initPlayer")
        exoPlayer = ExoPlayer.Builder(requireContext()).build().apply {
            prepareGlue(this)
            this.addListener(object : Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        playerDelegate.play()
                        danmakuView?.start(exoPlayer!!.currentPosition)
                    } else {
                        playerDelegate.pause()
                        danmakuView?.pause()
                    }
                }

            })
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayerGlue?.pause()
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        val context = requireContext()
        exoPlayerGlue = ProgressTransportControlGlue(
            context,
            LeanbackPlayerAdapter(
                context, localExoplayer, 200
            ),
            { playerDelegate.updateProgress(localExoplayer.currentPosition) },
            this::changeQuality
        ).apply {
            host = VideoSupportFragmentGlueHost(this@LbVideoPlaybackFragment)
            // Enable seek manually since PlaybackTransportControlGlue.getSeekProvider() is null,
            // so that PlayerAdapter.seekTo(long) will be called during user seeking.
            isControlsOverlayAutoHideEnabled = true
            isSeekEnabled = true
            title = viewModel.titleState.value
            seekDataProvider = AsyncSeekDataProvider(context, lifecycleScope)
            seekProvider = seekDataProvider
        }
    }

    private fun changeQuality() {
        if (qualityList.isEmpty()) {
            Toast.makeText(requireContext(), "无可切换的清晰度", Toast.LENGTH_SHORT).show()
            return
        }
        exoPlayer?.pause()
        ChooseVideoQualityDialog(qualityList, currentQuality) { qn ->
            val pos = exoPlayer!!.currentPosition
            currentQuality = qn.first
            buildMediaSourceAndPlay()
            exoPlayer!!.seekTo(pos)
        }.apply {
            showNow(this@LbVideoPlaybackFragment.requireActivity().supportFragmentManager, "")
        }
    }

    override fun onStop() {
        super.onStop()
        playerDelegate.resumePosition = exoPlayer!!.currentPosition
        playerDelegate.progress = playerDelegate.resumePosition
        playerDelegate.pause()
        destroyPlayer()
    }


    private fun destroyPlayer() {
        exoPlayer?.let {
            // Pause the player to notify listeners before it is released.
            it.pause()
            it.release()
            exoPlayer = null
        }
    }


    override fun onBackPressed(): Boolean {
        if (!playerDelegate.isPlaying) {
            backPressed = false
            return false
        }
        if (backPressed) {
            return false
        }
        backPressed = true
        Toast.makeText(requireContext(), "再按一次退出播放", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            delay(2000)
            backPressed = false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        danmakuView?.release()
        danmakuView = null
        danmakuContext = null
    }
}