package com.jing.bilibilitv.ext

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.getColorWithAlpha(@ColorRes resId: Int, alpha: Float) =
    Color.valueOf(ContextCompat.getColor(this, resId)).run {
        Color.valueOf(red(), green(), blue(), alpha)
    }


fun Context.showToast(text: String, duration: Int) = Toast.makeText(this, text, duration).show()

fun Context.showLongToast(text: String) = this.showToast(text, Toast.LENGTH_LONG)

fun Context.showShortToast(text: String) = this.showToast(text, Toast.LENGTH_SHORT)