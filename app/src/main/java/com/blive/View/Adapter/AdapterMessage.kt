package com.blive.View.Adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blive.Models.Gift
import com.blive.R
import com.blive.View.Adapter.AdapterMessage.MyViewHolder
import com.blive.model.MessageBean
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import java.util.*

class AdapterMessage(
    private val mContext: Context,
    private val messageBeanList: List<MessageBean>
) : RecyclerView.Adapter<MyViewHolder>() {
    private var listenerMessage: ListenerMessage? = null
    private var giftsList: ArrayList<Gift>? = null
    private val ids: ArrayList<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            setupView(holder, position)
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.logException(e)
        }
    }

    fun setGifts(giftsList: ArrayList<Gift>?) {
        this.giftsList = giftsList
    }

    override fun getItemCount(): Int {
        return messageBeanList.size
    }

    private fun setupView(holder: MyViewHolder, position: Int) {
        val bean = messageBeanList[position]
        Log.i("autolog", "message: " + bean.message)

        bean.isBeSelf = false
        if (bean.isWarning) {
            holder.tvWarning.visibility = View.VISIBLE
            holder.rl.visibility = View.GONE
        } else {
            holder.tvWarning.visibility = View.GONE
            holder.rl.visibility = View.VISIBLE
            val id = bean.message.substring(0, 6)
            val level = bean.message.substring(6, 8)
            try {
                val lvl = Integer.valueOf(level)
                if (position > ids.size){
                    ids.add(id)
                }
                Log.i("autolog", "lvl: $lvl")
                if (lvl <= 5) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_sandal) else if (lvl <= 10) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_blue
                ) else if (lvl <= 15) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_light_red) else if (lvl <= 20) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_green
                ) else if (lvl <= 25) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_brown) else if (lvl <= 30) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_pink
                ) else if (lvl <= 35) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_deep_blue) else if (lvl <= 40) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_black
                ) else if (lvl <= 45) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_green_yellow) else if (lvl <= 50) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_red
                ) else if (lvl <= 55) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_green_greyish) else if (lvl <= 60) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_yellow
                ) else if (lvl <= 65) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_teal) else if (lvl <= 70) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_blue
                ) else if (lvl <= 75) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_voilet) else if (lvl <= 80) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_pink_dark
                ) else if (lvl <= 85) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_lime) else if (lvl <= 90) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_green
                ) else if (lvl == 98) holder.textViewOtherName.setBackgroundResource(R.drawable.shape_circle_pkbroad) else if (lvl == 99) holder.textViewOtherName.setBackgroundResource(
                    R.drawable.shape_circle_pkguest
                )
                holder.tvLevel.text = level
                if (lvl == 98) holder.tvLevel.text = "★" else if (lvl == 99) holder.tvLevel.text =
                    "★"
                val message = " " + bean.message.substring(8, bean.message.length)
                val spannable: Spannable = SpannableString(message)
                if (bean.isGift) {
                    spannable.setSpan(
                        ForegroundColorSpan(mContext.resources.getColor(R.color.colorAccent)),
                        0,
                        message.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    setGiftImage(holder, message)
                } else holder.ivGift.visibility = View.GONE
                holder.textViewOtherMsg.setText(spannable, TextView.BufferType.SPANNABLE)
                holder.textViewOtherMsg.setTextColor(mContext.resources.getColor(R.color.white))
                holder.textViewOtherMsg.setOnClickListener { v: View? ->
                    Log.e(CrashlyticsCore.TAG, "setupView: " + " Clicked ")
                    try {
                        if (listenerMessage != null) {
                            Log.e(CrashlyticsCore.TAG, "setupView: " + " Clicked  1")
                            listenerMessage!!.onMessageClicked(
                                messageBeanList[holder.adapterPosition].account,
                                ids[holder.adapterPosition - 1]
                            )
                        }
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                    }
                }
                if (bean.background != 0) {
                    holder.textViewOtherName.setBackgroundResource(bean.background)
                }
            } catch (e: Exception) {
                Log.i("autolog", "e: $e")
                holder.rl.visibility = View.GONE
            }
        }
    }

    private fun setGiftImage(holder: MyViewHolder, message: String) {
        try {
            if (giftsList!!.size > 0) {
                holder.ivGift.visibility = View.VISIBLE
                for (i in giftsList!!.indices) {
                    if (message.contains(giftsList!![i].name!!)) {
                        Glide.with(mContext).load(giftsList!![i].icon).into(holder.ivGift)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(CrashlyticsCore.TAG, "setGiftImage: $e")
            Crashlytics.logException(e)
        }
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val textViewOtherName: TextView
         val tvLevel: TextView
         val tvWarning: TextView
         val textViewOtherMsg: TextView
         val rl: RelativeLayout
        val ivGift: ImageView

        init {
            textViewOtherName = itemView.findViewById(R.id.tv_bg)
            textViewOtherMsg = itemView.findViewById(R.id.item_msg_l)
            tvWarning = itemView.findViewById(R.id.tv_warning)
            rl = itemView.findViewById(R.id.rl_message)
            ivGift = itemView.findViewById(R.id.iv_gift)
            tvLevel = itemView.findViewById(R.id.tv_level)
        }
    }

    fun setOnClickListener(listenerMessage: ListenerMessage?) {
        this.listenerMessage = listenerMessage
    }

    interface ListenerMessage {
        fun onMessageClicked(name: String?, id: String?)
    }

    init {
        ids = ArrayList()
    }
}