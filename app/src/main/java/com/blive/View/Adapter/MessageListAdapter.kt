package com.blive.View.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.blive.Models.ChannelData
import com.blive.Models.Message
import com.blive.R
import io.agora.chatroom.manager.ChatRoomManager


class MessageListAdapter(context: Context?) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private val mChannelData: ChannelData?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.layout_item_message, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mChannelData?.messageList?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = mChannelData!!.messageList[position]
        val userId = message.sendId
        holder.iv_avatar!!.setImageResource(mChannelData.getMemberAvatar(userId))
        when (message.messageType) {
            Message.MESSAGE_TYPE_TEXT -> {
                holder.tv_message!!.visibility = View.VISIBLE
                val member = mChannelData.getMember(userId)
                holder.tv_message!!.text = String.format("%s：%s", if (member != null) member.name else userId, message.content)
            }
            Message.MESSAGE_TYPE_IMAGE -> {
            }
        }
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        var iv_avatar: ImageView? = null
        var tv_message: TextView? = null
        var iv_image: ImageView? = null

        init {
            tv_message = itemView.findViewById(R.id.tv_message)
            iv_avatar = itemView.findViewById(R.id.iv_avatar)
            iv_image = itemView.findViewById(R.id.iv_image)
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
        mChannelData = ChatRoomManager.instance(context)!!.channelData
    }
}