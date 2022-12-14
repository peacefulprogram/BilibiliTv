package com.jing.bilibilitv.ext

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.Inflater

fun ByteArray.unzip(): ByteArray {
    val inflater = Inflater()
    inflater.setInput(this)
    val byteArrayOutputStream = ByteArrayOutputStream(size)
    try {
        val buff = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buff)
            byteArrayOutputStream.write(buff, 0, count)
        }
    } catch (ignored: Exception) {
    } finally {
        try {
            byteArrayOutputStream.close()
        } catch (ignored: IOException) {
        }
    }
    inflater.end()
    return byteArrayOutputStream.toByteArray()
}


fun ByteArray.decompressBrotli() {

}