package com.blive.Presenter

import android.content.Context
import android.util.Log
import com.blive.Models.Register
import com.blive.Models.UserModel
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import com.blive.View.MVP.LoginMVP
import com.blive.View.Util.Retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class LoginPresenter(var loginmvp: LoginMVP.MainView, val context: Context) :
    LoginMVP.MainPresenter {
    override fun signin(login_domain: String, email: String, mobile: String, device_id: String) {
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient.instance.login(login_domain, email, mobile, device_id)
                .enqueue(object : retrofit2.Callback<UserModel> {
                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        loginmvp.setError(t.message)
                        Log.i("autolog", "login_domain: " + login_domain+email+device_id)
                        Log.i("autolog", "t: " + t.localizedMessage)
                        Log.i("autolog", call.request().url().toString())
                    }
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        Log.i("autolog", "genericResponse: " + response.raw().request().url())
                        Log.i("autolog", "genericResponse: " + response.code())
                        Log.i("autolog", "genericResponse: " + response.message())

                        if (response.body() != null) {
                            val genericResponse: UserModel? = response.body()
                            if (genericResponse!!.status.equals("0")){
                                if (genericResponse!!.message.equals("New user")){
                                    loginmvp.setError(genericResponse.message)
                                }else{
                                    SessionUser.saveLoginSession()
                                    SessionUser.saveBearerToken(genericResponse.body.activation_code)
                                    SessionUser.saveUser(genericResponse.body)
                                    loginmvp.setsuccess(genericResponse.message,genericResponse.body)
                                }
                            }else{
                                loginmvp.setError(genericResponse.message)
                            }
                        } else {
                            loginmvp.setError(response.errorBody().toString())
                        }
                    }
                })
        } else {
            loginmvp.setError("No Netwrok Connection")
        }
    }

    override fun createAccont(
        name: String,
        username: String,
        gender: String,
        login_domain: String,
        email: String,
        mobile: String,
        device_id: String,
        referral: String,
        gcm_registration_id: String,
        profile_pic: String
    ) {
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient.instance.register(name,username,gender,login_domain,email,
                mobile,device_id,referral,gcm_registration_id,profile_pic)
                .enqueue(object : retrofit2.Callback<Register> {
                    override fun onFailure(call: Call<Register>, t: Throwable) {
                        loginmvp.setError(t.message)
                    }
                    override fun onResponse(call: Call<Register>, response: Response<Register>) {
                             if (response.body() != null) {
                            val genericResponse: Register? = response.body()
                            if (genericResponse!!.status.equals("0")){
                                if (genericResponse!!.message.equals("New user")){
                                    loginmvp.setError(genericResponse.message)
                                }else{
                                    SessionUser.saveLoginSession()
                                    SessionUser.saveBearerToken(genericResponse.body.activation_code)
                                    SessionUser.saveUser(genericResponse.body)
                                    loginmvp.setsuccess(genericResponse.message,genericResponse.body)
                                }
                            }else{
                                loginmvp.setError(genericResponse.message)
                            }
                        } else {
                            loginmvp.setError(response.errorBody().toString())
                        }
                    }
                })
        } else {
            loginmvp.setError("No Netwrok Connection")
        }
    }
}