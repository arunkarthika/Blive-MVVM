package com.blive.View.Fragment


import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blive.BliveApplication
import com.blive.Models.*
import com.blive.Presenter.GoLivePresenter
import com.blive.Presenter.ListUserPresenter
import com.blive.R
import com.blive.View.Activity.Testactivity
import com.blive.View.Adapter.AdapterAudiences
import com.blive.View.Adapter.AdapterGifts
import com.blive.View.Adapter.AdapterMessage
import com.blive.View.Adapter.AdapterRequests
import com.blive.View.MVP.GoLiveMvp
import com.blive.View.MVP.ListViewMVP
import com.blive.View.Util.Agora.AGEventHandler
import com.blive.View.Util.Agora.ConstantApp
import com.blive.View.Util.Agora.RTM.ChatHandler
import com.blive.View.Util.Agora.RTM.ChatManager
import com.blive.View.Util.AppConstants
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import com.blive.databinding.FragmentGoLiveBinding
import com.blive.model.MessageBean
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.crashlytics.android.Crashlytics
import com.facebook.FacebookSdk.getApplicationContext
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.ChannelMediaInfo
import io.agora.rtc.video.ChannelMediaRelayConfiguration
import io.agora.rtc.video.VideoCanvas
import io.agora.rtm.*
import kotlinx.android.synthetic.main.fragment_go_live_.*


