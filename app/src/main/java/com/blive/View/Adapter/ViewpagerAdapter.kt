package com.blive.View.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blive.View.Fragment.FragmentLive
import com.blive.View.Fragment.FragmentProfile

class ViewpagerAdapter (var liststring: ArrayList<String>, fragmentActivity: FragmentActivity, val btnlistener: BtnClickListener) :FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return liststring.size
    }

    override fun createFragment(position: Int): Fragment {
        mClickListener = btnlistener
        if (mClickListener != null){
            mClickListener?.onBtnClick(position)
    }
        return FragmentLive()
    }
     interface BtnClickListener {
        fun onBtnClick(position: Int)
    }
    companion object {
        var mClickListener: BtnClickListener? = null
    }
}