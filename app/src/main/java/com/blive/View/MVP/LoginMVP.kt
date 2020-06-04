package com.blive.View.MVP

import com.blive.Models.User
import com.blive.Models.UserModel


interface LoginMVP {
    interface MainView {
        fun setError(error: String?)
        fun setsuccess(status: String?, userModel: User)
    }
    interface MainPresenter {
        fun signin(login_domain: String, email: String, mobile: String, device_id: String)
        fun createAccont( name: String, username: String, gender: String, login_domain: String
                         , email: String, mobile: String, device_id: String, referral: String,
                          gcm_registration_id: String,profile_pic: String)
    }

}