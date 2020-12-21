package com.yotsufe.techresearch.models

import android.util.Base64
import java.io.File
import java.io.FileInputStream

class Base64Encoder {

    companion object {
        fun encorde(filePath: String): String {
            val file = File(filePath)
            val data = ByteArray(file.length().toInt())
            try {
                FileInputStream(file).use({ stream ->
                    stream.read(data, 0, data.size)
                })
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return Base64.encodeToString(data, Base64.DEFAULT)
        }
    }
}