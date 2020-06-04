package com.blive.View.Util.Agora

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceView
import com.blive.BuildConfig
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions
import java.io.File

class WorkerThread(private val mContext: Context) : Thread() {

    private class WorkerThreadHandler internal constructor(private var mWorkerThread: WorkerThread?) :
        Handler() {
        fun release() {
            mWorkerThread = null
        }

        override fun handleMessage(msg: Message) {
            if (mWorkerThread == null) {
                Log.e("SwitchLive", "handler is already released! " + msg.what)
                return
            }
            when (msg.what) {
                ACTION_WORKER_THREAD_QUIT -> mWorkerThread!!.exit()
                ACTION_WORKER_JOIN_CHANNEL -> {
                    val data = msg.obj as Array<String>
                    mWorkerThread!!.joinChannel(data[0], msg.arg1)
                }
                ACTION_WORKER_LEAVE_CHANNEL -> {
                    val channel = msg.obj as String
                    mWorkerThread!!.leaveChannel(channel)
                }
                ACTION_WORKER_CONFIG_ENGINE -> {
                    val configData = msg.obj as Array<Any>
                    mWorkerThread!!.configEngine(
                        configData[0] as Int,
                        configData[1] as VideoDimensions
                    )
                }
                ACTION_WORKER_PREVIEW -> {
                    val previewData = msg.obj as Array<Any>
                    mWorkerThread!!.preview(
                        previewData[0] as Boolean,
                        previewData[1] as SurfaceView,
                        previewData[2] as Int
                    )
                }
                ACTION_WORKER_SWITCH_CHANNEL -> {
                    val data1 = msg.obj as Array<String>
                    mWorkerThread!!.switchChannel(data1[0], msg.arg1)
                }
            }
        }

    }

