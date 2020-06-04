package com.blive.View.Fragment


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.blive.Models.ListUsers
import com.blive.Presenter.ListUserPresenter
import com.blive.R
import com.blive.View.Activity.Testactivity
import com.blive.View.Adapter.ViewpagerAdapter
import com.blive.View.MVP.ListViewMVP
import com.blive.View.Util.AppConstants
import com.blive.View.Util.Session.SessionUser
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FragmentHome : Fragment(), ViewpagerAdapter.BtnClickListener, ListViewMVP.MainView {
    var list: ArrayList<String> = ArrayList()
    lateinit var listUserPresenter: ListUserPresenter

    //    var navController: NavController? = null
    companion object {
        var navController: NavController? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview: View = inflater.inflate(R.layout.fragment_home, container, false)
        Testactivity.bottomshow()
        val pager: ViewPager2 = rootview.findViewById(R.id.pager)
        val tabLayout: TabLayout = rootview.findViewById(R.id.tab_layout)

        list.clear()
        list.add("Home")
        list.add("Solo")
        list.add("Pk")
        list.add("18+")
        list.add("Group")
        list.add("Videos")

        pager.adapter =
            ViewpagerAdapter(list, activity!!, this)
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = list.get(position)
            /* if (position == 3) {
                 tab.icon = resources.getDrawable(R.drawable.splash_logo)
             }*/
            pager.setCurrentItem(tab.position, true)
        }.attach()
        return rootview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        listUserPresenter = ListUserPresenter(this, activity!!)
        listUserPresenter.userList(
            SessionUser.user.user_id,
            AppConstants.getGift,
            AppConstants.defaultpage,
            AppConstants.defaultlength
        )

    }

    override fun onBtnClick(position: Int) {
        Log.i("autolog", "position: " + position)
//        navController!!.navigate(R.id.action_homeFragment_to_fragmentLive)
    }

    override fun setError(error: String?) {

    }

    override fun setAdapterData(data: ListUsers.listDetails) {
        Log.i("autolog", "giftList: " +data)

        if (data.giftList != null) {
            SessionUser.saveGiftList(data.giftList.gift_list.all)
            Log.i("autolog", "giftList: " + SessionUser.getGiftList().size)
        }

    }

}
