package com.blive.Models

import android.text.TextUtils
import com.blive.View.Util.MemberUtil


object Constant {
    val sUserId = MemberUtil.userId
    fun isMyself(userId: String?): Boolean {
        return TextUtils.equals(userId, sUserId.toString())
    }

    val sName = MemberUtil.name
    val sAvatarIndex = MemberUtil.avatarIndex
}