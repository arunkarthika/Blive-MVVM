package com.blive.View.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blive.Models.Gift
import com.blive.R
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import java.util.*

class AdapterGifts(private val mContext: Context, private val gifts: ArrayList<Gift>) :
    RecyclerView.Adapter<AdapterGifts.ViewHolder>() {
    private var listenerGift: ListenerGift? = null
    private var selected = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gift, parent, false)
        return ViewHolder(layout)
    }

    fun onGiftReset() {
        selected = -1
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("autolog", "position: " + position)
        try {
            val (_, _, _, _, icon, _, name, price) = gifts[position]
            try {
                Glide.with(mContext)
                    .load(icon)
                    .into(holder.iv)
            } catch (e: Exception) {
                Crashlytics.logException(e)
            }
            if (selected != -1) {
                if (position == selected) {
                    holder.ll.background = mContext.resources.getDrawable(R.drawable.border_orange)
                } else holder.ll.background = mContext.resources.getDrawable(R.color.gift_trans)
            }
            if (price != "0") {
                holder.tvPrice.text = price
            } else {
                holder.tvPrice.visibility = View.GONE
                Glide.with(mContext)
                    .load(R.drawable.free_icon)
                    .into(holder.ivHeart)
            }
            holder.tvName.text = name
            holder.ll.setOnClickListener { v: View? ->
                if (listenerGift != null) {
                    if (holder.adapterPosition >= 0) {
                        holder.ll.setBackgroundColor(
                            mContext.resources.getColor(R.color.colorAccent)
                        )
                        selected = holder.adapterPosition
                        listenerGift!!.OnClicked(gifts[holder.adapterPosition])
                        notifyDataSetChanged()
                    }
                }
            }
        } catch (e: Exception) {
            Log.i("autolog", "e: " + e.toString());
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return gifts.size
    }

     class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var iv: ImageView
        var ivHeart: ImageView
        var tvName: TextView
        var tvPrice: TextView
        var ll: LinearLayout
        var llGiftlayout: LinearLayout

        init {
            iv = itemView.findViewById(R.id.iv)
            llGiftlayout = itemView.findViewById(R.id.ll_giftlayoutadpater)
            ivHeart = itemView.findViewById(R.id.iv_default_heart)
            tvName = itemView.findViewById(R.id.tv_name)
            tvPrice = itemView.findViewById(R.id.tv_price)
            ll = itemView.findViewById(R.id.ll)
        }
    }

    fun setOnClickListener(listenerGift: ListenerGift?) {
        this.listenerGift = listenerGift
    }

    interface ListenerGift {
        fun OnClicked(gift: Gift)
    }

}