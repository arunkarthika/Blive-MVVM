package com.blive.View.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.R
import com.blive.View.Util.Agora.RTM.ChatManager
import com.blive.View.Util.Session.SessionLogin
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils


/**
 * A simple [Fragment] subclass.
 */
class FragmentBlocked : Fragment() {

    lateinit var utils: Utils
    lateinit var navController: NavController
    lateinit var btn_exit: Button
    lateinit var tv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blocked, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(activity)
        navController = Navigation.findNavController(view)
        btn_exit = view.findViewById(R.id.btn_exit)
        tv = view.findViewById(R.id.tv)


        btn_exit.setOnClickListener {
            if (SessionLogin.getLoginSession()) {
                SessionLogin.clearLoginSession()
                SessionUser.clearUserSession()
            }
            finishAffinity(activity!!)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (SessionLogin.getLoginSession()) {
            SessionLogin.clearLoginSession()
            SessionUser.clearUserSession()
        }
    }
}
