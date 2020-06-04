package com.blive.View.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blive.Models.ActiveUsers
import com.blive.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.userslist_live.view.*


class AdapterLiveUsers(var users: ArrayList<ActiveUsers.Datauser>,var context:Context,var listenerChannel:ListenerChannel  ) :
    RecyclerView.Adapter<AdapterLiveUsers.UserViewHolder>() {

    fun updateUsers(newUsers: List<ActiveUsers.Datauser>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.userslist_live, parent, false)
        )

    override fun getItemCount() = users.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.userName.text=users.get(position).name
        holder.viewers.text=users.get(position).viewerCount

        Glide.with(context)
            .load(users.get(position).profilePic)
            .into(holder.userimage)
        holder.rl.setOnClickListener{
            listenerChannel.OnClicked(position,users.get(position))
        }

    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         val userName = view.tv_title
         val viewers = view.tv_viewers
         val userimage = view.iv_user
         val rl = view.rl

    }

    fun setOnClickListener(listenerChannel: ListenerChannel) {
        this.listenerChannel = listenerChannel
    }

    interface ListenerChannel {
        fun OnClicked(Position: Int, user: ActiveUsers.Datauser?)
    }
}
