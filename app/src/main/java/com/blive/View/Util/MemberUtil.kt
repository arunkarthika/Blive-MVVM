package com.blive.View.Util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.annotation.ArrayRes
import com.blive.R

import java.util.*

object MemberUtil {
    private fun sp(): SharedPreferences {
        return ChatRoomApplication.instance.getSharedPreferences("member", Context.MODE_PRIVATE)
    }

    val userId: Int
        get() {
            var userId = sp().getInt("userId", 0)
            if (userId == 0) {
                userId = Math.abs(UUID.randomUUID().hashCode())
                sp().edit().putInt("userId", userId).apply()
            }
            return userId
        }

    val name: String?
        get() {
            var name = sp().getString("name", "")
            if (TextUtils.isEmpty(name)) {
                name = String.format("%s %s", randomName(R.array.random_surnames), randomName(R.array.random_names))
                sp().edit().putString("name", name).apply()
            }
            return name
        }

    private fun randomName(@ArrayRes resId: Int): String {
        val names = ChatRoomApplication.instance.resources.getStringArray(resId)
        return names[Random().nextInt(names.size - 1)]
    }

    val avatarIndex: Int
        get() {
            var avatarIndex = sp().getInt("avatarIndex", 0)
            if (avatarIndex == 0) {
                val images = ChatRoomApplication.instance.resources.obtainTypedArray(R.array.random_avatar_images)
                val length = images.length()
                images.recycle()
                avatarIndex = Random().nextInt(length - 1)
                sp().edit().putInt("avatarIndex", avatarIndex).apply()
            }
            return avatarIndex
        }

    fun getAvatarResId(index: Int): Int {
        val images = ChatRoomApplication.instance.resources.obtainTypedArray(R.array.random_avatar_images)
        val resId = images.getResourceId(index, R.mipmap.ic_blive)
        images.recycle()
        return resId
    }
}