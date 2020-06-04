package com.blive.View.Fragment

import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.blive.Models.User
import com.blive.Presenter.LoginPresenter
import com.blive.R
import com.blive.View.MVP.LoginMVP
import com.blive.View.Util.Utils
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.iid.FirebaseInstanceId
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

class FragmentNewUser : androidx.fragment.app.Fragment(), LoginMVP.MainView {

    private var etPhoneNumber: EditText? = null
    private  var etOTP:EditText? = null
    private val country = ""
    private  var mobile:String? = ""
    private var bVerify: Button? = null
    private var btnContinue: Button? = null
    private var rlGenderMale: RelativeLayout? = null
    private var rlGenderFeMale: RelativeLayout? = null
    private var tvGenderFeMale: TextView? = null
    private var tvGenderMale: TextView? = null
    private  var bSendOTP:Button? = null
    private var bReset: TextView? = null
    lateinit var utils: Utils
    lateinit var navController: NavController
    private var name = ""
    private var inputName:TextInputLayout? = null
    private var inputUserName:TextInputLayout? = null
    private var inputMobile:TextInputLayout? = null
    private var inputReferral:TextInputLayout? = null
    private var etUserName:EditText? = null
    private var etReferral:EditText? = null
    private var etMobile:EditText? = null
    private var etName:EditText? = null
    private var userName:String? = ""
    private var gender:String? = "Male"
    private var email:String? = ""
    private var login_domain:String? = ""
    private var image:String? = ""
    private var referral:String? = ""
    private var gcm_registration_id:String? = ""
    private var regId:String? = ""
    var genders = arrayOf("Male", "Female")
     var sharedPreferences: SharedPreferences? = null

    lateinit var loginPresenter: LoginPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(activity)

