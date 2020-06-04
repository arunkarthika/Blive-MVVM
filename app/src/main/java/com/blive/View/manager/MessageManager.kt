package io.agora.chatroom.manager

import com.blive.Models.Message
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmMessage

interface MessageManager {
    fun sendOrder(userId: String?, orderType: String?, content: String?, callback: ResultCallback<Void?>?)
    fun sendMessage(text: String?)
    fun processMessage(rtmMessage: RtmMessage?)
    fun addMessage(message: Message?)
}