class FragmentGoLive : BaseFragment(),
    AGEventHandler, GoLiveMvp.MainView, AdapterMessage.ListenerMessage,
    AdapterAudiences.ListenerImage, AdapterRequests.Listener, ListViewMVP.MainView,
    AdapterGifts.ListenerGift {
    private var positionchange: Int = 0
    lateinit var goLivePresenter: GoLivePresenter
    lateinit var cvRequests: CardView
    lateinit var rvRequests: RecyclerView
    lateinit var btn_join: Button
    lateinit var utils: Utils
    private var audienceModel: AudienceModel? = null
    private var broadCaster: BroadCaster? = null
    var userid: String = "0"
    var channelname: String = "0"
    var position: Int = 0
    var role: Int = 2
    var videorequestsent: Boolean = false
    var cRole: Int = 1
    private var mRtmClient: RtmClient? = null
    private var mChatManager: ChatManager? = null
    private var mRtmChannel: RtmChannel? = null
    private var mChatHandler: ChatHandler? = null
    private var adapter: AdapterMessage? = null
    private var audiencesadapter: AdapterAudiences? = null
    val messageBeanList: MutableList<MessageBean> = ArrayList()
    val audiencelist: ArrayList<Viewers> = ArrayList()
    val guestlist: ArrayList<Viewers> = ArrayList()
    val waitinglist: ArrayList<Viewers> = ArrayList()
    var giftdata: ArrayList<Gift> = ArrayList()
    val remoteInvitationmodel: ArrayList<RemoteInvitationModel> = ArrayList()
    lateinit var useridlist: ArrayList<String>
    private var _binding: FragmentGoLiveBinding? = null
    private val binding get() = _binding!!
    lateinit var uidlist: ArrayList<Int>
    lateinit var channelMediaInfos: ArrayList<ChannelMediaInfo>
    lateinit var mUserItem: UserItem
    var userItemList: ArrayList<UserItem> = ArrayList()
    lateinit var callMgr: RtmCallManager
    lateinit var dialog: Dialog
    lateinit var adapterRequests: AdapterRequests
    lateinit var adapterGifts: AdapterGifts
    lateinit var listUserPresenter: ListUserPresenter
    val giftHandler = Handler()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGoLiveBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun initUIandEvent() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Testactivity.bottomhide()
        viewsvisible(8)
        cvRequests = view.findViewById(R.id.cvRequests)
        rvRequests = view.findViewById(R.id.rvRequests)
        btn_join = view.findViewById(R.id.btn_join)
        goLivePresenter = GoLivePresenter(this, activity!!)
        listUserPresenter = ListUserPresenter(this, activity!!)

        utils = Utils(activity)
        useridlist = ArrayList()
        uidlist = ArrayList()
        channelMediaInfos = ArrayList()
        Log.i("autolog", "userid: " + userid)
        val layoutManagers = LinearLayoutManager(activity!!)
        val layoutManagers1 = LinearLayoutManager(activity!!)
        layoutManagers.orientation = RecyclerView.VERTICAL
        binding.rvmessages.setLayoutManager(layoutManagers)
        layoutManagers1.orientation = RecyclerView.HORIZONTAL
        binding.rvImages1.setLayoutManager(layoutManagers1)
        val layoutManager =
            GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.rvGift.setLayoutManager(layoutManager)
        adapter = AdapterMessage(activity!!, messageBeanList)
        rvmessages.setAdapter(adapter)
        audiencesadapter = AdapterAudiences(activity!!, audiencelist)
        binding.rvImages1.setAdapter(audiencesadapter)
        adapter!!.setOnClickListener(this)
        audiencesadapter!!.setOnClickListener(this)
        dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        adapterRequests = AdapterRequests(activity!!, waitinglist, role, 0)
        try {
            if (SessionUser.getGiftList().size==0){
                utils.showToast("No More Gifts Found")
            }else{
                giftdata = SessionUser.getGiftList()
            }
        }catch (e:Exception){

            listUserPresenter.userList(
                SessionUser.user.user_id,
                AppConstants.getGift,
                AppConstants.defaultpage,
                AppConstants.defaultlength
            )

        }

        adapterGifts = AdapterGifts(activity!!, giftdata)
        adapterRequests.setOnClickListener(this)
        adapterGifts.setOnClickListener(this)
        binding.rvGift.setAdapter(adapterGifts)
        val messageBean = MessageBean(
            SessionUser.user.name,
            resources.getString(R.string.warning),
            true,
            true,
            false
        )
        messageBeanList.add(messageBean)
        adapter!!.notifyItemRangeChanged(messageBeanList.size, 1)
        rvmessages.scrollToPosition(messageBeanList.size - 1)
        setMsgFromRtm("100002", SessionUser.user.name + "stored")
        sendMessageToChannel(SessionUser.user.name + "Has Arrived")
        mChatManager = BliveApplication.getInstance().getChatManager()
        mRtmClient = mChatManager!!.getRtmClient()
        mRtmChannel = mChatManager!!.getRtmChannel()
        callMgr = mRtmClient!!.getRtmCallManager()
        callMgr.setEventListener(mRtmCallEventHandler)
        mChatManager!!.logout()
        if (mRtmChannel == null) {
            mChatManager!!.createChannel(SessionUser.user.username)
            mRtmChannel = mChatManager!!.rtmChannel
        } else {
            Log.i("autolog", "mRtmChannel: " + mRtmChannel!!.id)
        }
        mChatHandler = object : ChatHandler {
            override fun onLoginSuccess() {
                Log.e("RTm", "onLoginSuccess: ")
            }

            override fun onLoginFailed(errorInfo: ErrorInfo) {
                Log.e("RTm", "onLoginFailed: " + errorInfo.errorDescription)
                mChatManager!!.logout()
                mChatManager!!.doLogin(channelname)
            }

            override fun onChannelJoinSuccess() {
                Log.e("RTm", "onRTMChannelJoinSuccess: ")
            }

            override fun onChannelJoinFailed(errorCode: ErrorInfo?) {
                Log.e("RTm", "onRTMChannelJoinFailed: ")
                /*mChatManager.doLogin(channelname);*/Log.i(
                    "autolog-fromlive_bro",
                    "mChannelName: $channelname"
                )
            }

            override fun onMessageReceived(
                message: RtmMessage,
                fromMember: RtmChannelMember
            ) {
                Log.e(
                    "RTm",
                    "onRTMMessageReceived: account = " + fromMember.userId + " msg = " + message.text
                )
                activity!!.runOnUiThread { onMessageReceive(message, fromMember) }
            }

            override fun onMemberJoined(rtmChannelMember: RtmChannelMember) {
                audienceApiCall(
                    userid,
                    AppConstants.getAudience,
                    AppConstants.defaultpage,
                    AppConstants.defaultlength
                )
                activity!!.runOnUiThread {
                    sendMessageToChannel("has Joined")
                }
            }

            override fun onMemberLeft(rtmChannelMember: RtmChannelMember?) {
                audienceApiCall(
                    userid,
                    AppConstants.getAudience,
                    AppConstants.defaultpage,
                    AppConstants.defaultlength
                )
                activity!!.runOnUiThread {
                }
            }
        }
        mChatManager!!.addChantHandler(mChatHandler)
        useridlist.clear()
        if (arguments != null) {
            userid = arguments?.getString("user_id")!!
            useridlist = arguments?.getStringArrayList("list")!!
            position = arguments?.getInt("Position")!!
            Log.i("autolog", "useruidlist: " + useridlist.size);
        }
        Log.i("autolog", "userid: " + userid);
        event().addEventHandler(this)

        if (!userid.equals("0")) {

            goLivePresenter.getaudience(
                AppConstants.addAudience,
                AppConstants.defaultpage,
                AppConstants.defaultlength,
                userid
            )
            cRole = Constants.CLIENT_ROLE_AUDIENCE
            doConfigEngine(Constants.CLIENT_ROLE_AUDIENCE)
            binding.btnGolive.visibility = GONE
        } else {
            role = 0
            userid = SessionUser.user.user_id
            setupaudiencedataown(SessionUser.user)
            cRole = Constants.CLIENT_ROLE_BROADCASTER
            doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER)

//            doRenderRemoteUi(SessionUser.user.id.toInt())
            setupLocalVideo()
        }
        binding.ivPk.setOnClickListener {


            showDialogInGolive()
        }
        binding.switchCamera.setOnClickListener {
            if (cRole == Constants.CLIENT_ROLE_BROADCASTER) {
                worker().rtcEngine!!.switchCamera()
            }
        }
        binding.pkicon.setOnClickListener {

        }
        binding.ivGuestClose.setOnClickListener {
            endGuest()
        }
        binding.ivGift.setOnClickListener {
            binding.llGift.visibility = VISIBLE
        }


        binding.btnGolive.setOnClickListener {
            if (cRole != Constants.CLIENT_ROLE_AUDIENCE) {
                goliveapi("live", "solo", "", "", "")
            }
        }

        binding.btnMsg.setOnClickListener {
            binding.llChat.visibility = VISIBLE

        }
        binding.ivClose.setOnClickListener {
            clickclose()
        }
        binding.userMessageBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text!!.isNotEmpty()) {
                    sendButton.visibility = VISIBLE
                } else {
                    sendButton.visibility = GONE
                }

            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        binding.sendButton.setOnClickListener {
            sendMessageToChannel(userMessageBox.text.toString())
            userMessageBox.text.clear()
            binding.llChat.visibility = View.GONE
        }
        binding.ivVideoMute.setOnClickListener {

        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() { // Handle the back button event
                    if (binding.cvRequests.visibility == VISIBLE) {
                        binding.cvRequests.visibility = View.GONE
                    } else {
                        clickclose()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        binding.parentlayout.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(event)
                return true
            }

            private val gestureDetector =
                GestureDetector(
                    activity!!,
                    object : SimpleOnGestureListener() {
                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            Log.e("swipe", "onSingleTapUp: root")
                            if (binding.llChat.visibility == VISIBLE) {
                                binding.llChat.visibility = View.GONE
                            } else if (binding.cvRequests.visibility == VISIBLE) {
                                binding.cvRequests.visibility = View.GONE
                            } else if (binding.llGift.visibility == VISIBLE) {
                                binding.llGift.visibility = View.GONE
                            }
                            return false
                        }

                        override fun onDoubleTap(e: MotionEvent): Boolean {
//                            doLikeAnimation()
                            return super.onDoubleTap(e)
                        }

                        override fun onFling(
                            e1: MotionEvent,
                            e2: MotionEvent,
                            velocityX: Float,
                            velocityY: Float
                        ): Boolean {
                            when (getSlope(e1.x, e1.y, e2.x, e2.y)) {
                                1 -> {
                                    onSwipeUp()
                                    Log.e("swipe", "onFling: top")
                                    return true
                                }
                                2 -> {
                                    onSwipeLeft()
                                    Log.e("swipe", "onFling: left")
                                    return true
                                }
                                3 -> {
                                    onSwipeDown()
                                    Log.e("swipe", "onFling: down")
                                    return true
                                }
                                4 -> {
                                    onSwipeRight()
                                    Log.e("swipe", "onFling: right")
                                    return true
                                }
                            }
                            return false
                        }
                    })

            fun onSwipeUp() {
                if (useridlist.size != 1 && cRole != Constants.CLIENT_ROLE_BROADCASTER) {


                    positionchange = 1;
                    Log.i("autolog", "position: " + position)
                    if (position == useridlist.size - 1) {
                        goLivePresenter.getaudience("addAudience", "1", "10", useridlist.get(0))
                        position = 0
                    } else {
                        goLivePresenter.getaudience(
                            "addAudience",
                            "1",
                            "10",
                            useridlist.get(position + 1)
                        )
                        position = position + 1
                    }
                    Log.i("autolog", "position: " + position)
                } else {
                    utils.showToast("There is no more Broad")
                }
            }

            fun onSwipeDown() {
                if (useridlist.size != 1 && cRole != Constants.CLIENT_ROLE_BROADCASTER) {
                    positionchange = 1;

                    Log.i("autolog", "position: " + position);
                    if (position == 0) {
                        goLivePresenter.getaudience(
                            "addAudience",
                            "1",
                            "10",
                            useridlist.get(useridlist.size - 1)
                        )
                        position = useridlist.size - 1
                    } else {
                        goLivePresenter.getaudience(
                            "addAudience",
                            "1",
                            "10",
                            useridlist.get(position - 1)
                        )
                        position = position - 1
                    }
                    Log.i("autolog", "position: " + position)
                } else {
                    utils.showToast("There is no more Broad")
                }
            }

            fun onSwipeLeft() {

            }

            fun onSwipeRight() {
                /*  rlLive.setVisibility(View.VISIBLE)
                  fabMenu.setVisibility(View.VISIBLE)*/
                /*ivImng.setVisibility(View.VISIBLE);*/
            }

            private fun getSlope(
                x1: Float,
                y1: Float,
                x2: Float,
                y2: Float
            ): Int {
                val angle = Math.toDegrees(
                    Math.atan2(
                        y1 - y2.toDouble(),
                        x2 - x1.toDouble()
                    )
                )
                if (angle > 45 && angle <= 135) // top
                    return 1
                if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) // left
                    return 2
                if (angle < -45 && angle >= -135) // down
                    return 3
                return if (angle > -45 && angle <= 45) 4 else 0
            }
        })


    }

    private fun switchAudienceToGuest() {
        role = 1
        goLivePresenter.addguest(
            AppConstants.addguest,
            AppConstants.defaultpage,
            AppConstants.defaultlength,
            userid
        )
        cRole = Constants.CLIENT_ROLE_BROADCASTER
        doConfigEngine(Constants.CLIENT_ROLE_BROADCASTER)
        val surfaceV = RtcEngine.CreateRendererView(getApplicationContext())
        worker().preview(true, surfaceV, SessionUser.user.id.toInt())
        surfaceV.setZOrderMediaOverlay(true)
        userItemList.add(UserItem(SessionUser.user.id.toInt(), surfaceV))
        setupremotevideo()
    }

    private fun clickclose() {
        worker().leaveChannel(channelname)
        FragmentHome.navController!!.navigate(R.id.action_goLive_Fragment_to_fragmentLive2)
        if (cRole == Constants.CLIENT_ROLE_BROADCASTER) {
            goliveapi("offline", "solo", "0", "0", "0")
        }
    }

    private fun onMessageReceive(message: RtmMessage, fromMember: RtmChannelMember) {
        Log.i("autolog", "messagereceived: " + fromMember.channelId)
        Log.i("autolog", "messagereceived: " + message.text.toString())
        activity!!.runOnUiThread {
            setMsgFromRtm(fromMember.userId, message.text.toString())
        }
    }

    fun setMsgFromRtm(account: String, msg: String) {
        activity!!.runOnUiThread {
            val message = MessageBean(
                SessionUser.user.username,
                SessionUser.user.user_id + SessionUser.user.level + SessionUser.user.username + msg,
                true,
                false,
                false
            )
            messageBeanList.add(message)
            adapter!!.notifyItemRangeChanged(messageBeanList.size, 1)
            rvmessages.scrollToPosition(messageBeanList.size - 1)
        }

    }

    private fun goliveapi(
        action: String,
        broadcast_type: String,
        broadcasting_time: String,
        idle_time: String,
        actual_broadcasting_time: String
    ) {
        worker().joinChannel(SessionUser.user.username, SessionUser.user.id.toInt())

        goLivePresenter.golive(
            action,
            broadcast_type,
            broadcasting_time,
            idle_time,
            actual_broadcasting_time
        )
    }

    override fun deInitUIandEvent() {
        event().removeEventHandler(this)
    }

    private fun setupLocalVideo() {
//        binding.parentlayout.addView(surfaceView)
//        Log.i("autolog", "joinChannel: " + SessionUser.user.username)
//        worker().preview(true, surfaceView, SessionUser.user.id.toInt())
//        mUserItem = UserItem(SessionUser.user.id.toInt(), surfaceView)
//        userItemList.add(0, mUserItem)
//        uidlist.add(SessionUser.user.id.toInt())
//        binding.parentlayout.setVisibility(View.VISIBLE)

        val surfaceV = RtcEngine.CreateRendererView(getApplicationContext())
        mUserItem = UserItem(SessionUser.user.id.toInt(), surfaceV)
        rtcEngine().setupLocalVideo(
            VideoCanvas(
                surfaceV,
                VideoCanvas.RENDER_MODE_HIDDEN,
                SessionUser.user.id.toInt()
            )
        )
        worker().preview(true, surfaceV, SessionUser.user.id.toInt())
        surfaceV.setZOrderOnTop(false)
        surfaceV.setZOrderMediaOverlay(false)
        userItemList.add(0, mUserItem)
        setupremotevideo()
    }

    fun setupremotevideo() {
        activity!!.runOnUiThread {
            Log.i("autolog", "userItemList: " + userItemList.size)
            if (userItemList.size == 1) {
                binding.parentlayout.removeAllViews()
                binding.parentlayout.addView(userItemList.get(0).surfaceView)
                if (binding.guestone.getChildCount() != 0) {
                    binding.guestone.removeAllViews()
                }
                if (binding.guesttwo.getChildCount() != 0) {
                    binding.guesttwo.removeAllViews()
                }
            } else if (userItemList.size == 2) {

                audienceApiCall(
                    userid,
                    AppConstants.getGuest,
                    AppConstants.defaultpage,
                    AppConstants.defaultlength
                )



                if (binding.parentlayout.getChildCount() != 0) {
                    binding.parentlayout.removeAllViews()
                }
                if (binding.guestone.getChildCount() != 0) {
                    binding.guestone.removeAllViews()
                }
                if (binding.guesttwo.getChildCount() != 0) {
                    binding.guesttwo.removeAllViews()
                }
                Log.i(
                    "autolog",
                    "userItemList: " + userItemList.get(0).uid + " seond" + userItemList.get(1).uid
                )
                Log.i("autolog", "userItemList: " + SessionUser.user.id)
                binding.parentlayout.addView(userItemList.get(0).surfaceView)
                binding.guestone.addView(userItemList.get(1).surfaceView)
            } else if (userItemList.size == 3) {
                binding.parentlayout.removeAllViews()
                binding.parentlayout.addView(userItemList.get(0).surfaceView)
                binding.guestone.removeAllViews()
                binding.guestone.addView(userItemList.get(1).surfaceView)
                binding.guesttwo.removeAllViews()
                binding.guesttwo.addView(userItemList.get(2).surfaceView)
            } else {
                switchChannelReset()
            }


        }

    }


    override fun onUserOffline(uid: Int, reason: Int) {
        doRemoveRemoteUi(uid)
        Log.i("autolog", "uid: " + uid)

    }

    override fun onLastmileProbeResult(result: IRtcEngineEventHandler.LastmileProbeResult?) {
        Log.i("autolog", "result: " + result)
    }


    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        Log.i("autolog", "channel: " + channel)

    }

    override fun onLastmileQuality(quality: Int) {
        Log.i("autolog", "quality: " + quality)

    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        Log.i("autologonUserJoined", "uid: " + uid)
        activity!!.runOnUiThread {
            doRenderRemoteUi(uid)
        }


    }

    override fun onExtraCallback(type: Int, vararg data: Any?) {
        Log.i("autolog", "type: " + type + data.get(6))

    }


    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        Log.i("autolog", "Int: " + uid)

    }

    override fun setError(error: String?) {
        binding.btnGolive.visibility = VISIBLE
        utils.showToast(error)

    }

    override fun setAdapterData(data: ListUsers.listDetails) {
        Log.i("autolog", "data: " + data)
        if (data.audience.viewers_list != null) {
            audiencelist.clear()
            audiencelist.addAll(data.audience.viewers_list)
            audiencesadapter!!.notifyDataSetChanged()
        } else if (data.guest != null) {
            guestlist.clear()
            guestlist.addAll(data.guest.guest_list)
        }else if (data.giftList != null) {
            SessionUser.saveGiftList(data.giftList.gift_list.all)
            Log.i("autolog", "giftList: " + SessionUser.getGiftList().size)
        }

    }

    override fun success(status: String?, message: String?) {
        utils.showToast(message)
        binding.btnGolive.visibility = View.GONE
        viewsvisible(0)
        mChatManager!!.doLogin(SessionUser.user.username)
        mChatManager!!.createChannel(channelname)
    }

    override fun getprofilesuccess(
        status: String?,
        message: String?,
        broadProfileBody: BroadProfileBody
    ) {
        /*  broadProfileModel = broadProfileBody
          Log.i("autolog", "broadProfileModel: " + broadProfileModel!!)
          channelname = broadProfileModel!!.username
          Log.i("autolog", "channelname: " + channelname)
          if (positionchange == 0) {
              worker().joinChannel(channelname, SessionUser.user.id.toInt())
          } else {
              switchChannelReset()
              worker().switchChannel(channelname, SessionUser.user.id.toInt())
          }

          mChatManager!!.doLogin(channelname)
          mChatManager!!.createChannel(channelname)
          mRtmChannel = mChatManager!!.rtmChannel*/


    }

    override fun getaudiencesuccess(
        status: String?,
        message: String?,
        audienceModelfromres: AudienceModel
    ) {
        viewsvisible(0)
        audienceModel = audienceModelfromres
        broadCaster = audienceModelfromres.body.broadCastList
        Log.i("autolog", "broadProfileModel: " + audienceModel)
        joinRtcAndRtm(audienceModel!!)
        setupaudiencedata(audienceModel!!.body.broadCastList)
        audiencelist.clear()
        audiencelist.addAll(audienceModel!!.body.viewers_list)
        audiencesadapter!!.notifyDataSetChanged()
    }

    override fun addguestsuccess(status: String?, message: String?, guestResponse: GuestResponse) {
        utils.showToast(message)
        guestlist.clear()
        guestlist.addAll(guestResponse.body.guest_list)
        Log.i("autolog", "guestlist: " + guestlist.size)
        Log.i("autolog", "guestlist: " + guestlist)

    }

    private fun joinRtcAndRtm(audienceModel: AudienceModel) {
        channelname = audienceModel!!.body.broadCastList!!.username
        Log.i("autolog", "channelname: " + channelname)
        if (positionchange == 0) {
            worker().joinChannel(channelname, SessionUser.user.id.toInt())
        } else {
            switchChannelReset()
            worker().switchChannel(channelname, SessionUser.user.id.toInt())
        }
        mChatManager!!.doLogin(channelname)
        mChatManager!!.createChannel(channelname)
        mRtmChannel = mChatManager!!.rtmChannel

    }

    private fun switchChannelReset() {
        binding.parentlayout.removeAllViews()
        binding.parentlayout.setBackgroundDrawable(activity!!.getDrawable(R.drawable.blivemessage))

    }

    private fun setupaudiencedata(broaddata: BroadCaster) {
        clearlist()

        Glide.with(activity!!)
            .load(broaddata!!.profile_pic)
            .into(binding.ivUser)
        binding.roomName.text = broaddata.name
        binding.tvCount.text = broaddata.fans
        binding.tvReceived.text = broaddata.over_all_gold
//        binding.tvFreeGiftCount.text=broaddata


    }

    private fun clearlist() {
        userItemList.clear()
        waitinglist.clear()
        guestlist.clear()
        audiencelist.clear()
    }

    private fun setupaudiencedataown(user: User) {
        Glide.with(activity!!)
            .load(user.profile_pic)
            .into(binding.ivUser)
        binding.roomName.text = user.name
        binding.tvCount.text = "100"
        binding.tvReceived.text = user.over_all_gold
//        binding.tvFreeGiftCount.text=broadProfileModel


    }

    private fun doConfigEngine(cRole: Int) {
        val pref =
            PreferenceManager.getDefaultSharedPreferences(BliveApplication.getInstance())
        var prefIndex = pref.getInt(
            ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX,
            ConstantApp.DEFAULT_PROFILE_IDX
        )
        if (prefIndex > ConstantApp.VIDEO_DIMENSIONS.size - 1) {
            prefIndex = ConstantApp.DEFAULT_PROFILE_IDX
        }
        val dimension = ConstantApp.VIDEO_DIMENSIONS[prefIndex]
        worker().configEngine(cRole, dimension)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("autolog", "onDestroy: ")
        if (cRole == Constants.CLIENT_ROLE_BROADCASTER) {
//            worker().preview(false, surfaceView, SessionUser.user.id.toInt())
            // cross channel STOP
            worker().rtcEngine!!.stopChannelMediaRelay()

        }
        worker().leaveChannel(channelname)
        mChatManager!!.leaveChannel()
        mChatManager!!.removeChatHandler(mChatHandler)
        if (mRtmChannel != null) {
            Log.d("rtm", "onDestroyrtm: ")
            mRtmChannel!!.release()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("autolog", "onPause: ")
        if (cRole == Constants.CLIENT_ROLE_BROADCASTER) {
            worker().rtcEngine!!.muteLocalVideoStream(true)
//            worker().preview(false, surfaceView, SessionUser.user.id.toInt())
        }
    }

    override fun onResume() {
        super.onResume()
        worker().rtcEngine!!.muteLocalVideoStream(false)
//        worker().preview(true, surfaceView, SessionUser.user.id.toInt())
    }

    override fun onMessageClicked(name: String?, id: String?) {

    }

    fun sendMessageToChannel(msg: String) {
        activity!!.runOnUiThread {
            val message = MessageBean(
                SessionUser.user.username,
                SessionUser.user.user_id + SessionUser.user.level + SessionUser.user.username + msg,
                true,
                false,
                false
            )
            Log.i("autolog", "message: " + message.message)
            Log.i("autolog", "message: " + SessionUser.user.user_id)

            messageBeanList.add(message)
            adapter!!.notifyItemRangeChanged(messageBeanList.size, 1)
            rvmessages.scrollToPosition(messageBeanList.size - 1)
            sendChannelMessage(SessionUser.user.user_id + SessionUser.user.level + SessionUser.user.name + msg)
        }

    }

    private fun sendChannelMessage(msg: String) {
        Log.i("autolog", "msg: $msg")
        try { // step 1: create a message
            mRtmClient = mChatManager!!.rtmClient
            mRtmChannel = mChatManager!!.rtmChannel
            val message = mRtmClient!!.createMessage()
            message.text = msg
            Log.i("autolog", "mRtmClient: " + mRtmChannel!!.id)
            // step 2: send message to channel
            mRtmChannel!!.sendMessage(message, object : ResultCallback<Void?> {
                override fun onSuccess(aVoid: Void?) {
                    Log.i("autolog", "message: " + message.text)
                    Log.e("CHannelmsg", "onMessageSendSuccess: ")
                }

                override fun onFailure(errorInfo: ErrorInfo) { // refer to RtmStatusCode.ChannelMessageState for the message state
                    Log.e("CHannelmsg", "onMessageSendFailure: ")
                    val errorCode = errorInfo.errorCode
                    Log.i("autolog", "errorCode: $errorCode")
                    activity!!.runOnUiThread(Runnable {
                        when (errorCode) {
                            RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT, RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE -> {
                            }
                        }
                    })
                }
            })
        } catch (e: Exception) {
            Log.i("autolog", "e: " + e.localizedMessage)
            Crashlytics.logException(e)
        }
    }

    override fun OnClickedAudience(audience: Viewers?) {
        Log.i("autolog", "audience: " + audience)


    }


    fun crosschannel(channelName: String): ChannelMediaRelayConfiguration? {
        Log.i("autolog", "channelName: $channelName")
        val configuration = ChannelMediaRelayConfiguration()
        val tempSrcInfo = ChannelMediaInfo(null, null, 0)
        configuration.setSrcChannelInfo(tempSrcInfo)
        channelMediaInfos.clear()
        for (i in 0..0) {
            val tempDestToken: String? = null
            val tempDestInfo = ChannelMediaInfo(channelName, tempDestToken, 12345)
            channelMediaInfos.add(tempDestInfo)
            configuration.setDestChannelInfo(tempDestInfo.channelName, tempDestInfo)
            if (configuration == null) {
                utils.showToast("dest channel should not be null")
            }
            worker().rtcEngine!!.startChannelMediaRelay(configuration)
        }
        return configuration
    }

    private fun doRenderRemoteUi(uid: Int) {
        activity!!.runOnUiThread(Runnable {
            if (activity!!.isFinishing()) {
                return@Runnable
            }

            if (isExitUid(uid)) {
                return@Runnable
            }
            val surfaceV = RtcEngine.CreateRendererView(getApplicationContext())
//            surfaceV.setZOrderMediaOverlay(true)
            userItemList.add(UserItem(uid, surfaceV))
            Log.i("autolog", "userItemList: " + userItemList.size)
//            rtcEngine().setupRemoteVideo(
//                VideoCanvas(
//                    surfaceV,
//                    VideoCanvas.RENDER_MODE_HIDDEN,
//                    uid
//                )
//            )


            if (config().mUid == uid) {
                rtcEngine().setupLocalVideo(
                    VideoCanvas(
                        surfaceV,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        uid
                    )
                )
            } else {
                rtcEngine().setupRemoteVideo(

                    VideoCanvas(
                        surfaceV,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        uid
                    )
                )
            }
            setupremotevideo()
        })
    }

    private fun isExitUid(uid: Int): Boolean {
        for (i in userItemList.indices) {
            if (userItemList[i].uid == uid) {
                return true
            }
        }
        return false
    }

    private fun requestRemoteStreamType(uid: Int, streamtype: Int) {
        Handler().postDelayed({
            rtcEngine().setRemoteVideoStreamType(uid, streamtype) //Constants.VIDEO_STREAM_LOW
        }, 500)
    }

    private fun doRemoveRemoteUi(uid: Int) {
        for (i in userItemList.indices) {
            if (userItemList[i].uid == uid) {
                userItemList.removeAt(i)
            }
        }

        activity!!.runOnUiThread(Runnable {
            if (activity!!.isFinishing()) {
                return@Runnable
            }

            setupremotevideo()

        })
    }

    val mRtmCallEventHandler: RtmCallEventListener = object : RtmCallEventListener {
        override fun onLocalInvitationReceivedByPeer(localInvitation: LocalInvitation) {
            activity!!.runOnUiThread {
                Log.i(
                    "autolog",
                    "onLocalInvitationReceivedByPeer: " + localInvitation.channelId + localInvitation.calleeId
                )
                if (localInvitation.content.equals("Has Request To Join Broadcast", true)) {
                    videorequestsent = true
                    for (i in audiencelist.indices) {
                        Log.i(
                            "autolog",
                            "remoteInvitation: " + audiencelist.get(i).user_id
                        )
                        if (localInvitation.channelId.equals(
                                audiencelist.get(i).user_id,
                                true
                            )
                        ) {
                            waitinglist.add(audiencelist.get(i))
                            callAdapterRequests(waitinglist)
                        }
                    }

                }
            }
        }

        override fun onLocalInvitationAccepted(localInvitation: LocalInvitation, response: String) {
            if (localInvitation.content.equals("Has Request To Join Broadcast", true)) {
                switchAudienceToGuest()
                for (i in waitinglist.indices) {
                    Log.i("autolog", "remoteInvitation: " + waitinglist.get(i).user_id)
                    if (localInvitation.channelId.equals(waitinglist.get(i).user_id, true)) {
                        waitinglist.removeAt(i)
                        callAdapterRequests(waitinglist)
                    }
                }

            }
        }

        override fun onLocalInvitationRefused(localInvitation: LocalInvitation, response: String) {
            activity!!.runOnUiThread {
                videorequestsent = false
                Log.i("autolog", "onLocalInvitationRefused: " + localInvitation.content)
                Toast.makeText(
                    activity!!,
                    "Your Request Rejected By broadcaster",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        override fun onLocalInvitationCanceled(localInvitation: LocalInvitation) {
            activity!!.runOnUiThread {
                videorequestsent = false
                Log.i("autolog", "onLocalInvitationCanceled: " + localInvitation)
            }

        }

        override fun onLocalInvitationFailure(localInvitation: LocalInvitation, i: Int) {
            activity!!.runOnUiThread {
                videorequestsent = false
                Log.i("autolog", "onLocalInvitationFailure: " + i);
            }
        }

        override fun onRemoteInvitationReceived(remoteInvitation: RemoteInvitation) {
            activity!!.runOnUiThread {
                Log.i("autolog", "remoteInvitation: " + remoteInvitation.content)
                Log.i("autolog", "remoteInvitation: " + remoteInvitation)
                Log.i("autolog", "remoteInvitation: " + remoteInvitation.state)
                if (remoteInvitation.content.toString()
                        .equals("Has Request To Join Broadcast")
                ) {


                    remoteInvitationmodel.add(
                        RemoteInvitationModel(
                            remoteInvitation,
                            remoteInvitation.channelId
                        )
                    )
                    if (waitinglist.size != 0) {
                        for (k in waitinglist.indices) {
                            if (!remoteInvitation.channelId.equals(
                                    waitinglist.get(k).user_id,
                                    true
                                )
                            ) {
                                if (remoteInvitation.content.toString()
                                        .equals("Has Request To Join Broadcast")
                                ) {
                                    for (i in audiencelist.indices) {
                                        Log.i(
                                            "autolog",
                                            "remoteInvitation: " + audiencelist.get(i).user_id
                                        )
                                        if (remoteInvitation.channelId.equals(
                                                audiencelist.get(i).user_id,
                                                true
                                            )
                                        ) {
                                            waitinglist.add(audiencelist.get(i))
                                        }
                                    }
                                }
                            } else {

                            }
                        }

                    } else {
                        if (remoteInvitation.content.toString()
                                .equals("Has Request To Join Broadcast")
                        ) {
                            for (i in audiencelist.indices) {
                                Log.i("autolog", "remoteInvitation: " + audiencelist.get(i).user_id)
                                if (remoteInvitation.channelId.equals(
                                        audiencelist.get(i).user_id,
                                        true
                                    )
                                ) {
                                    waitinglist.add(audiencelist.get(i))
                                }
                            }
                        }

                    }


                    callAdapterRequests(waitinglist)
                } else if (remoteInvitation.content.toString()
                        .equals("End a Call")
                ) {
                    endGuest()
                }
            }
        }

        override fun onRemoteInvitationAccepted(remoteInvitation: RemoteInvitation) {
            Log.i("autolog", "onRemoteInvitationAccepted: " + remoteInvitation)
        }

        override fun onRemoteInvitationRefused(remoteInvitation: RemoteInvitation) {
            Log.i("autolog", "onRemoteInvitationRefused: " + remoteInvitation)

        }

        override fun onRemoteInvitationCanceled(remoteInvitation: RemoteInvitation) {
            Log.i("autolog", "onRemoteInvitationCanceled: " + remoteInvitation);

        }

        override fun onRemoteInvitationFailure(remoteInvitation: RemoteInvitation, i: Int) {
            Log.i("autolog", "onRemoteInvitationFailure: " + i);

        }
    }

    private fun callAdapterRequests(mRequests: ArrayList<Viewers>) {
        adapterRequests = AdapterRequests(activity!!, mRequests, role, 0)
        adapterRequests.setOnClickListener(this)
        if (mRequests.size > 0) {
            rvRequests.adapter = adapterRequests
            rvRequests.visibility = VISIBLE
            binding.tvNoRequests.visibility = GONE
            binding.tvNoOfRequests.text = mRequests.size.toString() + "Requests"
        } else {
            binding.tvNoRequests.visibility = VISIBLE
            rvRequests.visibility = GONE
            binding.tvNoOfRequests.text = mRequests.size.toString() + "Requests"

        }
    }

    private fun callAdapterGuests(mRequests: ArrayList<Viewers>) {
        adapterRequests = AdapterRequests(activity!!, mRequests, role, 1)
        adapterRequests.setOnClickListener(this)
        if (mRequests.size > 0) {
            rvRequests.adapter = adapterRequests
            rvRequests.visibility = VISIBLE
            binding.tvNoRequests.visibility = GONE
            binding.tvNoOfRequests.text = mRequests.size.toString() + "Guest"
        } else {
            binding.tvNoRequests.visibility = VISIBLE
            rvRequests.visibility = GONE
            binding.tvNoOfRequests.text = mRequests.size.toString() + "Guest"

        }
    }

    private fun showDialogInGolive() {
        binding.cvRequests.visibility = VISIBLE
        val linearLayoutManager1 = LinearLayoutManager(activity!!)
        linearLayoutManager1.orientation = RecyclerView.VERTICAL
        rvRequests.setLayoutManager(linearLayoutManager1)
        binding.tvWaiting.performClick()
        Log.i("autolog", "videorequestsent: " + videorequestsent)
        if (!videorequestsent) {
            if (cRole != 1) {
                Log.i("autolog", "videorequestsent: if" + videorequestsent);

                binding.btnJoin.visibility = VISIBLE
            } else {
                Log.i("autolog", "videorequestsent: else" + videorequestsent);

                binding.btnJoin.visibility = GONE
            }
        } else {
            binding.btnJoin.visibility = GONE
        }
        binding.btnJoin.setOnClickListener {
            binding.btnJoin.visibility = GONE
            videorequestsent = true
            Log.i("autolog", "videorequestsent: " + videorequestsent)

            var mInvitation: LocalInvitation
            mInvitation = callMgr.createLocalInvitation(broadCaster!!.username)
            mInvitation.setChannelId(SessionUser.user.user_id)
            mInvitation.setContent("Has Request To Join Broadcast")

            callMgr.sendLocalInvitation(
                mInvitation,
                object : ResultCallback<Void?> {
                    override fun onSuccess(aVoid: Void?) {

                    }

                    override fun onFailure(errorInfo: ErrorInfo) {
                        activity!!.runOnUiThread {
                            Log.i("autolog", "errorInfo: " + errorInfo)
                            utils.showToast(errorInfo.errorDescription)
                            videorequestsent = false

                        }

                    }
                })
            cvRequests.visibility = View.GONE
            btn_join.visibility = View.GONE
        }


        binding.tvWaiting.setOnClickListener {
            callAdapterRequests(waitinglist)
            binding.tvWaiting.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.black
                )
            );
            binding.tvGuestLive.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.colorAccent
                )
            );
            binding.tvRecommended.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.colorAccent
                )
            );
        }
        binding.tvGuestLive.setOnClickListener {

            callAdapterGuests(guestlist)
            binding.tvGuestLive.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.black
                )
            );
            binding.tvWaiting.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.colorAccent
                )
            );
            binding.tvRecommended.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.colorAccent
                )
            );
        }
        binding.tvRecommended.setOnClickListener {
            binding.tvRecommended.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.black
                )
            );
            binding.tvGuestLive.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.colorAccent
                )
            );
            binding.tvWaiting.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    getApplicationContext(),
                    R.color.colorAccent
                )
            );
        }


    }

    override fun onRejectRequest(user: Viewers?, position: Int) {
        Log.i("autolog", "user: " + user)

        for (i in remoteInvitationmodel.indices) {
            if (user!!.user_id.equals(remoteInvitationmodel.get(i).userid)) {
                callMgr.refuseRemoteInvitation(
                    remoteInvitationmodel.get(i).remoteInvitation,
                    object : ResultCallback<Void?> {
                        override fun onSuccess(aVoid: Void?) {
                            Log.i("autolog", "aVoid: " + aVoid)

                        }

                        override fun onFailure(errorInfo: ErrorInfo) {
                            Log.i("autolog", "errorInfo: " + errorInfo)

                        }
                    })


            }
        }
        waitinglist.removeAt(position)
        cvRequests.visibility = GONE

    }

    override fun onEndRequest(user: Viewers?, position: Int) {
        Log.i("autolog", "user: " + user)
        if (user!!.user_id.equals(SessionUser.user.user_id, true)) {
            endGuest()
        } else {
            if (role == 0) {
                var mInvitation: LocalInvitation
                mInvitation = callMgr.createLocalInvitation(user!!.username)
                mInvitation.setChannelId(SessionUser.user.user_id)
                mInvitation.setContent("End a Call")
                callMgr.sendLocalInvitation(
                    mInvitation,
                    object : ResultCallback<Void?> {
                        override fun onSuccess(aVoid: Void?) {

                        }

                        override fun onFailure(errorInfo: ErrorInfo) {
                            activity!!.runOnUiThread {
                                Log.i("autolog", "errorInfo: " + errorInfo)
                                utils.showToast(errorInfo.errorDescription)
                                videorequestsent = false

                            }

                        }
                    })
            }
        }
        cvRequests.visibility = GONE

    }

    override fun onAcceptRequest(user: Viewers?, position: Int) {
        Log.i("autolog", "user: " + user)
        for (i in remoteInvitationmodel.indices) {
            if (user!!.user_id.equals(remoteInvitationmodel.get(i).userid)) {

                callMgr.acceptRemoteInvitation(
                    remoteInvitationmodel.get(i).remoteInvitation,
                    object : ResultCallback<Void?> {
                        override fun onSuccess(aVoid: Void?) {
                            Log.i("autolog", "aVoid: " + aVoid)

                        }

                        override fun onFailure(errorInfo: ErrorInfo) {
                            Log.i("autolog", "errorInfo: " + errorInfo);

                        }
                    })

            }
        }
        waitinglist.removeAt(position)
        cvRequests.visibility = GONE
    }


    fun endGuest() {
        if (role == 1) {
            videorequestsent = false
            role = 2
            goLivePresenter.addguest(
                AppConstants.removeGuest,
                AppConstants.defaultpage,
                AppConstants.defaultlength,
                userid
            )
            cRole = Constants.CLIENT_ROLE_AUDIENCE
            doConfigEngine(Constants.CLIENT_ROLE_AUDIENCE)
            doRemoveRemoteUi(SessionUser.user.id.toInt())
        }
    }

    private fun audienceApiCall(userid: String?, action: String?, page: String?, lenght: String?) {
        Log.i("autolog", "action: " + action)
        Log.i("autolog", "userid: " + userid)
        listUserPresenter.userList(userid!!, action!!, page!!, lenght!!)
    }

    fun viewsvisible(visibility: Int) {
        binding.bottomBroadcaster.visibility = visibility
        binding.rvmessages.visibility = visibility
        binding.rvImages1.visibility = visibility
        binding.topArea.visibility = visibility
        binding.relativeLayout.visibility = visibility
    }

    override fun OnClicked(gift: Gift) {
        Log.i("autolog", "gift: " + gift)
        giftShow(gift)

    }


fun giftShow(gift:Gift){
    binding.ivImng.visibility= VISIBLE

    Glide.with(activity!!)
        .load(gift.url)
        .into(binding.ivImng)

    giftHandler.postDelayed(Runnable {
        binding.ivImng.visibility= GONE

    }, Integer.valueOf(gift.duration).toLong())

}

}
