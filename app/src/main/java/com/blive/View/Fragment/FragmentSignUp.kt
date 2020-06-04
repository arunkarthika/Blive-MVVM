package com.blive.View.Fragment

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.BliveApplication
import com.blive.Models.User
import com.blive.Presenter.LoginPresenter
import com.blive.R
import com.blive.View.Activity.ApplicationData
import com.blive.View.Activity.InstagramApp
import com.blive.View.Activity.Testactivity
import com.blive.View.MVP.LoginMVP
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import com.crashlytics.android.Crashlytics
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FragmentSignUp : androidx.fragment.app.Fragment(), LoginMVP.MainView {

    lateinit var bGoogle: ImageView
    lateinit var bInstagram: ImageView
    lateinit var bTwitter: ImageView
    lateinit var btnConnect: ImageView
    lateinit var rlPhone: RelativeLayout
    private var mApp: InstagramApp? = null
    lateinit var btnGetAllImages: Button
    lateinit var btnFollowers: Button
    lateinit var btnFollwing: Button
    lateinit var btnViewInfo: Button
    lateinit var llAfterLoginView: LinearLayout
    lateinit var bFb: ImageView
    lateinit var loginButton: LoginButton
    private var bTwitterLogin: TwitterLoginButton? = null
    lateinit var loginPresenter: LoginPresenter
    lateinit var navController: NavController
    lateinit var utils: Utils
    private val RC_SIGN_IN = 123
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var username: String = ""
    var accessToken: AccessToken? = null
    private var callbackManager: CallbackManager? = null
    private val EMAIL = "email"
    private var userName = ""
    private var email: String? = ""
    private var image: String? = ""
    private var domain: String? = ""
    private var versionName: String? = ""
    var ins_username: String? = null
    var ins_email: kotlin.String? = null
    var ins_image: kotlin.String? = null
    private var userInfo = HashMap<String, String>()
    private var userInfoHashmap =
        HashMap<String, String>()

    internal var mTwitterAuthClient: TwitterAuthClient? = null

    private val handler =
        Handler(Handler.Callback { msg ->
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp!!.userInfo
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(
                    activity!!, "Check your network.", Toast.LENGTH_SHORT
                ).show()
            }
            false
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        FacebookSdk.sdkInitialize(activity)
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val config = TwitterConfig.Builder(activity!!)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                    resources.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                    resources.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)
                )
            )
            //pass the created app Consumer KEY and Secret also called API Key and Secret
            .debug(true)//enable debug mode
            .build()

