package com.blive.View.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TablayoutAdapter(var list: ArrayList<String>, fragmentActivity: FragmentActivity,
                       fm: FragmentManager
): FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}