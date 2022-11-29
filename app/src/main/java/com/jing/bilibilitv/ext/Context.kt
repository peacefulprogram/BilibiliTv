package com.jing.bilibilitv.ext

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.getColorWithAlpha(@ColorRes resId: Int, alpha: Float) =
    Color.valueOf(ContextCompat.getColor(this, resId)).run {
        Color.valueOf(red(), green(), blue(), alpha)
    }