//finally initialize twitter with created configs
        Twitter.initialize(config)

        mTwitterAuthClient = TwitterAuthClient()

        getTwitterSession()

        utils = Utils(activity)
        Testactivity.bottomhide()
        mApp = InstagramApp(
            activity!!,
            ApplicationData.CLIENT_ID,
            ApplicationData.CLIENT_SECRET,
            ApplicationData.REDIRECT_URL
        )

        mApp!!.setListener(object : InstagramApp.OAuthAuthenticationListener {
            override fun onSuccess() {
                mApp!!.fetchUserName(handler)
                email = mApp!!.getTOken()
                ins_email = mApp!!.getTOken()
                ins_username = mApp!!.getUserName()
                userInfo = mApp!!.getUserInfo()
                image = userInfo.get(InstagramApp.TAG_PROFILE_PICTURE)
                userName = mApp!!.getUserName()
                if (userName != null && email != null) {
                    try {
                        domain = mApp!!.domain
                        callAPICheckUserIns(email!!)
                    } catch (e: java.lang.Exception) {
                        Crashlytics.logException(e)
                    }
                } else {

                }
            }

            override fun onFail(error: String?) {
                Toast.makeText(activity!!, error, Toast.LENGTH_SHORT).show()
            }
        })

        mGoogleSignInClient = BliveApplication.getInstance().getGoogleSignInClient()


        val EMAIL = "email"



        rlPhone = view.findViewById(R.id.rlPhone)
        llAfterLoginView = view.findViewById(R.id.llAfterLoginView)
        btnViewInfo = view.findViewById(R.id.btnViewInfo)
        btnGetAllImages = view.findViewById(R.id.btnGetAllImages)
        btnFollowers = view.findViewById(R.id.btnFollows)
        btnFollwing = view.findViewById(R.id.btnFollowing)
        btnConnect = view.findViewById(R.id.btnConnect)
        loginButton = view.findViewById(R.id.login_button)
        loginButton.setReadPermissions(Arrays.asList(EMAIL))
        bFb = view.findViewById(R.id.bFb)
        bTwitter = view.findViewById(R.id.bTwitter)
        bInstagram = view.findViewById(R.id.bInstagram)
        bGoogle = view.findViewById(R.id.bGoogle)
        bTwitterLogin = view.findViewById(R.id.bTwitterLogin)
        callbackManager = CallbackManager.Factory.create()
        loginPresenter = LoginPresenter(this, activity!!)
        navController = Navigation.findNavController(view)

        bindEventHandlers()

        if (SessionUser.getLoginSession()) {
            navController!!.navigate(R.id.action_signUpFragment_to_homeFragment)
        }

        bGoogle.setOnClickListener {
            if (utils.isNetworkAvailable()) {
                val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }

        loginButton.setOnClickListener { v: View? ->
            if (!utils.isNetworkAvailable) {

            }
        }

        bFb.setOnClickListener {
            if (utils.isNetworkAvailable()) {
                LoginManager.getInstance().logOut();
                LoginManager.getInstance()
                    .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
                LoginManager.getInstance().registerCallback(callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(loginResult: LoginResult) {
                            Log.d("MainActivity", "Facebook token: " + loginResult)
                            accessToken = loginResult.accessToken
                            val data_request = GraphRequest.newMeRequest(
                                loginResult.accessToken
                            ) { json_object: JSONObject, response: GraphResponse? ->
                                val jsondata = json_object.toString()
                                Log.i("autolog", "jsondata: " + jsondata);
                                var user_name: String? = null
                                var user_img: String? = null
                                var mail: String? = null
                                try {
                                    var json_object1 = JSONObject(jsondata)
                                    user_name = json_object1.getString("name")
                                    userName = user_name
                                    Log.i("autolog", "userName: " + userName);
                                    val picture = json_object1.getJSONObject("picture")
                                    val data = picture.getJSONObject("data")
                                    user_img = data.getString("url")
                                    image = user_img
                                    mail = json_object1.getString("email")
                                    if (mail != null) {
                                        email = mail
                                        Log.i("autolog", "email: " + email);
                                    }
                                } catch (e: JSONException) {
                                    Log.i("autolog", "e: " + e.localizedMessage);
                                    e.printStackTrace()
                                }
                            }


                            val permission_param = Bundle()
                            permission_param.putString(
                                "fields",
                                "id,name,email,picture.width(120).height(120)"
                            )
                            data_request.parameters = permission_param
                            data_request.executeAsync()
                            val handler = Handler()
                            handler.postDelayed({
                                if (email != null) {
                                    email = accessToken!!.userId
                                }
                                domain = "facebook"
                                if (userName != null && email != null) {
                                    try {
                                        callAPICheckUser(domain!!, email!!, "")
                                    } catch (e: java.lang.Exception) {
                                        Crashlytics.logException(e)
                                    }
                                } else {

                                }
                            }, 2000)
                        }

                        override fun onCancel() {
                            Log.d("MainActivity", "Facebook onCancel.")

                        }

                        override fun onError(error: FacebookException) {
                            Log.d("MainActivity", "Facebook onError." + error.localizedMessage)

                        }
                    })
            }
        }

        bTwitter.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                Log.e(TAG, "Onclicked Twitter");
                CookieSyncManager.createInstance(activity);
                var cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                TwitterCore.getInstance().getSessionManager().clearActiveSession()
                twitterLogin()
