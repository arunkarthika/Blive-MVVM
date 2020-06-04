package com.blive.View.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blive.Models.ListUsers
import com.blive.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_users.view.*

class AdapterListUsers(var users: ArrayList<ListUsers.listDetails.Details>, var context: Context,
                       var listenerChannel:ListenerChannel) :
    RecyclerView.Adapter<AdapterListUsers.UserViewHolder>() {

    fun updateUsers(newUsers: List<ListUsers.listDetails.Details>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        )

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
Log.i("autolog", "position: " + position);

        holder.tvName.text = users.get(position).profileName

        Glide.with(context)
            .load(users.get(position).profile_pic)
            .into(holder.profileImage)

        holder.ll.setOnClickListener {
            listenerChannel.OnClicked(position, users.get(position))
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.tv_name
        val profileImage = view.iv_image
        val ll = view.ll

    }

    fun setOnClickListener(listenerChannel: ListenerChannel) {
        this.listenerChannel = listenerChannel
    }

    interface ListenerChannel {
        fun OnClicked(Position: Int, user: ListUsers.listDetails.Details?)
    }
}