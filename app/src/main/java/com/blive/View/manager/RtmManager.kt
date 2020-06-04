package io.agora.chatroom.manager

import android.content.Context
import android.text.TextUtils
import android.util.Log
import io.agora.rtm.*
import java.util.*

class RtmManager private constructor(context: Context) {
    interface RtmEventListener {
        fun onChannelAttributesLoaded()
        fun onChannelAttributesUpdated(attributes: Map<String, String>?)
        fun onInitMembers(members: List<RtmChannelMember>?)
        fun onMemberJoined(userId: String?, attributes: Map<String, String>?)
        fun onMemberLeft(userId: String?)
        fun onMessageReceived(message: RtmMessage?)
    }

    private val TAG = RtmManager::class.java.simpleName
    private val mContext: Context
    private var mListener: RtmEventListener? = null
    private var mRtmClient: RtmClient? = null
    private var mRtmChannel: RtmChannel? = null
    private var mIsLogin = false
    private var mChannelId: String? = null
    fun setListener(listener: RtmEventListener?) {
        mListener = listener
    }

    fun init() {
        if (mRtmClient == null) {
            try {
                mRtmClient = RtmClient.createInstance(mContext, mContext.getString(com.blive.R.string.app_id), mClientListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun login(userId: Int, callback: ResultCallback<Void?>?) {
        if (mRtmClient != null) {
            if (mIsLogin) {
                callback?.onSuccess(null)
                return
            }
            mRtmClient!!.login(mContext.getString(com.blive.R.string.rtm_token), userId.toString(), object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    Log.d(TAG, "rtm login success")
                    mIsLogin = true
                    callback?.onSuccess(aVoid)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Log.e(TAG, String.format("rtm join %s", errorInfo.errorDescription))
                    mIsLogin = false
                    callback?.onFailure(errorInfo)
                }
            })
        }
    }

    fun joinChannel(channelId: String?, callback: ResultCallback<Void?>?) {
        mChannelId = channelId
        if (mRtmChannel == null) {
            if (mRtmClient != null) mRtmChannel = mRtmClient!!.createChannel(mChannelId, mChannelListener)
        }
        if (mRtmChannel != null) {
            mRtmChannel!!.join(object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    Log.d(TAG, "rtm join success")
                    getChannelAttributes(mChannelId)
                    members
                    callback?.onSuccess(aVoid)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Log.e(TAG, String.format("rtm join %s", errorInfo.errorDescription))
                    callback?.onFailure(errorInfo)
                }
            })
        }
    }

    private fun getChannelAttributes(channelId: String?) {
        if (mRtmClient != null) {
            mRtmClient!!.getChannelAttributes(channelId, object : ResultCallback<List<RtmChannelAttribute>?> {
                override fun onSuccess(attributeList: List<RtmChannelAttribute>?) {
                    processChannelAttributes(attributeList)
                    if (mListener != null) mListener!!.onChannelAttributesLoaded()
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Log.e(TAG, String.format("getChannelAttributes %s", errorInfo.errorDescription))
                }
            })
        }
    }

    private fun processChannelAttributes(attributeList: List<RtmChannelAttribute>?) {
        if (attributeList != null) {
            val attributes: MutableMap<String, String> = HashMap()
            for (attribute in attributeList) {
                attributes[attribute.key] = attribute.value
            }
            if (mListener != null) mListener!!.onChannelAttributesUpdated(attributes)
        }
    }

