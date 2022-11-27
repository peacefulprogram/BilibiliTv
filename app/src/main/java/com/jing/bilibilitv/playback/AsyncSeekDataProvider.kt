package com.jing.bilibilitv.playback

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.leanback.widget.PlaybackSeekDataProvider
import coil.ImageLoader
import coil.request.ImageRequest
import com.jing.bilibilitv.http.data.VideoSnapshotResponse
import kotlinx.coroutines.CoroutineScope

class AsyncSeekDataProvider(
    private val context: Context,
    private val scope: CoroutineScope
) : PlaybackSeekDataProvider() {

    private val TAG = AsyncSeekDataProvider::class.java.simpleName

    private val imageLoader = ImageLoader(context)

    private var mSeekPositions: LongArray? = null

    var snapshotResponse: VideoSnapshotResponse? = null
        set(value) {
            if (value != null) {
                if (value.index.isNotEmpty() && value.index.size > 1) {
                    val arr = LongArray(value.index.size)
                    for (i in 1 until value.index.size) {
                        arr[i - 1] = value.index[i] * 1000
                    }
                    mSeekPositions = arr
                }

            }
            field = value
        }
    private val callbackMap: MutableMap<Int, MutableList<Pair<Int, ResultCallback>>> =
        mutableMapOf()

    private val bitMapMap: MutableMap<Int, Bitmap?> = mutableMapOf()


    override fun getSeekPositions(): LongArray? {
        return mSeekPositions
    }

    override fun getThumbnail(index: Int, callback: ResultCallback?) {
        callback ?: return
        if (snapshotResponse == null) {
            return
        }
        val resp = snapshotResponse!!
        val imageIndex = index / (resp.imgXSize * resp.imgYSize)
        if (imageIndex > resp.image.size) {
            return
        }
        if (!bitMapMap.containsKey(imageIndex)) {
            bitMapMap[imageIndex] = null
            ImageRequest.Builder(context)
                .data(resp.image[imageIndex].let {
                    if (it.startsWith("http")) {
                        it
                    } else {
                        "https:$it"
                    }
                })
                .size(resp.imgXSize * resp.imgXLen, resp.imgYSize * resp.imgYLen)
                .allowHardware(false)
                .listener(onError = { _, result ->
                    Log.e(
                        TAG,
                        "getThumbnail: ",
                        result.throwable
                    )
                }, onSuccess = { _, result ->
                    bitMapMap[imageIndex] = result.drawable.toBitmap()
                    processCallback(imageIndex)
                })
                .build()
                .apply {
                    imageLoader.enqueue(this)
                }
        } else if (bitMapMap[imageIndex] == null) {
            val list = callbackMap[imageIndex] ?: mutableListOf()
            list.add(Pair(index, callback))
            callbackMap[imageIndex] = list
        } else {
            invokeCb(index, bitMapMap[imageIndex]!!, callback)
        }
    }

    private fun processCallback(imageIndex: Int) {
        val bitmap = bitMapMap[imageIndex]
        bitmap ?: return
        val callBackList = callbackMap[imageIndex]
        if (callBackList == null || callBackList.isEmpty()) {
            return
        }
        callbackMap.remove(imageIndex)
        callBackList.forEach { pair ->
            invokeCb(pair.first, bitmap, pair.second)
        }
    }

    private fun invokeCb(index: Int, source: Bitmap, resultCallback: ResultCallback) {
        snapshotResponse ?: return
        with(snapshotResponse!!) {
            val partIndex = index % (imgXLen * imgYLen)
            val xOffset = imgXSize * (partIndex % imgXLen)
            val yOffset = imgYSize * (partIndex / imgXLen)
            resultCallback.onThumbnailLoaded(
                Bitmap.createBitmap(source, xOffset, yOffset, imgXSize, imgYSize),
                index
            )
        }
    }

    override fun reset() {
        super.reset()
        bitMapMap.clear()
        callbackMap.clear()
    }

}