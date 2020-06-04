package com.blive.View.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.blive.Models.Viewers
import com.blive.R
import com.bumptech.glide.Glide
import java.net.URLDecoder
import java.util.*

class AdapterAudiences(
    private val mContext: Context,
    private val audiences: ArrayList<Viewers>
) : RecyclerView.Adapter<AdapterAudiences.ViewHolder>() {
    private var listenerImage: ListenerImage? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        try {
            val (_, dpEffects, _, _, _, _, base64) = audiences[position]
            val image = URLDecoder.decode(base64, "UTF-8")
            Glide.with(mContext)
                .load(image)
                .into(holder.iv)
            if (!audiences[position].dpEffects.isEmpty()) {
                Glide.with(mContext)
                    .load(dpEffects)
                    .into(holder.ivEffect)
            }
            holder.iv.setOnClickListener { v: View? ->
                if (listenerImage != null) {
                    listenerImage!!.OnClickedAudience(audiences[holder.adapterPosition])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return audiences.size
    }

    inner class ViewHolder  (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var iv: ImageView
        var ivEffect: ImageView
        var ll: LinearLayout

        init {
            iv = itemView.findViewById(R.id.iv)
            ivEffect = itemView.findViewById(R.id.iv_effect)
            ll = itemView.findViewById(R.id.ll)
        }
    }

    fun setOnClickListener(listenerImage: ListenerImage?) {
        this.listenerImage = listenerImage
    }

    interface ListenerImage {
        fun OnClickedAudience(audience: Viewers?)
    }

}