package com.jing.bilibilitv.danmaku

import android.graphics.Color
import com.jing.bilibilitv.danmaku.proto.DanmakuProto
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser

class VideoDanmakuParser(private val danmakuElList: List<DanmakuProto.DanmakuElem>) :
    BaseDanmakuParser() {

    override fun parse(): IDanmakus {
        val result = Danmakus()
        danmakuElList.forEach { el ->
            if (supportDanmakuMode(el.mode)) {
                result.addItem(buildDanmaku(el))
            }
        }
        return result
    }


    private fun supportDanmakuMode(mode: Int): Boolean {
        return BaseDanmaku.TYPE_SCROLL_RL == mode || BaseDanmaku.TYPE_FIX_TOP == mode || BaseDanmaku.TYPE_FIX_BOTTOM == mode || BaseDanmaku.TYPE_SCROLL_LR == mode
    }

    fun buildDanmaku(elem: DanmakuProto.DanmakuElem): BaseDanmaku {
        val danmaku = mContext.mDanmakuFactory.createDanmaku(elem.mode, mContext)
        if (danmaku != null) {
            danmaku.text = elem.getContent()
            danmaku.padding = 0
            danmaku.priority = 0 // 可能会被各种过滤器过滤并隐藏显示
            danmaku.time = elem.progress.toLong()
            danmaku.textSize = elem.fontSize * (displayer.density - 0.6f)
            danmaku.textColor = elem.color
            danmaku.textShadowColor =
                if (elem.color <= Color.BLACK) Color.TRANSPARENT else Color.BLACK
            danmaku.timer = mTimer
            danmaku.flags = mContext.mGlobalFlagValues
        }
        return danmaku
    }
}