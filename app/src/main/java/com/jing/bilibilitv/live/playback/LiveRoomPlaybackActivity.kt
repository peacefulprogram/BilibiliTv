package com.jing.bilibilitv.live.playback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.jing.bilibilitv.R
import com.jing.bilibilitv.http.api.LiveApi
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class LiveRoomPlaybackActivity : FragmentActivity() {

    lateinit var viewModel: LiveRoomPlaybackViewModel

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var liveApi: LiveApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        val roomId = intent.extras!!.getLong(ROOM_ID_KEY)
        viewModel = ViewModelProvider(
            this,
            LiveRoomPlaybackViewModel.createViewModelFactory(roomId, liveApi, okHttpClient)
        )[LiveRoomPlaybackViewModel::class.java]
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.activity_playback,
                LiveRoomPlaybackFragment(viewModel, okHttpClient)
            )
            .commit()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startWebSocketConnection()
    }

    override fun onStop() {
        viewModel.stopWebSocketConnection()
        super.onStop()
    }

    companion object {

        const val ROOM_ID_KEY = "roomId"

        fun navigateTo(roomId: Long, context: Context) {
            Intent(context, LiveRoomPlaybackActivity::class.java).apply {
                putExtra(ROOM_ID_KEY, roomId)
                context.startActivity(this)
            }
        }
    }
}