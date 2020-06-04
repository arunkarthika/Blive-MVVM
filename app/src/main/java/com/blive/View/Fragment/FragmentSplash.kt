package com.blive.View.Fragment


import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.R
import com.bumptech.glide.Glide

/**
 * A simple [Fragment] subclass.
 */
class FragmentSplash : Fragment() {
    var navController: NavController?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        val Splash_Iv: ImageView = view.findViewById(R.id.splash_iv)
        navController= Navigation.findNavController(view)

        Glide
            .with(activity!!)
            .load(R.drawable.splash_logo)
            .into(Splash_Iv)
        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            navController!!.navigate(R.id.action_splash_Fragment_to_skip_Fragment)

        }, 3000)

    }
}