        inputReferral = view.findViewById(R.id.input_referral)
        inputMobile = view.findViewById(R.id.input_mobile)
        inputName = view.findViewById(R.id.input_name)
        inputUserName = view.findViewById(R.id.input_username)
        etName = view.findViewById(R.id.et_name)
        etMobile = view.findViewById(R.id.etPhoneNumber)
        etReferral = view.findViewById(R.id.et_referral)
        etUserName = view.findViewById(R.id.et_userName)
        btnContinue = view.findViewById(R.id.btn_continue)
        bVerify = view.findViewById(R.id.bVerify)
        bSendOTP = view.findViewById(R.id.bSendOtp)
        bReset = view.findViewById(R.id.bReset)
        rlGenderMale = view.findViewById(R.id.rl_genderMale)
        rlGenderFeMale = view.findViewById(R.id.rl_genderFeMale)
        tvGenderFeMale = view.findViewById(R.id.tv_genderFeMaleText)
        tvGenderMale = view.findViewById(R.id.tv_genderMaleText)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)
        etOTP = view.findViewById(R.id.et_otp)
        navController = Navigation.findNavController(view)
        loginPresenter = LoginPresenter(this, activity!!)

        if (arguments != null) {
            mobile = arguments?.getString("mobileNum")!!
            login_domain = arguments?.getString("domain")!!
            email = arguments?.getString("email")!!
            image = arguments?.getString("image")!!
        }

        displayFireBaseRegId()

        sharedPreferences = getApplicationContext().getSharedPreferences("Mobile_Domain", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putString("isDomain", login_domain)
        editor.putString("isValue", "0")
        editor.commit()

        val intent: Intent = Intent()
        userName = intent.getStringExtra("userName")
        email = intent.getStringExtra("email")
        login_domain = intent.getStringExtra("domain")
        image = intent.getStringExtra("image")
        mobile = intent.getStringExtra("mobile")

        if (userName != null) {
            userName = userName!!.replace(" ", "")
            name = userName!!
            userName = userName!!.replace(".", "")
            userName = userName!!.replace("#", "")
            userName = userName!!.replace("$", "")
            userName = userName!!.replace("[", "")
            userName = userName!!.replace("]", "")
            etName!!.setText(name)
            etUserName!!.setText(userName)
        }

        btnContinue!!.setOnClickListener {  V: View ->
            if (utils.isNetworkAvailable) {
                if (login_domain.equals("mobile", ignoreCase = true)) {
                    if (isValidMobile()) {
                        callSignUpAPI(name,userName,gender,login_domain,email,mobile,
                            utils.getDeviceId(activity),referral,gcm_registration_id,image)
                    }
                } else {
                    if (isValid()) {
                        callSignUpAPI(name,userName,gender,login_domain,email,mobile,
                            utils.getDeviceId(activity),referral,gcm_registration_id,image)
                    }
                }
            }
        }

        rlGenderMale!!.setBackground(getApplicationContext().resources.getDrawable(R.drawable.gender_male_background))
        rlGenderFeMale!!.setBackground(getApplicationContext().resources.getDrawable(R.drawable.gender_female_unselected))
        tvGenderFeMale!!.setTextColor(getApplicationContext().resources.getColor(R.color.black))
        tvGenderMale!!.setTextColor(getApplicationContext().resources.getColor(R.color.white))

        rlGenderMale!!.setOnClickListener { V: View ->
            gender = "Male"
            rlGenderMale!!.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_male_background))
            rlGenderFeMale!!.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_female_unselected))
            tvGenderFeMale!!.setTextColor(getApplicationContext().getResources().getColor(R.color.black))
            tvGenderMale!!.setTextColor(getApplicationContext().getResources().getColor(R.color.white))
        }

        rlGenderFeMale!!.setOnClickListener {V: View ->
        gender = "Female"
            rlGenderMale!!.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_male_unselected))
            rlGenderFeMale!!.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.gender_female_background))
            tvGenderFeMale!!.setTextColor(getApplicationContext().getResources().getColor(R.color.white))
            tvGenderMale!!.setTextColor(getApplicationContext().getResources().getColor(R.color.black))
        }
    }

    private fun callSignUpAPI(profileName: String?,username: String?,gender: String?,login_domain: String?,
                              email: String?,mobile: String?, device_id: String?,referral: String?,
                              gcm_registration_id: String?,profile_pic: String?) {
        Log.e("TAg" , profileName + username )
        Log.e("TAg" ,  gender!! + login_domain!! + email!! + mobile!! + device_id!! )
        loginPresenter.createAccont(profileName!!,username!!, gender!!, login_domain!!, email!!, mobile!!,device_id!!,
            referral!!, gcm_registration_id!!, profile_pic!!)
    }

    private fun isValid(): Boolean {
        userName = etUserName!!.getText().toString().trim()
        name = etName!!.getText().toString().trim()
        referral = etReferral!!.getText().toString().trim()
        if (name.isEmpty()) {
            inputName!!.setError("Name must not be empty")
            Toast.makeText(activity!!,"Name must not be empty",Toast.LENGTH_SHORT).show()
        } else if (name.length < 3) {
            inputName!!.setError("Name must be minimum 3 characters")
            Toast.makeText(activity!!,"Name must be minimum 3 characters",Toast.LENGTH_SHORT).show()
        } else if (userName!!.isEmpty()) {
            inputUserName!!.setError("UserName must not be empty")
            Toast.makeText(activity!!,"UserName must not be empty",Toast.LENGTH_SHORT).show()
        } else if (userName!!.length < 3) {
            inputUserName!!.setError("UserName must be minimum 3 characters")
            Toast.makeText(activity!!,"UserName must be minimum 3 characters",Toast.LENGTH_SHORT).show()
        } else return true
        return false
    }

    private fun isValidMobile(): Boolean {
        userName = etUserName!!.getText().toString().trim()
        mobile = etMobile!!.getText().toString().trim()
        name = etName!!.getText().toString().trim()
        if (name.isEmpty()) {
            inputName!!.setError("Name must not be empty")
            Toast.makeText(activity!!,"Name must not be empty",Toast.LENGTH_SHORT).show()
        } else if (name.length < 3) {
            inputName!!.setError("Name must be minimum 3 characters")
            Toast.makeText(activity!!,"Name must be minimum 3 characters",Toast.LENGTH_SHORT).show()
        } else if (userName!!.isEmpty()) {
            inputUserName!!.setError("UserName must not be empty")
            Toast.makeText(activity!!,"UserName must not be empty",Toast.LENGTH_SHORT).show()
        } else if (userName!!.length < 3) {
            inputUserName!!.setError("UserName must be minimum 3 characters")
            Toast.makeText(activity!!,"UserName must be minimum 3 characters",Toast.LENGTH_SHORT).show()
        } else if (mobile!!.length == 0) {
            inputMobile!!.setError("Mobile number is missing")
            Toast.makeText(activity!!,"Mobile number is missing",Toast.LENGTH_SHORT).show()
        } else if (mobile!!.length < 10) {
            inputMobile!!.setError("Mobile number is missing")
            Toast.makeText(activity!!,"Invalid Mobile Number",Toast.LENGTH_SHORT).show()
        } else if (referral == userName) {
            inputReferral!!.setError("Invalid Referral Code!")
            Toast.makeText(activity!!,"IInvalid Referral Code!",Toast.LENGTH_SHORT).show()
        } else return true
        return false
    }

    override fun setError(error: String?) {
        utils.showToast(error)
    }

    override fun setsuccess(status: String?, userModel: User) {
        navController!!.navigate(R.id.action_fragmentNewUser_to_homeFragment)
    }

    private fun displayFireBaseRegId() {
        regId = FirebaseInstanceId.getInstance().token
        Log.e(TAG, "displayFireBaseRegId: $regId")
    }

    fun onItemSelected(
        arg0: AdapterView<*>?,
        arg1: View?,
        position: Int,
        id: Long
    ) {
        gender = genders[position]
    }

    fun onNothingSelected(arg0: AdapterView<*>?) {

    }
}
