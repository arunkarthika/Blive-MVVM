package com.blive.View.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.R
import com.blive.View.Activity.Testactivity
import com.blive.View.Util.AppConstants
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import com.bumptech.glide.Glide
import io.agora.rtc.Constants
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : Fragment() {

    lateinit var ivSettings: ImageView
    lateinit var ivProfPic: ImageView
    lateinit var llFriends: LinearLayout
    lateinit var llFans: LinearLayout
    lateinit var llFollowers: LinearLayout
    lateinit var llOfficialMessage: LinearLayout
    lateinit var llMessage: LinearLayout
    lateinit var llLevel: LinearLayout
    lateinit var llWallet: LinearLayout
    lateinit var llRewards: LinearLayout
    lateinit var llMyAssets: LinearLayout
    lateinit var llMyProgress: LinearLayout
    lateinit var llTopFans: LinearLayout
    lateinit var llCheckIn: LinearLayout
    lateinit var ivEditProfile: ImageView
    lateinit var tvUserName: TextView
    lateinit var tvBLiveId: TextView
    lateinit var tvGold: TextView
    lateinit var tvDiamond: TextView
    lateinit var tvFriends: TextView
    lateinit var tvFans: TextView
    lateinit var tvFollowers: TextView
    lateinit var utils: Utils
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Testactivity.bottomhide()
        utils = Utils(activity)
        navController = Navigation.findNavController(view)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() { // Handle the back button event
                    navController!!.navigate(R.id.action_fragmentProfile_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        ivProfPic = view.findViewById(R.id.iv_profile)
        tvUserName = view.findViewById(R.id.tv_userName)
        tvBLiveId = view.findViewById(R.id.tv_bLiveId)
        tvGold = view.findViewById(R.id.tv_gold)
        tvDiamond = view.findViewById(R.id.tv_diamond)
        tvFriends = view.findViewById(R.id.tv_friends)
        tvFans = view.findViewById(R.id.tv_fans)
        tvFollowers = view.findViewById(R.id.tv_followers)
        ivEditProfile = view.findViewById(R.id.iv_editProfile)
        ivSettings = view.findViewById(R.id.iv_settings)
        llFans = view.findViewById(R.id.ll_fans)
        llFriends = view.findViewById(R.id.ll_friends)
        llFollowers = view.findViewById(R.id.ll_followers)
        llOfficialMessage = view.findViewById(R.id.ll_officialMessage)
        llMessage = view.findViewById(R.id.ll_message)
        llLevel = view.findViewById(R.id.ll_level)
        llWallet = view.findViewById(R.id.ll_Wallet)
        llRewards = view.findViewById(R.id.ll_rewards)
        llMyAssets = view.findViewById(R.id.ll_myAssets)
        llMyProgress = view.findViewById(R.id.ll_myProgress)
        llTopFans = view.findViewById(R.id.ll_topFans)
        llCheckIn = view.findViewById(R.id.ll_checkIn)

        tvUserName.setText(SessionUser.user.name)
        tvBLiveId.setText("BLive ID " + SessionUser.user.reference_user_id)
        tvGold.setText(SessionUser.user.over_all_gold)
        tvDiamond.setText(SessionUser.user.over_all_gold)
        tvFollowers.setText(SessionUser.user.followers)
        tvFans.setText(SessionUser.user.followers)
        tvFriends.setText(SessionUser.user.friends)

        Glide.with(activity!!)
            .load(SessionUser.user.profile_pic)
            .into(ivProfPic)

        ivSettings.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentSettings)
            }
        }

        ivEditProfile.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentEditProfile)
            }
        }

        llFriends.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                var bundle = bundleOf("userId" to userid,"actionType" to "friends","title" to "Friend's")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentUsers,bundle)
            }
        }

        llFans.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val action = "followers"
                val base = userid + action
                var bundle = bundleOf("userId" to userid,"actionType" to "fans","title" to "Fan's")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentUsers,bundle)
            }
        }

        llFollowers.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val action = "following"
                val base = userid + action
                var bundle = bundleOf("userId" to userid,"actionType" to "followers","title" to "Following")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentUsers,bundle)
            }
        }

        llOfficialMessage.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
               /* navController!!.navigate(R.id.action_fragmentProfile_to_fragmentAudioCall)*/
                utils.showToast("Coming Soon!!")
            }
        }

        llMessage.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
               utils.showToast("Coming Soon!!")
            }
        }

        llLevel.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val url = AppConstants.level
                val base = url + userid
                var bundle = bundleOf("baseUrl" to base,"title" to "Level")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentWebView2,bundle)
            }
        }

        llWallet.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val url = AppConstants.wallet
                val base = url + userid
                var bundle = bundleOf("baseUrl" to base,"title" to "Wallet")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentWebView2,bundle)
            }
        }

        llRewards.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val url = AppConstants.rewards
                val base = url + userid
                var bundle = bundleOf("baseUrl" to base,"title" to "Rewards")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentWebView2,bundle)
            }
        }

        llMyAssets.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val url = AppConstants.assets
                val base = url + userid
                var bundle = bundleOf("baseUrl" to base,"title" to "My Assets")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentWebView2,bundle)
            }
        }

        llMyProgress.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val url = AppConstants.progress
                val base = url + userid
                var bundle = bundleOf("baseUrl" to base,"title" to "My Progress")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentWebView2,bundle)
            }
        }

        llTopFans.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                utils.showToast("Coming Soon!!")
            }
        }

        llCheckIn.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                val userid = SessionUser.user.user_id
                val url = AppConstants.DailyCheckin
                val base = url + userid
                var bundle = bundleOf("baseUrl" to base,"title" to "Check IN")
                navController!!.navigate(R.id.action_fragmentProfile_to_fragmentWebView2,bundle)
            }
        }
    }
}
