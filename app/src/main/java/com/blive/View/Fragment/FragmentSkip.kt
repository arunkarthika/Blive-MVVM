package com.blive.View.Fragment


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.R
import com.blive.View.Util.AppConstants

/**
 * A simple [Fragment] subclass.
 */
class FragmentSkip : Fragment() {
    lateinit var timer: CountDownTimer
    lateinit var webView: WebView
    var navController: NavController? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootview: View = inflater.inflate(R.layout.fragment_skip, container, false)
        return rootview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        navController = Navigation.findNavController(view)

        webView = view.findViewById(R.id.webview_splah)
        webView.loadUrl(AppConstants.index1)
        webView.visibility == View.VISIBLE

        /*webView.loadUrl(AppConstants.WebViewURL)*/
        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i("autolog", "millisUntilFinished: " + millisUntilFinished / 1000);
            }

            override fun onFinish() {
                timer.cancel()
                navController!!.navigate(R.id.action_skip_Fragment_to_homeFragment)
            }
        }
        timer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        Log.i("autolog", "onDestroyView: ");
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        Log.i("autolog", "onDestroy: ");
    }
}
