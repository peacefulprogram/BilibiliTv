package com.jing.bilibilitv.ext

import android.content.res.Resources
import android.util.TypedValue


val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )


fun Number.secondsToDuration(): String {
    val value = this.toLong()
    val seconds = value % 60
    val minutes = value / 60
    return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
}