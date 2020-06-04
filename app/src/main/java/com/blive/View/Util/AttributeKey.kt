package com.blive.View.Util

import android.text.TextUtils
import com.blive.Models.ChannelData
import java.util.*

object AttributeKey {
    const val KEY_ANCHOR_ID = "anchorId"
    val KEY_SEAT_ARRAY = initSeatKeys()
    const val KEY_USER_INFO = "userInfo"
    private fun initSeatKeys(): Array<String?> {
        val strings = arrayOfNulls<String>(ChannelData.MAX_SEAT_NUM)
        for (i in strings.indices) {
            strings[i] = String.format(Locale.getDefault(), "seat%d", i)
        }
        return strings
    }

    fun indexOfSeatKey(key: String?): Int {
        for (i in KEY_SEAT_ARRAY.indices) {
            if (TextUtils.equals(key, KEY_SEAT_ARRAY[i])) return i
        }
        return -1
    }
}