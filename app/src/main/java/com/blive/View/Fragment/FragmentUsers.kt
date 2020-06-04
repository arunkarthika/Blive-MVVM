package com.blive.View.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blive.Models.ListUsers
import com.blive.Presenter.ListUserPresenter
import com.blive.R
import com.blive.View.Adapter.AdapterListUsers
import com.blive.View.MVP.ListViewMVP
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import java.util.ArrayList


class FragmentUsers : BaseFragment(), AdapterListUsers.ListenerChannel, ListViewMVP.MainView {

    lateinit var navController: NavController
    lateinit var utils: Utils
    lateinit var tvNousers: TextView
    lateinit var rvUsers: RecyclerView
    lateinit var ListUserPresenter: ListUserPresenter
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var data: ArrayList<ListUsers.listDetails.Details>
    lateinit var rvListUser: RecyclerView
    var userId: String? = null
    var actionType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun initUIandEvent() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(activity)
        navController = Navigation.findNavController(view)
        data = ArrayList()

        if (arguments != null) {
            userId = arguments?.getString("userId")!!
            actionType = arguments?.getString("actionType")!!
        }

        rvListUser = view.findViewById(R.id.rv_users)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        tvNousers = view.findViewById(R.id.tv_no_users)
        rvUsers = view.findViewById(R.id.rv_users)
        val layoutManagers = LinearLayoutManager(activity!!)
        layoutManagers.orientation = RecyclerView.VERTICAL
        rvListUser.setLayoutManager(layoutManagers)
        rvListUser.visibility == View.VISIBLE

        swipeRefreshLayout.setColorScheme(
            R.color.blue,
            R.color.green, R.color.yellow, R.color.green_greyish
        );

        ListUserPresenter = ListUserPresenter(this, activity!!)

        callUsersAPI(userId, actionType, "1", "20")

        swipeRefreshLayout.isRefreshing = true

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    navController!!.navigate(R.id.action_fragmentUsers_to_fragmentProfile)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun deInitUIandEvent() {

    }

    private fun callUsersAPI(userid: String?, action: String?, page: String?, lenght: String?) {
        Log.i("autolog", "action: " + action)
        Log.i("autolog", "userid: " + userid)
        ListUserPresenter.userList(userid!!, action!!, page!!, lenght!!)
    }

    override fun OnClicked(Position: Int, user: ListUsers.listDetails.Details?) {
        Toast.makeText(activity!!, "Success In Response", Toast.LENGTH_LONG).show()
    }

    override fun setError(error: String?) {
        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(activity!!, "Error In Response", Toast.LENGTH_LONG).show()
    }

    override fun setAdapterData(datausers: ListUsers.listDetails) {
        swipeRefreshLayout.isRefreshing = false
        data.clear()
        if (datausers.blocked!=null){
            data.addAll(datausers.blocked)
        }else if (datausers.fans!=null){
            data.addAll(datausers.fans)
        }else if (datausers.followers!=null){
            data.addAll(datausers.followers)
        }else if (datausers.friends!=null){
            data.addAll(datausers.friends)
        }
        Log.i("autolog", "data.: " + data.size)
        val rvAdapter = AdapterListUsers(data, activity!!, this)
        rvListUser.setAdapter(rvAdapter)
    }
}
