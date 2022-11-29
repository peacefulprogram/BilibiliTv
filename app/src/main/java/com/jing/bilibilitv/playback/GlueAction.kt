package com.jing.bilibilitv.playback

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Action
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