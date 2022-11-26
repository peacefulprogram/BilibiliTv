package com.jing.bilibilitv.playback

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.jing.bilibilitv.R
import com.jing.bilibilitv.fragment.LbVideoPlaybackFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class VideoPlayActivity : FragmentActivity() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private val TAG = VideoPlayActivity::class.java.simpleName

    private var backPressed = false

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
                LbVideoPlaybackFragment(args.avid, args.bvid, args.title, okHttpClient)
            )
            .commit()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    override fun onBackPressed() {
        if (backPressed) {
            super.onBackPressed()
            return
        }
        backPressed = true
        Toast.makeText(this, "再按一次退出播放", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            delay(2000)
            backPressed = false
        }
    }
}