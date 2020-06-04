package com.blive.View.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blive.Models.Viewers
import com.blive.R
import com.blive.View.Util.Session.SessionUser
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import java.net.URLDecoder
import java.util.*

/**
 * Created by sans on 15-10-2018.
 */
class AdapterRequests(
    private val mContext: Context,
    private val users: ArrayList<Viewers>,
    private val role: Int,
    private val type: Int
) : RecyclerView.Adapter<AdapterRequests.ViewHolder>() {
    private var listener: Listener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        try {
            val user = users[position]
            if (type == 0 && role == 0) {
                holder.rlAccept.visibility = View.VISIBLE
                holder.rlReject.visibility = View.VISIBLE
                holder.rlEnd.visibility = View.GONE
            } else {
                holder.rlAccept.visibility = View.GONE
                holder.rlReject.visibility = View.GONE
                holder.rlEnd.visibility = View.GONE
            }
            if (type == 1) {
                if (role == 0 || role == 1) {
                    holder.rlAccept.visibility = View.GONE
                    holder.rlReject.visibility = View.GONE
                    if (user.user_id.equals(
                            SessionUser.user.user_id,
                            ignoreCase = true
                        ) || role == 0
                    ) {
                        holder.rlEnd.visibility = View.VISIBLE
                    } else {
                        holder.rlEnd.visibility = View.GONE
                    }
                } else {
                    holder.rlAccept.visibility = View.GONE
                    holder.rlReject.visibility = View.GONE
                    holder.rlEnd.visibility = View.GONE
                }
            }
            val base64 = user.profile_pic
            val image = URLDecoder.decode(base64, "UTF-8")
            Glide.with(mContext)
                .load(image)
                .error(R.drawable.blivemessage)
                .into(holder.iv)
            holder.tvName.text = user.profileName
            holder.rlAccept.setOnClickListener { v: View? ->
                if (listener != null) {
                    try {
                        listener!!.onAcceptRequest(
                            users[holder.adapterPosition],
                            holder.adapterPosition
                        )
                    } catch (e: Exception) {
                        Log.i("autolog", "e: " + e.message)
                        Crashlytics.logException(e)
                    }
                }
            }
            holder.rlReject.setOnClickListener { v: View? ->
                if (listener != null) {
                    try {
                        listener!!.onRejectRequest(
                            users[holder.adapterPosition],
                            holder.adapterPosition
                        )
                    } catch (e: Exception) {
                        Log.i("autolog", "e: " + e.message)
                        Crashlytics.logException(e)
                    }
                }
            }
            holder.rlEnd.setOnClickListener { v: View? ->
                if (listener != null) {
                    try {
                        listener!!.onEndRequest(
                            users[holder.adapterPosition],
                            holder.adapterPosition
                        )
                    } catch (e: Exception) {
                        Log.i("autolog", "e: " + e.message)
                        Crashlytics.logException(e)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder  (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var iv: ImageView
        var tvName: TextView
        var rlAccept: ImageView
        var rlReject: ImageView
        var rlEnd: ImageView

        init {
            iv = itemView.findViewById(R.id.iv)
            tvName = itemView.findViewById(R.id.tv_name)
            rlAccept = itemView.findViewById(R.id.rl_accept)
            rlReject = itemView.findViewById(R.id.rl_reject)
            rlEnd = itemView.findViewById(R.id.rl_end)
        }
    }

    fun setOnClickListener(listener: Listener?) {
        this.listener = listener
    }

    interface Listener {
        fun onAcceptRequest(user: Viewers?, position: Int)
        fun onRejectRequest(user: Viewers?, position: Int)
        fun onEndRequest(user: Viewers?, position: Int)
    }

}