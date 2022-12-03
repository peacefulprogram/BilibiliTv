package com.jing.bilibilitv.playback

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Action
import androidx.leanback.widget.PlaybackControlsRow.MultiAction
import com.jing.bilibilitv.R

class ReplayAction(context: Context) : Action(10) {
    init {
        icon = ContextCompat.getDrawable(context, R.drawable.replay)
    }
}

class VideoQualityAction(context: Context) : Action(20) {
    init {
        icon = ContextCompat.getDrawable(context, R.drawable.video_quality)
    }
}

class PlayListAction(context: Context) : Action(30) {
    init {
        icon = ContextCompat.getDrawable(context, R.drawable.play_list)
    }
}

class DanmuToggleAction(context: Context, defaultEnable: Boolean) : MultiAction(40) {

    init {
        setDrawables(
            arrayOf(
                ContextCompat.getDrawable(context, R.drawable.danmu_enable),
                ContextCompat.getDrawable(context, R.drawable.danmu_disable)
            )
        )
        index = if (defaultEnable) 0 else 1

    }

    fun toggleState() {
        index = (index + 1) % 2
    }
}