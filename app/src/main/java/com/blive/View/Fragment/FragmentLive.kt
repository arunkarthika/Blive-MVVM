package com.blive.View.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blive.Models.ActiveUsers
import com.blive.Presenter.LiveuserPresenter
import com.blive.R
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Adapter.AdapterLiveUsers
import com.blive.View.MVP.LiveUserMvp

class FragmentLive : BaseFragment(), LiveUserMvp.MainView, AdapterLiveUsers.ListenerChannel {
    lateinit var recycle: RecyclerView
    lateinit var rvAdapter: AdapterLiveUsers
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var liveuserPresenter: LiveuserPresenter
    lateinit var data: ArrayList<ActiveUsers.Datauser>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live, container, false)
    }

    override fun initUIandEvent() {

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycle = view.findViewById(R.id.recycle)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)
        data = ArrayList()
        rvAdapter = AdapterLiveUsers(data, activity!!, this)
        swipeRefreshLayout.setColorScheme(
            R.color.blue,
            R.color.green, R.color.yellow, R.color.green_greyish
        );
        liveuserPresenter = LiveuserPresenter(this, activity!!)
        liveuserPresenter.loadData("live_user", "1", "all", "all", "India", "Coimbatore", "20")
        swipeRefreshLayout.isRefreshing = true

        val linearLayoutManager = GridLayoutManager(activity, 2)
        recycle.setLayoutManager(linearLayoutManager)


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        swipeRefreshLayout.setOnRefreshListener {
            liveuserPresenter.loadData("live_user", "1", "all", "all", "India", "Coimbatore", "20")
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun deInitUIandEvent() {

    }

    override fun setError(error: String?) {
        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(activity!!, "Error In Response", Toast.LENGTH_LONG).show()

    }

    override fun settoken(token: String?) {
        swipeRefreshLayout.isRefreshing = false

        Log.i("autolog", "bearer: " + SessionUser.user.activation_code + ":" + SessionUser.token)
    }

    override fun setAdapterData(dataapi: ArrayList<ActiveUsers.Datauser>) {
        swipeRefreshLayout.isRefreshing = false
        data.clear()
        data.addAll(dataapi)
        recycle.setAdapter(rvAdapter)
    }


    override fun OnClicked(Position: Int, user: ActiveUsers.Datauser?) {
        var useridlist: ArrayList<String> = ArrayList()
        useridlist.clear()
        for (i in data.indices) {
            Log.i("autolog", "data.size: " + data.size);
            Log.i("autolog", "i: " + i);
            useridlist.add(data.get(i).userid)
        }
        var bundle = bundleOf("user_id" to user!!.userid, "list" to useridlist,"Position" to Position)
        FragmentHome.navController!!.navigate(R.id.action_homeFragment_to_goLive_Fragment, bundle)
        Toast.makeText(activity!!, user!!.name, Toast.LENGTH_SHORT).show()
    }

}
