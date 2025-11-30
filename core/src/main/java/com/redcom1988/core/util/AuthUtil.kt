package com.redcom1988.core.util

import android.nfc.Tag

fun extractNfcNumber(tag: Tag): String {
    return try {
        val tagId = tag.id
        tagId.joinToString("") { "%02X".format(it) }
    } catch (_: Exception) {
        ""
    }
}