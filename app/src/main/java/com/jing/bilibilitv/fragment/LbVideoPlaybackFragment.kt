package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.leanback.R
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.widget.Action
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
import com.jing.bilibilitv.dialog.PlayBackChooseDialog
import com.jing.bilibilitv.ext.secondsToDuration
import com.jing.bilibilitv.model.VideoPlayBackViewModel
import com.jing.bilibilitv.model.VideoPlayerDelegate
import com.jing.bilibilitv.model.VideoUrlAndQuality
import com.jing.bilibilitv.playback.*
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
) : VideoSupportFragment() {

    private val TAG = LbVideoPlaybackFragment::class.java.simpleName

    private val viewModel by activityViewModels<VideoPlayBackViewModel>()

    private lateinit var playerDelegate: VideoPlayerDelegate

    private val exoPlayerDataSourceFactory = OkHttpDataSource.Factory { okHttpClient.newCall(it) }

    private var exoPlayer: ExoPlayer? = null

    private var exoPlayerGlue: ProgressTransportControlGlue<LeanbackPlayerAdapter>? = null

    private lateinit var snapshotLoader: ImageLoader

    private var seekDataProvider: AsyncSeekDataProvider? = null

    private var backPressed = false

    private var danmakuView: DanmakuView? = null

    private var danmakuContext: DanmakuContext? = null

    private var isChooseDialogShown = false

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
        if (danmakuContext == null) {
            danmakuContext = createDefaultDanmakuContext()
        }
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
                viewModel.selectedVideoAndAudio.collectLatest { state ->
                    when (state) {
                        is Resource.Success -> {
                            var seekTo = -1L
                            var showToast = false
                            if (playerDelegate.resumePosition > 0) {
                                seekTo = playerDelegate.resumePosition
                            } else if (viewModel.lastPlayTime > 0 && viewModel.lastPlayTime / 1000 < viewModel.duration - 10) {
                                seekTo = viewModel.lastPlayTime
                                viewModel.lastPlayTime = 0
                                showToast = true
                            }
                            buildMediaSourceAndPlay(state.data, seekTo)
                            if (showToast) {
                                Toast.makeText(
                                    requireContext(),
                                    "已定位到上次播放位置: ${(seekTo / 1000).secondsToDuration()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is Resource.Error -> {
                            if (state.exception != null) {
                                Log.e(TAG, "加载视频链接异常", state.exception)
                            }
                            Toast.makeText(
                                requireContext(),
                                "加载视频错误:${state.getErrorMessage()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {}
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

    private fun buildMediaSourceAndPlay(videoAndAudio: VideoUrlAndQuality, seekTo: Long = -1) {
        val factory = ProgressiveMediaSource.Factory(exoPlayerDataSourceFactory)
        val urlList = mutableListOf(videoAndAudio.videoUrl)
        videoAndAudio.audioUrl?.let { urlList.add(it) }
        val sources =
            urlList.map { factory.createMediaSource(MediaItem.fromUri(it)) }.toTypedArray()
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

    private val replayActionCallback =
        object : GlueActionCallback {
            override fun support(action: Action): Boolean = action is ReplayAction

            override fun onAction(action: Action) {
                exoPlayerGlue?.seekTo(0L)
                exoPlayerGlue?.play()
                hideControlsOverlay(true)
            }

        }

    private val chooseVideoQualityActionCallback =
        object : GlueActionCallback {
            override fun support(action: Action): Boolean = action is VideoQualityAction

            override fun onAction(action: Action) {
                changeQuality()
            }

        }

    private val playListActionCallback = object : GlueActionCallback {
        override fun support(action: Action): Boolean = action is PlayListAction

        override fun onAction(action: Action) {
            openPlayListDialogAndChoose()
        }

    }


    private fun openPlayListDialogAndChoose() {
        if (viewModel.videoPages.isEmpty()) {
            Toast.makeText(requireContext(), "暂无可选分P", Toast.LENGTH_SHORT).show()
            return
        }
        if (isControlsOverlayVisible) {
            hideControlsOverlay(false)
        }
        PlayBackChooseDialog(
            viewModel.videoPages,
            viewModel.videoPages.indexOfFirst { it.cid == viewModel.currentCid },
            viewWidth = 300,
            getText = { index, page -> "${index + 1}.${page.part}" },
        ) {
            viewModel.changePage(it)
        }.apply {
            showDialog(this)
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayerGlue?.pause()
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        val context = requireContext()
        exoPlayerGlue = ProgressTransportControlGlue(
            context = context,
            playerAdapter = LeanbackPlayerAdapter(
                context, localExoplayer, 200
            ),
            onCreatePrimaryAction = {
                it.add(VideoQualityAction(requireContext()))
                it.add(ReplayAction(requireContext()))
                it.add(PlayListAction(requireContext()))
            }
        ) { playerDelegate.updateProgress(localExoplayer.currentPosition) }.apply {
            addActionCallback(replayActionCallback)
            addActionCallback(chooseVideoQualityActionCallback)
            addActionCallback(playListActionCallback)
            setKeyEventInterceptor { onKeyEvent(it) }

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
        if (viewModel.qualityList.isEmpty()) {
            Toast.makeText(requireContext(), "无可切换的清晰度", Toast.LENGTH_SHORT).show()
            return
        }
        if (isControlsOverlayVisible) {
            hideControlsOverlay(false)
        }
        PlayBackChooseDialog(
            viewModel.qualityList,
            viewModel.qualityList.indexOfFirst { it.first == viewModel.currentQn },
            viewWidth = 130,
            getText = { _, qn -> qn.second }
        ) {
            viewModel.changeQn(it.first)
        }.apply {
            showDialog(this)
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

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
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
        if (keyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER && !isControlsOverlayVisible) {
            if (exoPlayer?.isPlaying == true) {
                exoPlayer?.pause()
            } else {
                exoPlayer?.play()
            }
            return true
        }

        if (keyEvent.keyCode == KeyEvent.KEYCODE_MENU) {
            openPlayListDialogAndChoose()
            return true
        }
        return false
    }

    private fun <T> showDialog(dialog: PlayBackChooseDialog<T>) {
        if (isChooseDialogShown) {
            return
        }
        isChooseDialogShown = true
        dialog.dismissListener = { isChooseDialogShown = false }
        dialog.showNow(requireActivity().supportFragmentManager, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        danmakuView?.release()
        danmakuView = null
        danmakuContext = null
    }
}