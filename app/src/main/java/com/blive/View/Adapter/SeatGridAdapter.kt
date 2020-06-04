package com.blive.View.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blive.Models.ChannelData
import com.blive.Models.Seat
import com.blive.R
import de.hdodenhof.circleimageview.CircleImageView
import io.agora.chatroom.manager.ChatRoomManager
import io.agora.chatroom.manager.ChatRoomManager.Companion.instance


class SeatGridAdapter(context: Context?) : RecyclerView.Adapter<SeatGridAdapter.ViewHolder>() {
    private var mInflater: LayoutInflater
    private var mListener: OnItemClickListener? = null
    private var mChannelData: ChannelData?


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.layout_item_seat, parent, false)
        return ViewHolder(view)
    }

    fun SeatGridAdapter(context: Context?) {
        mInflater = LayoutInflater.from(context)
        mChannelData = instance(context)!!.channelData
    }

    override fun getItemCount(): Int {
        return mChannelData?.seatArray?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, payloads)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seat = mChannelData!!.seatArray[position]
        if (seat != null) {
            if (seat.isClosed) {
                holder.iv_seat!!.setImageResource(R.mipmap.ic_blive)
                holder.iv_mute!!.visibility = View.GONE
            } else {
                val userId = seat.userId
                if (mChannelData!!.isUserOnline(userId)) {
                    holder.iv_seat!!.setImageResource(mChannelData!!.getMemberAvatar(userId))
                    holder.iv_mute!!.visibility = if (mChannelData!!.isUserMuted(userId)) View.VISIBLE else View.GONE
                } else {
                    holder.iv_seat!!.setImageResource(R.mipmap.ic_blive)
                    holder.iv_mute!!.visibility = View.GONE
                }
            }
        } else {
            holder.iv_seat!!.setImageResource(R.mipmap.ic_blive)
            holder.iv_mute!!.visibility = View.GONE
        }
        holder.iv_seat!!.setOnClickListener { view: View? -> if (mListener != null) mListener!!.onItemClick(holder.view, position, seat) }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    fun notifyItemChanged(userId: String?, animated: Boolean) {
        val index = mChannelData!!.indexOfSeatArray(userId)
        if (index >= 0) {
            if (animated) notifyItemChanged(index, true) else notifyItemChanged(index)
        }
    }

    inner class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        var iv_seat: CircleImageView? = null
        var iv_mute: ImageView? = null

        init {
            iv_seat = itemView.findViewById(R.id.iv_seat)
            iv_mute = itemView.findViewById(R.id.iv_mute)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int, seat: Seat?)
    }

    init {
        mInflater = LayoutInflater.from(context)
        mChannelData = ChatRoomManager.instance(context)!!.channelData
    }
}