package io.agora.chatroom.manager

interface ChatRoomEventListener {
    fun onSeatUpdated(position: Int)
    fun onUserGivingGift(userId: String?)
    fun onMessageAdded(position: Int)
    fun onMemberListUpdated(userId: String?)
    fun onUserStatusChanged(userId: String?, muted: Boolean?)
    fun onAudioMixingStateChanged(isPlaying: Boolean)
    fun onAudioVolumeIndication(userId: String?, volume: Int)
}