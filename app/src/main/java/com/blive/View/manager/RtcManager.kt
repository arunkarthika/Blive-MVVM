package io.agora.chatroom.manager

import android.content.Context
import android.util.Log
import com.blive.R
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine


class RtcManager private constructor(context: Context) {
    interface RtcEventListener {
        fun onJoinChannelSuccess(channelId: String?)
        fun onUserOnlineStateChanged(uid: Int, isOnline: Boolean)
        fun onUserMuteAudio(uid: Int, muted: Boolean)
        fun onAudioMixingStateChanged(isPlaying: Boolean)
        fun onAudioVolumeIndication(uid: Int, volume: Int)
    }

    private val TAG = RtcManager::class.java.simpleName
    private val mContext: Context
    private var mListener: RtcEventListener? = null
    private var mRtcEngine: RtcEngine? = null
    private var mUserId = 0
    fun setListener(listener: RtcEventListener?) {
        mListener = listener
    }

    fun init() {
        if (mRtcEngine == null) {
            try {
                mRtcEngine = RtcEngine.create(mContext, mContext.getString(R.string.agora_app_id), mEventHandler)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (mRtcEngine != null) {
            mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine!!.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT)
            mRtcEngine!!.enableAudioVolumeIndication(500, 3, false)
        }
    }

    fun joinChannel(channelId: String?, userId: Int) {
        if (mRtcEngine != null) mRtcEngine!!.joinChannel(mContext.getString(R.string.token), channelId, null, userId)
    }

    fun setClientRole(role: Int) {
        if (mRtcEngine != null) mRtcEngine!!.setClientRole(role)
    }

    fun muteAllRemoteAudioStreams(muted: Boolean) {
        if (mRtcEngine != null) mRtcEngine!!.muteAllRemoteAudioStreams(muted)
    }

    fun muteLocalAudioStream(muted: Boolean) {
        if (mRtcEngine != null) mRtcEngine!!.muteLocalAudioStream(muted)
        if (mListener != null) mListener!!.onUserMuteAudio(mUserId, muted)
    }

    fun startAudioMixing(filePath: String?) {
        if (mRtcEngine != null) {
            mRtcEngine!!.startAudioMixing(filePath, false, false, 1)
            mRtcEngine!!.adjustAudioMixingVolume(15)
        }
    }

    fun leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine!!.leaveChannel()
            setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        }
    }

    private val mEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            Log.i(TAG, String.format("onJoinChannelSuccess %s %d", channel, uid))
            mUserId = uid
            if (mListener != null) mListener!!.onJoinChannelSuccess(channel)
        }

        override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
            super.onClientRoleChanged(oldRole, newRole)
            Log.i(TAG, String.format("onClientRoleChanged %d %d", oldRole, newRole))
            if (mListener != null) {
                if (newRole == Constants.CLIENT_ROLE_BROADCASTER) mListener!!.onUserOnlineStateChanged(mUserId, true) else if (newRole == Constants.CLIENT_ROLE_AUDIENCE) mListener!!.onUserOnlineStateChanged(mUserId, false)
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.i(TAG, String.format("onUserJoined %d", uid))
            if (mListener != null) mListener!!.onUserOnlineStateChanged(uid, true)
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            Log.i(TAG, String.format("onUserOffline %d", uid))
            if (mListener != null) mListener!!.onUserOnlineStateChanged(uid, false)
        }

        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
            super.onUserMuteAudio(uid, muted)
            Log.i(TAG, String.format("onUserMuteAudio %d %b", uid, muted))
            if (mListener != null) mListener!!.onUserMuteAudio(uid, muted)
        }

        override fun onAudioVolumeIndication(speakers: Array<AudioVolumeInfo>, totalVolume: Int) {
            super.onAudioVolumeIndication(speakers, totalVolume)
            for (info in speakers) {
                if (info.volume > 0) {
                    val uid = if (info.uid == 0) mUserId else info.uid
                    if (mListener != null) mListener!!.onAudioVolumeIndication(uid, info.volume)
                }
            }
        }

        override fun onAudioMixingStateChanged(state: Int, errorCode: Int) {
            super.onAudioMixingStateChanged(state, errorCode)
            if (mListener != null) mListener!!.onAudioMixingStateChanged(state == Constants.MEDIA_ENGINE_AUDIO_EVENT_MIXING_PLAY)
        }
    }

    companion object {
        private var instance: RtcManager? = null
        @JvmStatic
        fun instance(context: Context): RtcManager? {
            if (instance == null) {
                synchronized(RtcManager::class.java) { if (instance == null) instance = RtcManager(context) }
            }
            return instance
        }
    }

    init {
        mContext = context.applicationContext
    }
}