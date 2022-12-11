package com.jing.bilibilitv.ext

import android.content.res.Resources
import android.util.TypedValue
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


val Number.dpToPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.spToPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )

fun Number.secondsToDuration(): String {
    val value = this.toLong()
    val seconds = value % 60
    val minutes = value / 60
    return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
}

fun Number.toShortText(): String {
    val longVal = this.toLong()
    if (longVal > 10000) {
        return longVal.floorDiv(1000).toDouble().div(10).toString() + "万"
    }
    return toString()
}


fun Long.secondsToDateString(): String =
    LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MM-dd"))