//                bTwitterLogin!!.performClick()
            }
        }

        rlPhone.setOnClickListener { V: View ->
            if (utils.isNetworkAvailable) {
                navController!!.navigate(R.id.action_signUpFragment_to_fragmentMobile)
            }
        }


        bInstagram.setOnClickListener { v: View? ->
            if (utils.isNetworkAvailable) {
                connectOrDisconnectUser()
            }
        }


    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("autolog", "resultCode: " + resultCode);
        Log.i("autolog", "requestCode: " + requestCode);
        Log.i("autolog", "data: " + data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (mTwitterAuthClient != null) {
            mTwitterAuthClient!!.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try { // Google Sign In was successful, authenticate with FireBase
                val account = task.getResult(
                    ApiException::class.java
                )
                handleSignInResult(account!!)
            } catch (e: ApiException) { // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e);
            }
        }
    }

    private fun handleSignInResult(acct: GoogleSignInAccount) {
        try {
            val profilePicUrl = acct.photoUrl
            if (profilePicUrl != null) image = profilePicUrl.toString()
        } catch (e: Exception) {
            Log.e(TAG, "handleSignInResult: $e")
        }
        userName = acct.displayName!!
        email = acct.email
        Log.i("autolog", "email: " + email);
        domain = "google"
        try {
            callAPICheckUser(domain!!, email!!, "")
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    private fun handleTwitterSession(session: TwitterSession) {
        val twitterAuthClient = TwitterAuthClient()
        twitterAuthClient.requestEmail(
            session,
            object : Callback<String>() {
                override fun success(emailResult: Result<String>) {
                    val mail = emailResult.data
                    email = mail
                    userName = session.userName
                    if (email!!.isEmpty()) email = userName
                    domain = "twitter"
                    image = "https://twitter.com/" + session.userName + "/profile_image?size=bigger"
                    try {
                        callAPICheckUser(domain!!, email!!, "")
                    } catch (e: java.lang.Exception) {
                        Crashlytics.logException(e)
                    }
                }

                override fun failure(e: TwitterException) {

                }
            })
    }

    private fun callAPICheckUser(logindomain: String, email: String, mobile: String) {
        loginPresenter.signin(logindomain, email, mobile, utils.getDeviceId(activity))
    }

    override fun setError(error: String?) {
        Log.i("autolog", "error: " + error)
        if (error.equals("New user")) {
            navController!!.navigate(R.id.action_signUpFragment_to_fragmentNewUser)
        }
        utils.showToast(error)
    }

    override fun setsuccess(status: String?, userModel: User) {
        navController!!.navigate(R.id.action_signUpFragment_to_homeFragment)
    }

    private fun bindEventHandlers() {
        btnConnect.setOnClickListener {
            if (utils.isNetworkAvailable) {
                connectOrDisconnectUser()
            }

        }
        btnViewInfo.setOnClickListener { }
        btnGetAllImages.setOnClickListener { }
        btnFollwing.setOnClickListener { }
        btnFollowers.setOnClickListener { }
    }

    fun onClick(v: View) {
        if (v === btnConnect) {
            if (utils.isNetworkAvailable) {
                connectOrDisconnectUser()
            }
        }
    }

    private fun connectOrDisconnectUser() {
        if (mApp!!.hasAccessToken()) {
            val builder =
                AlertDialog.Builder(activity!!)
            builder.setMessage("connect from Instagram?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, id ->
                    mApp!!.resetAccessToken()
                    mApp!!.authorize()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        } else {
            mApp!!.authorize()
        }
    }

    private fun callAPICheckUserIns(email: String) {
        val utils = Utils(activity!!)
        val deviceId = utils.getDeviceId(activity!!)
        domain = "instagram"
    }

    private fun getTwitterSession(): TwitterSession? {

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return TwitterCore.getInstance().sessionManager.activeSession
    }

    fun twitterLogin() {
        if (getTwitterSession() == null) {
            mTwitterAuthClient!!.authorize(activity!!, object : Callback<TwitterSession>() {
                override fun success(twitterSessionResult: Result<TwitterSession>) {
                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show()
                    Log.e(" twitterLogin()","succwss")
                    val twitterSession = twitterSessionResult.data
                    fetchTwitterEmail(twitterSession)
                }

                override fun failure(e: TwitterException) {
                    Toast.makeText(activity, "Failure", Toast.LENGTH_SHORT).show()
                    Log.e(" twitterLogin()",e.toString())
                }
            })
        } else {//if user is already authenticated direct call fetch twitter email api
            fetchTwitterEmail(getTwitterSession())
        }
    }

    fun fetchTwitterEmail(twitterSession: TwitterSession?) {
        mTwitterAuthClient?.requestEmail(twitterSession, object : Callback<String>() {
            override fun success(result: Result<String>) {

                Log.d(TAG, "twitterLogin:userId" + twitterSession!!.userId)
                Log.d(TAG, "twitterLogin:userName" + twitterSession!!.userName)
                Log.d(TAG, "twitterLogin: result.data" + result.data)

//                val i = Intent(this@LoginActivity, SignupActivity::class.java)
//                val bundle = Bundle()
//                bundle.putString(Utils.FIRST_NAME, "")
//                bundle.putString(Utils.LAST_NAME, "")
//                bundle.putString(Utils.EMAIL, result.data)
//                bundle.putString(Utils.AUTH_TYPE, "TWITTER")
//                bundle.putString(Utils.TPA_TOKEN, twitterSession.userId.toString())
//                i.putExtras(bundle)
//                startActivity(i)
//                finish()
                var userId = twitterSession!!.userId
                var username = twitterSession!!.userName
                var email = result.data
                var token = twitterSession.userId.toString()
                var str = "Now you are successfully login with twitter \n\n"
                var tokenStr = ""
                var usernameStr = ""
                var emailStr = ""
                if (token != null || token != "") {
                    tokenStr = "User Id : " + token + "\n\n"
                }

                if (username != null || username != "") {
                    usernameStr = "Username : " + username + "\n\n"
                }

//                if (email != null || email != "") {
//                    emailStr = "Email ID : " + email + "\n\n"
//                }

            }

            override fun failure(exception: TwitterException) {
                Toast.makeText(
                    activity,
                    "Failed to authenticate. Please try again.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }


}
