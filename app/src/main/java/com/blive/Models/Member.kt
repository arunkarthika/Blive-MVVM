package com.blive.Models

import android.text.TextUtils
import com.google.gson.Gson

class Member(var userId: String?) {
    var name: String? = null
    var avatarIndex = 0

    constructor(userId: String?, name: String?, avatarIndex: Int) : this(userId) {
        this.name = name
        this.avatarIndex = avatarIndex
    }

    fun update(member: Member) {
        name = member.name
        avatarIndex = member.avatarIndex
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is Member) TextUtils.equals(userId, obj.userId) else super.equals(obj)
    }

    fun toJsonString(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJsonString(str: String?): Member {
            return Gson().fromJson(str, Member::class.java)
        }
    }

}