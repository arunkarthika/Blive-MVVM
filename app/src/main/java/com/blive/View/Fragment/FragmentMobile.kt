package com.blive.View.Fragment


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.Models.Country
import com.blive.Models.User
import com.blive.Presenter.LoginPresenter
import com.blive.R
import com.blive.View.MVP.LoginMVP
import com.blive.View.Util.Agora.ConstantApp
import com.blive.View.Util.Session.SessionLogin
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class FragmentMobile : Fragment(), LoginMVP.MainView {

    lateinit var mCallbacks: OnVerificationStateChangedCallbacks
    lateinit var loginPresenter: LoginPresenter
    private var mVerificationId: String? = null
    private var mResendToken: ForceResendingToken? = null
    private var mAuth: FirebaseAuth? = null
    private var etPhoneNumber: EditText? = null
    private var etOTP: EditText? = null
    private var country = ""
    private var countryCode: String? = ""
    private var phoneNumber: String? = ""
    private var mobile: String? = ""
    private var bVerify: Button? = null
    private var bSendOtp: Button? = null
    private var bReset: TextView? = null
    var spCountryName: SearchableSpinner? = null
    private val countryPosition = 0
    lateinit var navController: NavController
    lateinit var utils: Utils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(activity)

        navController = Navigation.findNavController(view)
        loginPresenter = LoginPresenter(this, activity!!)
        bReset = view.findViewById(R.id.bReset)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        etOTP = view.findViewById(R.id.et_otp)
        spCountryName = view.findViewById(R.id.spCountry)
        bSendOtp = view.findViewById(R.id.bSendOtp)
        bVerify = view.findViewById(R.id.bVerify)

        FirebaseApp.initializeApp(activity!!);
        mAuth = FirebaseAuth.getInstance()

        val constants_app = ConstantApp()
        val countryArrayList: ArrayList<Country> = constants_app.getCountry(activity!!)
        val countries = ArrayList<String>()
        for (i in countryArrayList.indices) {
            countries.add(countryArrayList[i].name!!)
        }

        val countryAdapter = ArrayAdapter(activity!!, R.layout.spinner_country, countries)

        bReset!!.setOnClickListener {
            etPhoneNumber!!.setText("")
            bReset!!.visibility = View.GONE
            etOTP!!.visibility = View.GONE
            etOTP!!.setText("")
            bVerify!!.visibility = View.GONE
            bSendOtp!!.visibility = View.VISIBLE
            etPhoneNumber!!.isEnabled = true
        }

        bVerify!!.setOnClickListener {
            try {
                val code = etOTP!!.text.toString().trim()
                if (code.length > 1) {
                    val credential =
                        PhoneAuthProvider.getCredential(mVerificationId!!, code)
                    if (code.equals(credential.smsCode, ignoreCase = true)) {
                        signInWithPhoneAuthCredential(credential)
                    } else {
                        utils.hideProgress()
                        Toast.makeText(activity!!, "Invalid OTP code!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    utils.hideProgress()
                    Toast.makeText(activity!!, "Please Enter OTP to verify!\"", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                utils.hideProgress()
                Log.e("OTP FAILED", e.toString())
                Toast.makeText(activity!!, "Verification failed please try after some time!", Toast.LENGTH_SHORT).show()
            }
        }

        spCountryName!!.setAdapter(countryAdapter)
        spCountryName!!.setSelection(countryPosition)

        spCountryName!!.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    val countrySelected: Country = countryArrayList[position]
                    country = countrySelected.name!!
                    countryCode = countrySelected.dialCode!!
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
            }
        }

        bSendOtp!!.setOnClickListener {
            if (utils.isNetworkAvailable) {
                if (!countryCode!!.isEmpty()) {
                    utils.showToast(etPhoneNumber!!.text.toString())
                    if (isValidMobile(etPhoneNumber!!.text.toString().trim())) {
                        phoneNumber = getE164Number()
                        mobile = getE164Number()
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber.toString(),
                            /* Phone number to verify*/ 60, /* Timeout duration*/ TimeUnit.SECONDS,
                            /* Unit of timeout*/ activity!!, // Activity (for callback binding)
                            mCallbacks
                        ) // OnVerificationStateChangedCallbacks
                        bSendOtp!!.visibility = View.GONE
                        etOTP!!.visibility = View.VISIBLE
                        bVerify!!.visibility = View.VISIBLE
                        bReset!!.visibility = View.VISIBLE
                        etPhoneNumber!!.isEnabled = false
                    }
                } else {
                    utils.showToast("Select a Country !")
                }
            }
        }
    }

    private fun isValidMobile(phone: String): Boolean {
        var check = false
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length < 7 || phone.length > 15) { // if(phone.length() != 10) {
                etPhoneNumber!!.error = "Not Valid Number"
            } else {
                check = true
            }
        }
        return check
    }

    private fun getE164Number(): String? {
        return countryCode + etPhoneNumber!!.text.toString().replace(
            "\\D".toRegex(),
            ""
        ).trim { it <= ' ' }
    }

    private fun callOldUser(user: User) {
        Answers.getInstance().logLogin(
            LoginEvent()
                .putMethod(user.login_domain!!)
                .putSuccess(true)
        )
        SessionUser.saveUser(user)
        SessionLogin.saveLoginSession()
        navController!!.navigate(R.id.action_signUpFragment_to_homeFragment)
    }

    private fun callNewUser() {
        /* val intent = Intent(activity!!, ::class.java)
         intent.putExtra("mobile", mobile)
         intent.putExtra("domain", "mobile")
         intent.putExtra("email", mobile)
         intent.putExtra("image", "")
         startActivity(intent)*/
        navController!!.navigate(R.id.action_signUpFragment_to_homeFragment)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {

                    Toast.makeText(activity!!, "Verified Successfully!!", Toast.LENGTH_SHORT).show()

                    val user = task.result?.user

                    checkUser(mobile!!, mobile!!)


                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun checkUser(email: String, mobile: String) {
        loginPresenter.signin("mobile", email, mobile, utils.getDeviceId(activity))
    }

    override fun setError(error: String?) {
        Log.i("autolog", "error: " + error)
        if (error.equals("New user")){
            val mobileNum = mobile!!
            val domain = "mobile"
            val email = mobile!!
            val image = ""
            var bundle = bundleOf("mobileNum" to mobileNum,"domain" to domain,"email" to email,"image" to image)
            navController!!.navigate(R.id.action_fragmentMobile_to_fragmentNewUser,bundle)
        }else if(error.equals("Already exsits")){

        }
        utils.showToast(error)
    }

    override fun setsuccess(status: String?, userModel: User) {
       Answers.getInstance().logLogin(
            LoginEvent()
                .putMethod(userModel.login_domain!!)
                .putSuccess(true)
        )
        SessionUser.saveUser(userModel)
        SessionLogin.saveLoginSession()
        utils.hideProgress()
        finishAffinity(activity!!)
        navController!!.navigate(R.id.action_fragmentMobile_to_homeFragment)
    }
}
