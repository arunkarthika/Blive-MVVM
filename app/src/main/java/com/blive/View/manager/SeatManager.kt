package io.agora.chatroom.manager

import android.util.Log
import com.blive.Models.ChannelData
import com.blive.Models.Constant
import com.blive.Models.Message
import com.blive.Models.Seat
import com.blive.View.Util.AttributeKey
import com.google.gson.Gson

import io.agora.rtc.Constants
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback

abstract class SeatManager {
    private val TAG = SeatManager::class.java.simpleName
    abstract val channelData: ChannelData
    abstract val messageManager: MessageManager
    abstract val rtcManager: RtcManager
    abstract val rtmManager: RtmManager
    fun toBroadcaster(userId: String?, position: Int) {
        Log.d(TAG, String.format("toBroadcaster %s %d", userId, position))
        val channelData = channelData
        if (Constant.isMyself(userId)) {
            val index = channelData.indexOfSeatArray(userId)
            if (index >= 0) {
                if (position == index) {
                    rtcManager.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
                } else {
                    changeSeat(userId, index, position, object : ResultCallback<Void?> {
                        override fun onSuccess(aVoid: Void?) {
                            rtcManager.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
                        }

                        override fun onFailure(errorInfo: ErrorInfo) {}
                    })
                }
            } else {
                occupySeat(userId, position, object : ResultCallback<Void?> {
                    override fun onSuccess(aVoid: Void?) {
                        rtcManager.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {}
                })
            }
        } else {
            messageManager.sendOrder(userId, Message.ORDER_TYPE_BROADCASTER, position.toString(), null)
        }
    }

    fun toAudience(userId: String?, callback: ResultCallback<Void?>?) {
        Log.d(TAG, String.format("toAudience %s", userId))
        val channelData = channelData
        if (Constant.isMyself(userId)) {
            resetSeat(channelData.indexOfSeatArray(userId), object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    rtcManager.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
                    callback?.onSuccess(aVoid)
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    callback?.onFailure(errorInfo)
                }
            })
        } else {
            messageManager.sendOrder(userId, Message.ORDER_TYPE_AUDIENCE, null, callback)
        }
    }

    private fun occupySeat(userId: String?, position: Int, callback: ResultCallback<Void?>) {
        modifySeat(position, Seat(userId), callback)
    }

    private fun resetSeat(position: Int, callback: ResultCallback<Void?>?) {
        modifySeat(position, null, callback)
    }

    private fun changeSeat(userId: String?, oldPosition: Int, newPosition: Int, callback: ResultCallback<Void?>) {
        resetSeat(oldPosition, object : ResultCallback<Void?> {
            override fun onSuccess(aVoid: Void?) {
                occupySeat(userId, newPosition, callback)
            }

            override fun onFailure(errorInfo: ErrorInfo) {}
        })
    }

    fun muteMic(userId: String?, muted: Boolean) {
        if (Constant.isMyself(userId)) {
            if (!channelData.isUserOnline(userId)) return
            rtcManager.muteLocalAudioStream(muted)
        } else {
            if (!channelData.isAnchorMyself) return
            messageManager.sendOrder(userId, Message.ORDER_TYPE_MUTE, muted.toString(), null)
        }
    }

    fun closeSeat(position: Int) {
        val channelData = channelData
        if (!channelData.isAnchorMyself) return
        val seat = channelData.seatArray[position]
        if (seat != null) {
            val userId = seat.userId
            if (channelData.isUserOnline(userId)) {
                toAudience(userId, object : ResultCallback<Void?> {
                    override fun onSuccess(aVoid: Void?) {
                        modifySeat(position, Seat(true), null)
                    }

                    override fun onFailure(errorInfo: ErrorInfo) {}
                })
                return
            }
        }
        modifySeat(position, Seat(true), null)
    }

    fun openSeat(position: Int) {
        if (!channelData.isAnchorMyself) return
        resetSeat(position, null)
    }

    private fun modifySeat(position: Int, seat: Seat?, callback: ResultCallback<Void?>?) {
        if (position >= 0 && position < AttributeKey.KEY_SEAT_ARRAY.size) rtmManager.addOrUpdateChannelAttributes(
                AttributeKey.KEY_SEAT_ARRAY[position],
                Gson().toJson(seat),
                callback
        )
    }

    fun updateSeatArray(position: Int, value: String?): Boolean {
        val seat = Gson().fromJson(value, Seat::class.java)
        return channelData.updateSeat(position, seat)
    }
}