    private var mWorkerHandler: WorkerThreadHandler? = null
    private var mReady = false
    fun waitForReady() {
        while (!mReady) {
            try {
                sleep(20)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            Log.i(
                "SwitchLive",
                "wait for " + WorkerThread::class.java.simpleName
            )
        }
    }

    override fun run() {
        Log.i("SwitchLive", "start to run")
        Looper.prepare()
        mWorkerHandler = WorkerThreadHandler(this)
        ensureRtcEngineReadyLock()
        mReady = true
        // enter thread looper
        Looper.loop()
    }

    var rtcEngine: RtcEngine? = null
        private set

    fun joinChannel(channel: String, uid: Int) {
        if (currentThread() !== this) {
            Log.i(
                "SwitchLive",
                "joinChannel() - worker thread asynchronously $channel $uid"
            )
            val envelop = Message()
            envelop.what =
                ACTION_WORKER_JOIN_CHANNEL
            envelop.obj = arrayOf(channel)
            envelop.arg1 = uid
            mWorkerHandler!!.sendMessage(envelop)
            return
        }
        ensureRtcEngineReadyLock()
        rtcEngine!!.joinChannel(null, channel, "OpenLive", uid)
        engineConfig.mChannel = channel
        Log.i("SwitchLive", "joinChannel " + channel + " " + (uid and 0xFFFFFFFFL.toInt()))
    }

    fun leaveChannel(channel: String) {
        if (currentThread() !== this) {
            Log.i(
                "SwitchLive",
                "leaveChannel() - worker thread asynchronously $channel"
            )
            val envelop = Message()
            envelop.what =
                ACTION_WORKER_LEAVE_CHANNEL
            envelop.obj = channel
            mWorkerHandler!!.sendMessage(envelop)
            return
        }
        if (rtcEngine != null) {
            rtcEngine!!.leaveChannel()
        }
        val clientRole = engineConfig.mClientRole
        engineConfig.reset()
        Log.i("SwitchLive", "leaveChannel $channel $clientRole")
    }

    fun switchChannel(channel: String, uid: Int) {
        if (currentThread() !== this) {
            Log.i(
                "SwitchLive",
                "switchChannel() - worker thread asynchronously $channel $uid"
            )
            val envelop = Message()
            envelop.what =
                ACTION_WORKER_SWITCH_CHANNEL
            envelop.obj = arrayOf(channel)
            envelop.arg1 = uid
            mWorkerHandler!!.sendMessage(envelop)
            return
        }
        ensureRtcEngineReadyLock()
        rtcEngine!!.switchChannel(null, channel)
        engineConfig.mChannel = channel
        Log.i("SwitchLive", "switchChannel " + channel + " " + (uid and 0xFFFFFFFFL.toInt()))
    }

    val engineConfig: EngineConfig

    private val mEngineEventHandler: MyEngineEventHandler
    fun configEngine(cRole: Int, videoDimension: VideoDimensions) {
        if (currentThread() !== this) {
            Log.i(
                "SwitchLive",
                "configEngine() - worker thread asynchronously $cRole $videoDimension"
            )
            val envelop = Message()
            envelop.what =
                ACTION_WORKER_CONFIG_ENGINE
            envelop.obj = arrayOf(cRole, videoDimension)
            mWorkerHandler!!.sendMessage(envelop)
            return
        }
        ensureRtcEngineReadyLock()
        engineConfig.mClientRole = cRole
        engineConfig.mVideoDimension = videoDimension
        //      mRtcEngine.setVideoProfile(mEngineConfig.mVideoProfile, true); // Earlier than 2.3.0
        rtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                videoDimension,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            )
        )
        rtcEngine!!.setClientRole(cRole)
        Log.i(
            "SwitchLive",
            "configEngine " + cRole + " " + engineConfig.mVideoDimension
        )
    }

    fun preview(start: Boolean, view: SurfaceView, uid: Int) {
        if (currentThread() !== this) {
            Log.i(
                "SwitchLive",
                "preview() - worker thread asynchronously " + start + " " + view + " " + (uid and 0XFFFFFFFFL.toInt())
            )
            val envelop = Message()
            envelop.what = ACTION_WORKER_PREVIEW
            envelop.obj = arrayOf(start, view, uid)
            mWorkerHandler!!.sendMessage(envelop)
            return
        }
        ensureRtcEngineReadyLock()
        if (start) {
            rtcEngine!!.setupLocalVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            rtcEngine!!.startPreview()
        } else {
            rtcEngine!!.stopPreview()
        }
    }

    private fun ensureRtcEngineReadyLock(): RtcEngine? {
        if (rtcEngine == null) {
            val appId = BuildConfig.private_app_id
            if (TextUtils.isEmpty(appId)) {
                throw RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/")
            }
            try {
                rtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandler.mRtcEventHandler)
            } catch (e: Exception) {
                Log.i("SwitchLive", Log.getStackTraceString(e))
                throw RuntimeException(
                    "NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(
                        e
                    )
                )
            }
            rtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            rtcEngine!!.enableVideo()
            val path = Environment.getExternalStorageDirectory()
                .toString() + File.separator + mContext.packageName + "/log/agora-rtc.log"
            rtcEngine!!.setLogFile(path)
            // Warning: only enable dual stream mode if there will be more than one broadcaster in the channel
            rtcEngine!!.enableDualStreamMode(true)
            Log.e("liqilin", "log path: $path")
        }
        return rtcEngine
    }

    fun eventHandler(): MyEngineEventHandler {
        return mEngineEventHandler
    }

    /**
     * call this method to exit
     * should ONLY call this method when this thread is running
     */
    fun exit() {
        if (currentThread() !== this) {
            Log.i("SwitchLive", "exit() - exit app thread asynchronously")
            mWorkerHandler!!.sendEmptyMessage(ACTION_WORKER_THREAD_QUIT)
            return
        }
        mReady = false
        Log.i("SwitchLive", "exit() > start")
        // exit thread looper
        Looper.myLooper()!!.quit()
        mWorkerHandler!!.release()
        Log.i("SwitchLive", "exit() > end")
    }

    companion object {
        private const val ACTION_WORKER_THREAD_QUIT = 0X1010 // quit this thread
        private const val ACTION_WORKER_JOIN_CHANNEL = 0X2010
        private const val ACTION_WORKER_LEAVE_CHANNEL = 0X2011
        private const val ACTION_WORKER_CONFIG_ENGINE = 0X2012
        private const val ACTION_WORKER_SWITCH_CHANNEL = 0x2013
        private const val ACTION_WORKER_PREVIEW = 0X2014
        fun getDeviceID(context: Context): String { // XXX according to the API docs, this value may change after factory reset
// use Android id as device id
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }

    init {
        engineConfig = EngineConfig()
        val pref =
            PreferenceManager.getDefaultSharedPreferences(mContext)
        engineConfig.mUid = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_UID, 0)
        mEngineEventHandler = MyEngineEventHandler()
    }
}