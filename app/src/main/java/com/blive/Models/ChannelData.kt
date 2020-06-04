package com.blive.Models

import android.text.TextUtils
import com.blive.R
import com.blive.View.Util.MemberUtil

import java.util.*

class ChannelData {
    fun release() {
        mAnchorId = null
        Arrays.fill(seatArray, 0, seatArray.size, null)
        mUserStatus.clear()
        mMemberList.clear()
        mMessageList.clear()
    }

    // AnchorId
    private var mAnchorId: String? = null

    fun setAnchorId(anchorId: String?): Boolean {
        if (TextUtils.equals(anchorId, mAnchorId)) return false
        mAnchorId = anchorId
        return true
    }

    fun hasAnchor(): Boolean {
        return !TextUtils.isEmpty(mAnchorId)
    }

    fun isAnchor(userId: String?): Boolean {
        return TextUtils.equals(userId, mAnchorId)
    }

    val isAnchorMyself: Boolean
        get() = isAnchor(Constant.sUserId.toString())

    // SeatArray
    val seatArray = arrayOfNulls<Seat>(MAX_SEAT_NUM)

    fun updateSeat(position: Int, seat: Seat?): Boolean {
        val temp = seatArray[position]
        if (seat === temp) return false
        if (seat != null && temp != null && TextUtils.equals(seat.userId, temp.userId)) return false
        seatArray[position] = seat
        return true
    }

    fun indexOfSeatArray(userId: String?): Int {
        for (i in seatArray.indices) {
            val seat = seatArray[i] ?: continue
            if (TextUtils.equals(userId, seat.userId)) return i
        }
        return -1
    }

    fun firstIndexOfEmptySeat(): Int {
        for (i in seatArray.indices) {
            val seat = seatArray[i] ?: return i
            if (!isUserOnline(seat.userId)) return i
        }
        return -1
    }

    // UserStatus
    private val mUserStatus: MutableMap<String, Boolean> = HashMap()

    fun isUserOnline(userId: String?): Boolean {
        val muted = mUserStatus[userId]
        return muted != null
    }

    fun isUserMuted(userId: String?): Boolean {
        val muted = mUserStatus[userId]
        return muted ?: false
    }

    fun addOrUpdateUserStatus(uid: Int, muted: Boolean) {
        mUserStatus[uid.toString()] = muted
    }

    fun removeUserStatus(uid: Int) {
        mUserStatus.remove(uid.toString())
    }

    // MemberList
    private val mMemberList: MutableList<Member> = ArrayList()

    val memberList: List<Member>
        get() = mMemberList

    fun addOrUpdateMember(member: Member) {
        val index = mMemberList.indexOf(member)
        if (index >= 0) {
            mMemberList[index].update(member)
        } else {
            mMemberList.add(member)
        }
    }

    fun removeMember(userId: String?) {
        val member = Member(userId)
        mMemberList.remove(member)
    }

    fun getMember(userId: String?): Member? {
        for (member in mMemberList) {
            if (TextUtils.equals(userId, member.userId)) {
                return member
            }
        }
        return null
    }

    fun getMemberAvatar(userId: String?): Int {
        val member = getMember(userId) ?: return R.mipmap.ic_blive
        return MemberUtil.getAvatarResId(member.avatarIndex)
    }

    fun indexOfMemberList(userId: String?): Int {
        return mMemberList.indexOf(Member(userId))
    }

    // MessageList
    private val mMessageList: MutableList<Message> = ArrayList()

    val messageList: List<Message>
        get() = mMessageList

    fun addMessage(message: Message): Int {
        mMessageList.add(message)
        return mMessageList.size - 1
    }

    companion object {
        const val MAX_SEAT_NUM = 10
    }
}