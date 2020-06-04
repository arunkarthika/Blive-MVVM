package com.blive.View.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.R
import com.blive.databinding.FragmentGoLiveBinding
import com.blive.databinding.FragmentWebViewBinding
import kotlinx.android.synthetic.main.fragment_live_room.*


class FragmentWebView : BaseFragment() {
    lateinit var navController: NavController
    var webView: WebView? = null
    var header: LinearLayout? = null
    var baseUrl: String? = null
    var title: String? = null
    var webUrl: String? = null
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        header=view.findViewById(R.id.ll_header)

        if (arguments != null) {
            baseUrl = arguments?.getString("baseUrl")!!
            title = arguments?.getString("title")!!
        }

        if (title.equals("Toppers")){

        }

        navController = Navigation.findNavController(view)
        webView = view.findViewById(R.id.wv)

        header!!.visibility=View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.navigate(R.id.action_fragmentWebView2_to_fragmentProfile)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        webLoad(baseUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webLoad(Url: String?) {
        try {
            val webSettings = webView!!.settings
            webView!!.settings.javaScriptEnabled = true
            webView!!.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            webView!!.settings.setAppCacheEnabled(true)
            webView!!.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            webSettings.domStorageEnabled = true
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            webSettings.useWideViewPort = true
            webView!!.webChromeClient = MyChromeClient()
            webView!!.webViewClient = MyWebViewClient()
            webView!!.loadUrl(Url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class MyChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    override fun initUIandEvent() {

    }

    override fun deInitUIandEvent() {

    }

}