    private val members: Unit
        private get() {
            if (mRtmChannel != null) {
                mRtmChannel!!.getMembers(object : ResultCallback<List<RtmChannelMember>> {
                    override fun onSuccess(rtmChannelMembers: List<RtmChannelMember>) {
                        if (mListener != null) mListener!!.onInitMembers(rtmChannelMembers)
                        for (member in rtmChannelMembers) {
                            getUserAttributes(member.userId)
                        }
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {}
                })
            }
        }

    private fun getUserAttributes(userId: String) {
        if (mRtmClient != null) {
            mRtmClient!!.getUserAttributes(userId, object : ResultCallback<List<RtmAttribute>> {
                override fun onSuccess(rtmAttributes: List<RtmAttribute>) {
                    Log.d(TAG, String.format("getUserAttributes %s", rtmAttributes.toString()))
                    processUserAttributes(userId, rtmAttributes)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Log.e(TAG, String.format("getUserAttributes %s", errorInfo.errorDescription))
                }
            })
        }
    }

    private fun processUserAttributes(userId: String, attributeList: List<RtmAttribute>?) {
        if (attributeList != null) {
            val attributes: MutableMap<String, String> = HashMap()
            for (attribute in attributeList) {
                attributes[attribute.key] = attribute.value
            }
            if (mListener != null) mListener!!.onMemberJoined(userId, attributes)
        }
    }

    fun setLocalUserAttributes(key: String?, value: String?) {
        if (mRtmClient != null) {
            val attribute = RtmAttribute(key, value)
            mRtmClient!!.setLocalUserAttributes(listOf(attribute), null)
        }
    }

    fun addOrUpdateChannelAttributes(key: String?, value: String?, callback: ResultCallback<Void?>?) {
        if (mRtmClient != null) {
            val attribute = RtmChannelAttribute(key, value)
            mRtmClient!!.addOrUpdateChannelAttributes(mChannelId, listOf(attribute), options(), object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    Log.d(TAG, String.format("addOrUpdateChannelAttributes %s %s", key, value))
                    callback?.onSuccess(aVoid)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Log.e(TAG, String.format("addOrUpdateChannelAttributes %s %s %s", key, value, errorInfo.errorDescription))
                    callback?.onFailure(errorInfo)
                }
            })
        }
    }

    private fun options(): ChannelAttributeOptions {
        return ChannelAttributeOptions(true)
    }

    fun deleteChannelAttributesByKey(key: String) {
        if (mRtmClient != null) mRtmClient!!.deleteChannelAttributesByKeys(mChannelId, listOf(key), options(), null)
    }

    fun sendMessage(content: String?, callback: ResultCallback<Void?>?) {
        if (mRtmClient != null) {
            val message = mRtmClient!!.createMessage(content)
            if (mRtmChannel != null) {
                mRtmChannel!!.sendMessage(message, object : ResultCallback<Void?> {
                    override fun onSuccess(aVoid: Void?) {
                        Log.d(TAG, String.format("sendMessage %s", content))
                        callback?.onSuccess(aVoid)
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        Log.e(TAG, String.format("sendMessage %s", errorInfo.errorDescription))
                        callback?.onFailure(errorInfo)
                    }
                })
            }
        }
    }

    fun sendMessageToPeer(userId: String?, content: String?, callback: ResultCallback<Void?>?) {
        if (TextUtils.isEmpty(userId)) return
        if (mRtmClient != null) {
            val message = mRtmClient!!.createMessage(content)
            mRtmClient!!.sendMessageToPeer(userId, message, null, object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    Log.d(TAG, String.format("sendMessageToPeer %s %s", userId, content))
                    callback?.onSuccess(aVoid)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Log.e(TAG, String.format("sendMessageToPeer %s", errorInfo.errorDescription))
                    callback?.onFailure(errorInfo)
                }
            })
        }
    }

    fun leaveChannel() {
        if (mRtmChannel != null) {
            mRtmChannel!!.leave(null)
            mRtmChannel!!.release()
            mRtmChannel = null
        }
    }

    private val mClientListener: RtmClientListener = object : RtmClientListener {
        override fun onConnectionStateChanged(i: Int, i1: Int) {}
        override fun onMessageReceived(rtmMessage: RtmMessage, s: String) {
            Log.i(TAG, String.format("onPeerMessageReceived %s %s", rtmMessage.text, s))
            if (mListener != null) mListener!!.onMessageReceived(rtmMessage)
        }

        override fun onTokenExpired() {}
        override fun onPeersOnlineStatusChanged(map: Map<String, Int>) {}
    }
    private val mChannelListener: RtmChannelListener = object : RtmChannelListener {
        override fun onMemberCountUpdated(i: Int) {}
        override fun onAttributesUpdated(list: List<RtmChannelAttribute>) {
            Log.i(TAG, "onAttributesUpdated")
            processChannelAttributes(list)
        }

        override fun onMessageReceived(rtmMessage: RtmMessage, rtmChannelMember: RtmChannelMember) {
            Log.i(TAG, String.format("onChannelMessageReceived %s %s", rtmMessage.text, rtmChannelMember.userId))
            if (mListener != null) mListener!!.onMessageReceived(rtmMessage)
        }

        override fun onMemberJoined(rtmChannelMember: RtmChannelMember) {
            val userId = rtmChannelMember.userId
            Log.i(TAG, String.format("onMemberJoined %s", userId))
            getUserAttributes(userId)
        }

        override fun onMemberLeft(rtmChannelMember: RtmChannelMember) {
            val userId = rtmChannelMember.userId
            Log.i(TAG, String.format("onMemberLeft %s", userId))
            if (mListener != null) mListener!!.onMemberLeft(userId)
        }
    }

    companion object {
         var instance: RtmManager? = null
        @JvmStatic
        fun instance(context: Context): RtmManager? {
            if (instance == null) {
                synchronized(RtmManager::class.java) { if (instance == null) instance = RtmManager(context) }
            }
            return instance
        }
    }

    init {
        mContext = context.applicationContext
    }
}