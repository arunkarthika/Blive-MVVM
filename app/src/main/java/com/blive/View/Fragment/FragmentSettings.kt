package com.blive_mvvm


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.R
import com.blive.View.Activity.Testactivity
import com.blive.View.Util.AppConstants
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils


class FragmentSettings : Fragment() {

    lateinit var phoneLogo: ImageView
    lateinit var facebookLogo: ImageView
    lateinit var googleLogo: ImageView
    lateinit var twitterLogo: ImageView
    lateinit var instgramLogo: ImageView
    lateinit var llPrivacy: LinearLayout
    lateinit var llBlockedList: LinearLayout
    lateinit var llConnectedAcc: LinearLayout
    lateinit var llPin: LinearLayout
    lateinit var llSuggestions: LinearLayout
    lateinit var llAboutUs: LinearLayout
    lateinit var llCache: LinearLayout
    lateinit var llLogout: LinearLayout
    lateinit var utils: Utils
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(activity)
        navController = Navigation.findNavController(view)

        phoneLogo = view.findViewById(R.id.phone_logo)
        facebookLogo =view.findViewById(R.id.facebook_logo)
        googleLogo = view.findViewById(R.id.google_logo)
        twitterLogo = view.findViewById(R.id.twitter_logo)
        instgramLogo = view.findViewById(R.id.instgram_logo)
        llPrivacy = view.findViewById(R.id.ll_privacy)
        llBlockedList = view.findViewById(R.id.ll_blockedlist)
        llConnectedAcc = view.findViewById(R.id.ll_connectedAccounts)
        llPin = view.findViewById(R.id.ll_pin)
        llSuggestions = view.findViewById(R.id.ll_suggestions)
        llAboutUs = view.findViewById(R.id.ll_about_us)
        llCache = view.findViewById(R.id.ll_cache)
        llLogout = view.findViewById(R.id.ll_logout)
        val connected_account: String = SessionUser.user.login_domain

        when (connected_account) {
            "google" -> googleLogo.visibility = View.VISIBLE
            "facebook" -> facebookLogo.visibility = View.VISIBLE
            "instagram" -> instgramLogo.visibility = View.VISIBLE
            "twitter" -> twitterLogo.visibility = View.VISIBLE
            "mobile" -> phoneLogo.visibility = View.VISIBLE
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController!!.navigate(R.id.action_fragmentSettings_to_fragmentProfile)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        llPrivacy.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                navController!!.navigate(R.id.action_fragmentSettings_to_fragmentPrivacy)
            }
        }

        llBlockedList.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val action = "followers"
                val base = userid + action
                var bundle = bundleOf("userId" to base,"actionType" to "fans","title" to "Fan's")
                navController!!.navigate(R.id.action_fragmentSettings_to_fragmentUsers,bundle)
            }
        }

        llPin.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                navController!!.navigate(R.id.action_fragmentSettings_to_fragmentPin)
            }
        }

        llSuggestions.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val url = AppConstants.suggestions
                val base = url + userid
                var bundle = bundleOf("baseUrl" to base,"title" to "Suggestion")
                navController!!.navigate(R.id.action_fragmentSettings_to_fragmentWebView2,bundle)
            }
        }

        llAboutUs.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val url = AppConstants.aboutUs
                var bundle = bundleOf("baseUrl" to url,"title" to "About Us")
                navController!!.navigate(R.id.action_fragmentSettings_to_fragmentWebView2,bundle)
            }
        }

        llCache.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
               utils.deleteCache(activity!!)
                utils.showToast("Cache Cleared!")
            }
        }

        llLogout.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                activity!!.finishAffinity()
                SessionUser.clearLoginSession()
                val intent = Intent(activity!!, Testactivity::class.java)
                startActivity(intent)
            }
        }
    }
}
