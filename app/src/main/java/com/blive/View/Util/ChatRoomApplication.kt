package com.blive.View.Util

import android.app.Application
import io.agora.chatroom.manager.RtcManager
import io.agora.rtm.internal.RtmManager

class ChatRoomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RtcManager.instance(this)!!.init()
        RtcManager.instance(this)!!.init()
    }

    companion object {
        lateinit var instance: Application
    }

    init {
        instance = this
    }
}