package com.jing.bilibilitv.playback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.navigation.navArgs
import com.jing.bilibilitv.R
import com.jing.bilibilitv.fragment.LbVideoPlaybackFragment
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class VideoPlayActivity : FragmentActivity() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private val TAG = VideoPlayActivity::class.java.simpleName

    init {
        Log.d(TAG, "video activity: create")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        val args = navArgs<VideoPlayActivityArgs>().value
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.activity_playback,
                LbVideoPlaybackFragment(args.avid, args.bvid, okHttpClient)
            )
            .commit()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    companion object {
        fun navigateToPlayActivity(context: Context, avid: String?, bvid: String?, title: String) {
            Intent(context, VideoPlayActivity::class.java).apply {
                replaceExtras(VideoPlayActivityArgs(avid, bvid, title).toBundle())
                context.startActivity(this)
            }
        }
    }
}