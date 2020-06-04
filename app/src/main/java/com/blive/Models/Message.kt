package com.blive.Models

import com.google.gson.Gson

class Message(var content: String?, sendId: Int) {
    var messageType = MESSAGE_TYPE_TEXT
    var orderType: String? = null
    var sendId: String

    constructor(messageType: Int, content: String?, sendId: Int) : this(content, sendId) {
        this.messageType = messageType
    }

    constructor(orderType: String?, content: String?, sendId: Int) : this(content, sendId) {
        messageType = MESSAGE_TYPE_ORDER
        this.orderType = orderType
    }

    fun toJsonString(): String {
        return Gson().toJson(this)
    }

    companion object {
        const val MESSAGE_TYPE_TEXT = 0
        const val MESSAGE_TYPE_IMAGE = 1
        const val MESSAGE_TYPE_GIFT = 2
        const val MESSAGE_TYPE_ORDER = 3
        const val ORDER_TYPE_AUDIENCE = "toAudience"
        const val ORDER_TYPE_BROADCASTER = "toBroadcaster"
        const val ORDER_TYPE_MUTE = "mute"
        fun fromJsonString(str: String?): Message {
            return Gson().fromJson(str, Message::class.java)
        }
    }

    init {
        this.sendId = sendId.toString()
    }
}