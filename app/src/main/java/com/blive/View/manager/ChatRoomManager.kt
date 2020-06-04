package io.agora.chatroom.manager

import android.content.Context
import android.util.Log
import com.blive.Models.ChannelData
import com.blive.Models.Constant
import com.blive.Models.Member
import com.blive.Models.Message
import com.blive.View.Util.AttributeKey
import io.agora.chatroom.manager.RtcManager.RtcEventListener
import io.agora.chatroom.manager.RtmManager.RtmEventListener
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmChannelMember
import io.agora.rtm.RtmMessage

class ChatRoomManager private constructor(context: Context) : SeatManager(), MessageManager {

    private val TAG = ChatRoomManager::class.java.simpleName

    override val channelData: ChannelData
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val messageManager: MessageManager
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val rtcManager: RtcManager
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val rtmManager: RtmManager
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

     val mRtcManager: RtcManager = RtcManager.instance(context)!!
    var mRtmManager: RtmManager = RtmManager.instance(context)!!
     var mListener: ChatRoomEventListener? = null
     val mChannelData = ChannelData()


    fun setListener(listener: ChatRoomEventListener?) {
        mListener = listener
    }

    fun joinChannel(channelId: String?) {
        mRtmManager.login(Constant.sUserId, object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                val member = Member(Constant.sUserId.toString(), Constant.sName, Constant.sAvatarIndex)
                mRtmManager.setLocalUserAttributes(AttributeKey.KEY_USER_INFO, member.toJsonString())
                mRtcManager.joinChannel(channelId, Constant.sUserId)
            }

            override fun onFailure(errorInfo: ErrorInfo) {}
        })
    }

    fun leaveChannel() {
        mRtcManager.leaveChannel()
        mRtmManager.leaveChannel()
        mChannelData.release()
    }

    private fun checkAndBeAnchor() {
        val myUserId = Constant.sUserId.toString()
        if (mChannelData.isAnchorMyself) {
            var index = mChannelData.indexOfSeatArray(myUserId)
            if (index == -1) {
                index = mChannelData.firstIndexOfEmptySeat()
            }
            toBroadcaster(myUserId, index)
        } else {
            if (mChannelData.hasAnchor()) return
            mRtmManager.addOrUpdateChannelAttributes(AttributeKey.KEY_ANCHOR_ID, myUserId, object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    toBroadcaster(myUserId, mChannelData.firstIndexOfEmptySeat())
                }

                override fun onFailure(errorInfo: ErrorInfo) {}
            })
        }
    }

    fun givingGift() {
        val message = Message(Message.MESSAGE_TYPE_GIFT, null, Constant.sUserId)
        mRtmManager.sendMessage(message.toJsonString(), object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                if (mListener != null) mListener!!.onUserGivingGift(message.sendId)
            }

            override fun onFailure(errorInfo: ErrorInfo) {}
        })
    }

   /* override fun sendOrder(userId: String, orderType: String, content: String, callback: ResultCallback<Void>) {
        if (!mChannelData.isAnchorMyself) return
        val message = Message(orderType, content, Constant.sUserId)
        mRtmManager.sendMessageToPeer(userId, message.toJsonString(), callback)
    }

    override fun sendMessage(text: String) {
        val message = Message(text, Constant.sUserId)
        mRtmManager.sendMessage(message.toJsonString(), object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                addMessage(message)
            }

            override fun onFailure(errorInfo: ErrorInfo) {}
        })
    }

    override fun processMessage(rtmMessage: RtmMessage) {
        val message = Message.fromJsonString(rtmMessage.text)
        when (message.messageType) {
            Message.MESSAGE_TYPE_TEXT, Message.MESSAGE_TYPE_IMAGE -> addMessage(message)
            Message.MESSAGE_TYPE_GIFT -> if (mListener != null) mListener!!.onUserGivingGift(message.sendId)
            Message.MESSAGE_TYPE_ORDER -> {
                val myUserId = Constant.sUserId.toString()
                when (message.orderType) {
                    Message.ORDER_TYPE_AUDIENCE -> toAudience(myUserId, null)
                    Message.ORDER_TYPE_BROADCASTER -> toBroadcaster(myUserId, Integer.valueOf(message.content))
                    Message.ORDER_TYPE_MUTE -> muteMic(myUserId, java.lang.Boolean.valueOf(message.content))
                }
            }
        }
    }

    override fun addMessage(message: Message) {
        val position = mChannelData.addMessage(message)
        if (mListener != null) mListener!!.onMessageAdded(position)
    }*/

     val mRtcListener: RtcEventListener = object : RtcEventListener {
        override fun onJoinChannelSuccess(channelId: String?) {
            mRtmManager.joinChannel(channelId, null)
        }

        override fun onUserOnlineStateChanged(uid: Int, isOnline: Boolean) {
            if (isOnline) {
                mChannelData.addOrUpdateUserStatus(uid, false)
                if (mListener != null) mListener!!.onUserStatusChanged(uid.toString(), false)
            } else {
                mChannelData.removeUserStatus(uid)
                if (mListener != null) mListener!!.onUserStatusChanged(uid.toString(), null)
            }
        }

        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
            mChannelData.addOrUpdateUserStatus(uid, muted)
            if (mListener != null) mListener!!.onUserStatusChanged(uid.toString(), muted)
        }

        override fun onAudioMixingStateChanged(isPlaying: Boolean) {
            if (mListener != null) mListener!!.onAudioMixingStateChanged(isPlaying)
        }

        override fun onAudioVolumeIndication(uid: Int, volume: Int) {
            if (mListener != null) mListener!!.onAudioVolumeIndication(uid.toString(), volume)
        }
    }
    private val mRtmListener: RtmEventListener = object : RtmEventListener {
        override fun onChannelAttributesLoaded() {
            checkAndBeAnchor()
        }

        override fun onChannelAttributesUpdated(attributes: Map<String, String>?) {
            if (attributes != null) {
                for ((key, userId) in attributes) {
                    when (key) {
                        AttributeKey.KEY_ANCHOR_ID -> {
                            if (mChannelData.setAnchorId(userId)) Log.i(TAG, String.format("onChannelAttributesUpdated %s %s", key, userId))
                        }
                        else -> {
                            val index = AttributeKey.indexOfSeatKey(key)
                            if (index >= 0) {
                                if (updateSeatArray(index, userId)) {
                                    Log.i(TAG, String.format("onChannelAttributesUpdated %s %s", key, userId))
                                    if (mListener != null) mListener!!.onSeatUpdated(index)
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onInitMembers(members: List<RtmChannelMember>?) {
            if (members != null) {
                for (member in members) {
                    mChannelData.addOrUpdateMember(Member(member.userId))
                }
            }
            if (mListener != null) {
                mListener!!.onMemberListUpdated(null)
            }
        }

        override fun onMemberJoined(userId: String?, attributes: Map<String, String>?) {
            if (attributes != null) {
                for ((key, value) in attributes) {
                    if (AttributeKey.KEY_USER_INFO == key) {
                        val member = Member.fromJsonString(value)
                        mChannelData.addOrUpdateMember(member)
                        if (mListener != null) mListener!!.onMemberListUpdated(userId)
                        break
                    }
                }
            }
        }

        override fun onMemberLeft(userId: String?) {
            mChannelData.removeMember(userId)
            if (mListener != null) mListener!!.onMemberListUpdated(userId)
        }

        override fun onMessageReceived(message: RtmMessage?) {
            processMessage(message)
        }
    }

    companion object {
        private var instance: ChatRoomManager? = null
        @JvmStatic
        fun instance(context: Context?): ChatRoomManager? {
            if (instance == null) {
                synchronized(ChatRoomManager::class.java) { if (instance == null) instance = context?.let { ChatRoomManager(it) } }
            }
            return instance
        }
    }

    init {
        mRtcManager.setListener(mRtcListener)
        mRtmManager = RtmManager.instance(context)!!
        mRtmManager.setListener(mRtmListener)
    }

    override fun sendOrder(userId: String?, orderType: String?, content: String?, callback: ResultCallback<Void?>?) {
        if (!mChannelData.isAnchorMyself) return
        val message = Message(orderType, content, Constant.sUserId)
        mRtmManager.sendMessageToPeer(userId, message.toJsonString(), callback)
    }

    override fun sendMessage(text: String?) {
        val message = Message(text, Constant.sUserId)
        mRtmManager.sendMessage(message.toJsonString(), object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                addMessage(message)
            }

            override fun onFailure(errorInfo: ErrorInfo) {}
        })
    }

    override fun processMessage(rtmMessage: RtmMessage?) {
        val message = Message.fromJsonString(rtmMessage!!.text)
        when (message.messageType) {
            Message.MESSAGE_TYPE_TEXT, Message.MESSAGE_TYPE_IMAGE -> addMessage(message)
            Message.MESSAGE_TYPE_GIFT -> if (mListener != null) mListener!!.onUserGivingGift(message.sendId)
            Message.MESSAGE_TYPE_ORDER -> {
                val myUserId = Constant.sUserId.toString()
                when (message.orderType) {
                    Message.ORDER_TYPE_AUDIENCE -> toAudience(myUserId, null)
                    Message.ORDER_TYPE_BROADCASTER -> toBroadcaster(myUserId, Integer.valueOf(message.content!!))
                    Message.ORDER_TYPE_MUTE -> muteMic(myUserId, java.lang.Boolean.valueOf(message.content))
                }
            }
        }
    }

    override fun addMessage(message: Message?) {
        val position = mChannelData.addMessage(message!!)
        if (mListener != null) mListener!!.onMessageAdded(position)
    }

}