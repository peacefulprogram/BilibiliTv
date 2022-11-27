package com.jing.bilibilitv.playback

import master.flame.danmaku.danmaku.model.Danmaku
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext

fun createDefaultDanmakuContext() = DanmakuContext.create().apply {
    setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3f)
    isDuplicateMergingEnabled = false
    scrollSpeedFactor = 1.2f
    setScaleTextSize(1.2f)
    setMaximumLines(
        mapOf(
            Danmaku.TYPE_SCROLL_RL to 5,
            Danmaku.TYPE_FIX_TOP to 5,
            Danmaku.TYPE_FIX_BOTTOM to 8
        )
    )
    preventOverlapping(
        mapOf(
            Danmaku.TYPE_SCROLL_RL to true,
            Danmaku.TYPE_FIX_TOP to true,
            Danmaku.TYPE_FIX_BOTTOM to true
        )
    )

    setDanmakuTransparency(0.8f)
    setDanmakuMargin(5)
}