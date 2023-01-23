package com.jing.bilibilitv.ext

import org.brotli.dec.BrotliInputStream
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


fun ByteArray.decompressBrotli(): ByteArray {
    val input = BrotliInputStream(this.inputStream())
    val output = ByteArrayOutputStream()
    input.copyTo(output)
    val result = output.toByteArray()
    input.close()
    output.close()
    return result
}