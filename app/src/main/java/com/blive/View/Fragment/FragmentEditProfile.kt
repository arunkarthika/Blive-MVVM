package com.blive.View.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.R
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils

import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class FragmentEditProfile : Fragment() {

    lateinit var utils: Utils
    lateinit var image1: String
    lateinit var image2: String
    lateinit var image3: String
    lateinit var navController: NavController
    val arrayList = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(activity)
        navController = Navigation.findNavController(view)

        image1 = SessionUser.user.profile_pic
        image2 = SessionUser.user.profile_pic1
        image3 = SessionUser.user.profile_pic2

        arrayList.add(image1)
        arrayList.add(image2)
        arrayList.add(image3)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() { // Handle the back button event
                    navController!!.navigate(R.id.action_fragmentEditProfile_to_fragmentProfile)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

}
