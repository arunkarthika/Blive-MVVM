package com.blive.View.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.blive.R
import com.blive.View.Util.Agora.AGEventHandler
import com.blive.View.Util.AppConstants
import com.blive.View.Util.Session.SessionUser
import com.blive.databinding.ActivityMainBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.agora.rtc.IRtcEngineEventHandler.LastmileProbeResult
import org.slf4j.LoggerFactory

class Testactivity : BaseActivity(), AGEventHandler {
    private var binding: ActivityMainBinding? = null
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.getRoot()
        setContentView(view)
        bar = findViewById(R.id.bar)
        fab = findViewById(R.id.fab)

        navController = findNavController(R.id.my_nav_host_fragment);
        binding!!.bar.setOnClickListener {
            Toast.makeText(this, "mBar", Toast.LENGTH_LONG).show()
        }
        binding!!.home.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }
        binding!!.pk.setOnClickListener {
            Toast.makeText(this, "mPk", Toast.LENGTH_LONG).show()
        }
        binding!!.winners.setOnClickListener {
            val userid = SessionUser.user.user_id
            val url = AppConstants.getTopperList
            val base = url + userid
            var bundle = bundleOf("baseUrl" to base, "title" to "Toppers")
            navController.navigate(R.id.fragmentWebView2, bundle)
        }
        binding!!.profile.setOnClickListener {
            navController.navigate(R.id.fragmentProfile)
        }
        binding!!.fab.setOnClickListener {
            navController!!.navigate(R.id.goLive_Fragment)
        }
    }

    override fun initUIandEvent() {

    }

    override fun deInitUIandEvent() {

    }

    override fun onDestroy() {
        super.onDestroy()
        log.debug("onDestroy")
    }

    override fun onFirstRemoteVideoDecoded(
        uid: Int,
        width: Int,
        height: Int,
        elapsed: Int
    ) {
    }

    override fun onJoinChannelSuccess(
        channel: String,
        uid: Int,
        elapsed: Int
    ) {
    }

    override fun onUserOffline(uid: Int, reason: Int) {}
    override fun onUserJoined(uid: Int, elapsed: Int) {}
    override fun onLastmileQuality(quality: Int) {}
    override fun onExtraCallback(type: Int, vararg data: Any) {}
    override fun onLastmileProbeResult(result: LastmileProbeResult) {}

    companion object {
        var bar: BottomAppBar? = null
        var fab: FloatingActionButton? = null
        private val log = LoggerFactory.getLogger(Testactivity::class.java)
        fun bottomhide() {
            bar?.setTitleMargin(0, 0, 0, 0)
            bar!!.visibility = View.GONE
            fab!!.visibility = View.GONE
        }

        fun bottomshow() {
            bar?.setTitleMargin(0, 0, 0, 60)
            bar!!.visibility = View.VISIBLE
            fab!!.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        utils.showToast("Tab once to exit")
    }
}