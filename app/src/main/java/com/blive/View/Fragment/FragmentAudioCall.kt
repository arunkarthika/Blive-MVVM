package com.blive.View.Fragment

import android.Manifest
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import com.blive.Models.Seat
import com.blive.R
import com.blive.View.Adapter.MessageListAdapter
import com.blive.View.Adapter.SeatGridAdapter
import io.agora.chatroom.manager.ChatRoomEventListener
import io.agora.chatroom.manager.ChatRoomManager

class FragmentAudioCall : BaseFragment(), ChatRoomEventListener,SeatGridAdapter.OnItemClickListener {

    lateinit var navController: NavController
    private val PERMISSION_REQ_ID = 22
    var manager: ChatRoomManager? = null
        private set
    private var mSeatAdapter: SeatGridAdapter? = null
    private var mMessageAdapter: MessageListAdapter? = null
    private var mChannelId: String? = null
    private var mMuteRemote = false

    var container: ConstraintLayout? = null
    var tv_title: TextView? = null
    var btn_num: TextView? = null
    var rv_seat_grid: RecyclerView? = null
    var rv_message_list: RecyclerView? = null
    var cb_mixing: CheckBox? = null
    var btn_mic: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_call, container, false)
        initView()
        initManager()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        rv_seat_grid = view.findViewById(R.id.rv_seat_grid)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.navigate(R.id.action_fragmentWebView2_to_fragmentProfile)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initView() {

        container = view!!.findViewById(R.id.container)
        tv_title =  view!!.findViewById(R.id.tv_title)
        btn_num =  view!!.findViewById(R.id.btn_num)
        rv_seat_grid =  view!!.findViewById(R.id.rv_seat_grid)
        rv_message_list =  view!!.findViewById(R.id.rv_message_list)
        btn_mic =  view!!.findViewById(R.id.btn_mic)

        if (arguments != null) {

          /*  baseUrl = arguments?.getString("baseUrl")!!
            title = arguments?.getString("title")!!*/

           /* container!!.setBackgroundResource(intent.getIntExtra(BUNDLE_KEY_BACKGROUND_RES, 0))
            mChannelId = intent.getStringExtra(BUNDLE_KEY_CHANNEL_ID)
            tv_title!!.text = mChannelId
            initSeatRecyclerView()
            initMessageRecyclerView()*/
        }
    }

    private fun initMessageRecyclerView() {
        mMessageAdapter = MessageListAdapter(activity)
        rv_message_list!!.adapter = mMessageAdapter
        rv_message_list!!.layoutManager = LinearLayoutManager(activity)
        val spacing = resources.getDimensionPixelSize(R.dimen.item_message_spacing)
        rv_message_list!!.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect[spacing, 0, spacing] = spacing
            }
        })
    }

    private fun initSeatRecyclerView() {
        rv_seat_grid!!.setHasFixedSize(true)
        val animator: ItemAnimator = rv_seat_grid!!.getItemAnimator()!!
        if (animator is SimpleItemAnimator) animator.supportsChangeAnimations = false
        mSeatAdapter = SeatGridAdapter(activity)
        mSeatAdapter!!.setOnItemClickListener(this)
        rv_seat_grid!!.setAdapter(mSeatAdapter)

        rv_seat_grid!!.setLayoutManager(GridLayoutManager(activity!!, 5))
        val spacing = resources.getDimensionPixelSize(R.dimen.item_seat_spacing)
        rv_seat_grid!!.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect[spacing, spacing, spacing] = spacing
            }
        })
    }

    private fun initManager() {
        manager = ChatRoomManager.instance(activity)
        manager!!.setListener(this)
        if (checkPermission()) manager!!.joinChannel(mChannelId)
    }

    override fun initUIandEvent() {

    }

    override fun deInitUIandEvent() {

    }

    override fun onItemClick(view: View?, position: Int, seat: Seat?) {

    }

    private fun showSeatPop(view: View, ids: IntArray, userId: String?, position: Int) {

    }

    override fun onSeatUpdated(position: Int) {

    }

    override fun onUserGivingGift(userId: String?) {

    }

    override fun onMessageAdded(position: Int) {

    }

    override fun onMemberListUpdated(userId: String?) {

    }

    override fun onUserStatusChanged(userId: String?, muted: Boolean?) {

    }

    override fun onAudioMixingStateChanged(isPlaying: Boolean) {

    }

    override fun onAudioVolumeIndication(userId: String?, volume: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        manager!!.leaveChannel()
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) != PermissionChecker.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQ_ID)
            return false
        }
        return true
    }

    companion object {
        const val BUNDLE_KEY_CHANNEL_ID = "channelId"
        const val BUNDLE_KEY_BACKGROUND_RES = "backgroundRes"
    }
}
