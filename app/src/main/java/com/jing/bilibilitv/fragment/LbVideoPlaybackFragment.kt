package com.jing.bilibilitv.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.jing.bilibilitv.dialog.ChooseVideoQualityDialog
import com.jing.bilibilitv.http.data.VideoUrlAudio
import com.jing.bilibilitv.http.data.VideoUrlResponse
import com.jing.bilibilitv.http.data.VideoUrlVideo
import com.jing.bilibilitv.model.VideoPlayBackViewModel
import com.jing.bilibilitv.model.VideoPlayerDelegate
import com.jing.bilibilitv.playback.ProgressTransportControlGlue
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class LbVideoPlaybackFragment(
    private val avid: String?,
    private val bvid: String?,
    private val videoTitle: String,
    private val okHttpClient: OkHttpClient
) :
    VideoSupportFragment() {

    private val viewModel by activityViewModels<VideoPlayBackViewModel>()

    private lateinit var playerDelegate: VideoPlayerDelegate

    private val exoPlayerDataSourceFactory = OkHttpDataSource.Factory { okHttpClient.newCall(it) }

    private var exoPlayer: ExoPlayer? = null

    private var exoPlayerGlue: ProgressTransportControlGlue<LeanbackPlayerAdapter>? = null

    private var qualityList: List<Pair<Int, String>> = emptyList()

    private var videoList: List<VideoUrlVideo> = emptyList()

    private var audioList: List<VideoUrlAudio> = emptyList()

    private var currentQuality: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadVideoUrl(avid = avid, bvid)
        playerDelegate = viewModel.playerDelegate
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewUrlState.collectLatest {
                    if (it is Resource.Success) {
                        onUrlResponse(it.data)
                        buildMediaSourceAndPlay()
                    }
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

    private fun buildMediaSourceAndPlay() {
        val video = videoList.find { it.id == currentQuality }
        if (video == null) {
            Toast.makeText(requireContext(), "未发现视频", Toast.LENGTH_LONG).show()
            return
        }
        val factory = ProgressiveMediaSource.Factory(exoPlayerDataSourceFactory)
        val sources = arrayOf(
            factory.createMediaSource(MediaItem.fromUri(video.baseUrl)),
            factory.createMediaSource(
                MediaItem.fromUri(audioList.minBy { it.bandwidth }.baseUrl)
            )
        )
        exoPlayer!!.setMediaSource(MergingMediaSource(*sources))
        exoPlayer!!.prepare()
        exoPlayer!!.play()
    }


    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext())
            .build()
            .apply {
                prepareGlue(this)
                this.addListener(object : Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            playerDelegate.play()
                        } else {
                            playerDelegate.pause()
                        }
                    }

                })
            }
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        exoPlayerGlue = ProgressTransportControlGlue(
            requireContext(),
            LeanbackPlayerAdapter(
                requireContext(),
                localExoplayer,
                200
            ),
            { playerDelegate.updateProgress(localExoplayer.currentPosition) },
            this::changeQuality
        ).apply {
            host = VideoSupportFragmentGlueHost(this@LbVideoPlaybackFragment)
            // Enable seek manually since PlaybackTransportControlGlue.getSeekProvider() is null,
            // so that PlayerAdapter.seekTo(long) will be called during user seeking.
            isControlsOverlayAutoHideEnabled = true
            isSeekEnabled = true
            title = videoTitle
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
            this.onCancel(object : DialogInterface {
                override fun cancel() {
                    exoPlayerGlue?.play()
                }

                override fun dismiss() {
                }

            })

            showNow(requireActivity().supportFragmentManager, "")
        }
    }

    override fun onStop() {
        super.onStop()
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
}