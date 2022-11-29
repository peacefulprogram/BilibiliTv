/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jing.bilibilitv.playback

import android.content.Context
import android.view.KeyEvent
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow.FastForwardAction
import androidx.leanback.widget.PlaybackControlsRow.RewindAction
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import java.util.concurrent.TimeUnit

/**
 * Custom [PlaybackTransportControlGlue] that exposes a callback when the progress is updated.
 *
 * The callback is triggered based on a progress interval defined in several ways depending on the
 * [PlayerAdapter].
 *
 * [LeanbackPlayerAdapter] example:
 * ```
 *     private val updateMillis = 16
 *     LeanbackPlayerAdapter(context, exoplayer, updateMillis)
 * ```
 *
 * [MediaPlayerAdapter] example:
 * ```
 *     object : MediaPlayerAdapter(context) {
 *         private val updateMillis = 16
 *         override fun getProgressUpdatingInterval(): Int {
 *             return updateMillis
 *         }
 *     }
 * ```
 */
class ProgressTransportControlGlue<T : PlayerAdapter>(
    context: Context,
    playerAdapter: T,
    private val onCreatePrimaryAction: (ArrayObjectAdapter) -> Unit = {},
    private val onCreateSecondaryAction: (ArrayObjectAdapter) -> Unit = {},
    private val onUpdateProgressCb: () -> Unit = {}
) : PlaybackTransportControlGlue<T>(context, playerAdapter) {

    private val actionCallbackList = mutableListOf<GlueActionCallback>()

    private var _keyEventInterceptor: (KeyEvent) -> Boolean = { false }

    fun addActionCallback(callback: GlueActionCallback) {
        actionCallbackList.add(callback)
    }

    fun setKeyEventInterceptor(interceptor: (KeyEvent) -> Boolean) {
        _keyEventInterceptor = interceptor
    }

    // Define actions for fast forward and rewind operations.
    @VisibleForTesting
    var skipForwardAction: FastForwardAction = FastForwardAction(context)

    @VisibleForTesting
    var skipBackwardAction: RewindAction = RewindAction(context)

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(primaryActionsAdapter)
        onCreatePrimaryAction.invoke(primaryActionsAdapter)
    }

    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        onCreateSecondaryAction.invoke(secondaryActionsAdapter)
    }

    override fun onUpdateProgress() {
        super.onUpdateProgress()
        onUpdateProgressCb()
    }

    override fun onActionClicked(action: Action) {
        when (action) {
            skipBackwardAction -> skipBackward()
            skipForwardAction -> skipForward()
            else -> {
                if (!dispatchAction(action)) {
                    super.onActionClicked(action)
                }
            }
        }
    }

    private fun dispatchAction(action: Action): Boolean {
        val cb = actionCallbackList.find { it.support(action) } ?: return false
        cb.onAction(action)
        return true
    }

    /** Skips backward 30 seconds.  */
    private fun skipBackward() {
        var newPosition: Long = currentPosition - THIRTY_SECONDS
        newPosition = newPosition.coerceAtLeast(0L)
        playerAdapter.seekTo(newPosition)
    }

    /** Skips forward 30 seconds.  */
    private fun skipForward() {
        var newPosition: Long = currentPosition + THIRTY_SECONDS
        newPosition = newPosition.coerceAtMost(duration)
        playerAdapter.seekTo(newPosition)
    }


    override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
        if (_keyEventInterceptor.invoke(event)) {
            return true
        }
        return super.onKey(v, keyCode, event)
    }


    companion object {
        private val THIRTY_SECONDS = TimeUnit.SECONDS.toMillis(30)
    }
}
