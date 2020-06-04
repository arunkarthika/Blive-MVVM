package com.blive.Presenter

import android.content.Context
import android.util.Log
import com.blive.Models.*
import com.blive.View.MVP.GoLiveMvp
import com.blive.View.Util.Retrofit.RetrofitClient
import com.blive.View.Util.Retrofit.RetrofitClient_NEW
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GoLivePresenter(var goLiveMvp: GoLiveMvp.MainView, val context: Context) :
    GoLiveMvp.MainPresenter {
    override fun golive(
        action: String,
        broadcast_type: String,
        broadcasting_time: String,
        idle_time: String,
        actual_broadcasting_time: String
    ) {
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient_NEW.instance.golive(
                action,
                broadcast_type,
                broadcasting_time,
                idle_time,
                actual_broadcasting_time).enqueue(object : Callback<GoliveModel> {
                override fun onFailure(call: Call<GoliveModel>, t: Throwable) {
                    Log.i("autolog", "t: " + call.request().url())
                    Log.i("autolog", "t: " + t.localizedMessage)
                    goLiveMvp.setError(t.message)
                }

                override fun onResponse(
                    call: Call<GoliveModel>,
                    response: Response<GoliveModel>
                ) {
                    Log.i("autolog", "response: " + response.raw().request().url())
                    Log.i("autolog", "response: " + response.code())
                    Log.i("autolog", "response: " + response.raw().request().headers())




                    if (response.code()==401){
                        if (Utils.isNetworkAvailableapi()) {
                            RetrofitClient.instance.auth("android")!!.enqueue(object :
                                Callback<Tokens.tokensres> {
                                override fun onFailure(call: Call<Tokens.tokensres>, t: Throwable) {
                                    Log.i("autolog", "t: " + call.request().url())
                                    goLiveMvp.setError(t.message)
                                }

                                override fun onResponse(
                                    call: Call<Tokens.tokensres>,
                                    tokenresponse: Response<Tokens.tokensres>
                                ) {
                                    Log.i("autolog", "tokenresponse: " + tokenresponse.raw().request().url())
                                    if (tokenresponse.body() != null) {
                                        val genericResponse: Tokens.tokensres? = tokenresponse.body()
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        SessionUser.saveToken(genericResponse!!.body.token)
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                        Log.i("autolog", "bearer: " + SessionUser.user.activation_code + ":" + SessionUser.token)
                                        Log.i("autolog", "tokens: " + SessionUser.user.activation_code+SessionUser.token)
                                        golive(action, broadcast_type, broadcasting_time, idle_time, actual_broadcasting_time)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                    } else {
                                        goLiveMvp.setError(tokenresponse.errorBody().toString())
                                    }
                                }
                            })
                        } else {
                            goLiveMvp.setError("No Netwrok Connection")
                        }

                    }else{
                        if (response.body() != null) {
                            val genericResponse: GoliveModel? = response.body()
                            Log.i(
                                "autolog",
                                "genericResponse: " + genericResponse!!.message
                            )
                            goLiveMvp.success(genericResponse.status,genericResponse.message)
                        } else {
                            goLiveMvp.setError(response.errorBody().toString())
                        }
                    }
                }
            })
        } else {
            goLiveMvp.setError("No Netwrok Connection")
        }

    }
    override fun getprofile(
        action: String,
        user_id: String
    ) {
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient_NEW.instance.getprofile(
                action,
                user_id).enqueue(object : Callback<BroadProfileModel> {
                override fun onFailure(call: Call<BroadProfileModel>, t: Throwable) {
                    Log.i("autolog", "t: " + call.request().url())
                    goLiveMvp.setError(t.message)
                }

                override fun onResponse(
                    call: Call<BroadProfileModel>,
                    response: Response<BroadProfileModel>
                ) {
                    Log.i("autolog", "response: " + response.raw().request().url())
                    Log.i("autolog", "response: " + response.code())
                    Log.i("autolog", "response: " + response.raw().request().headers())




                    if (response.code()==401){
                        if (Utils.isNetworkAvailableapi()) {
                            RetrofitClient.instance.auth("android")!!.enqueue(object :
                                Callback<Tokens.tokensres> {
                                override fun onFailure(call: Call<Tokens.tokensres>, t: Throwable) {
                                    Log.i("autolog", "t: " + call.request().url())
                                    goLiveMvp.setError(t.message)
                                }

                                override fun onResponse(
                                    call: Call<Tokens.tokensres>,
                                    tokenresponse: Response<Tokens.tokensres>
                                ) {
                                    Log.i("autolog", "tokenresponse: " + tokenresponse.raw().request().url())
                                    if (tokenresponse.body() != null) {
                                        val genericResponse: Tokens.tokensres? = tokenresponse.body()
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        SessionUser.saveToken(genericResponse!!.body.token)
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                        Log.i("autolog", "bearer: " + SessionUser.user.activation_code + ":" + SessionUser.token)
                                        Log.i("autolog", "tokens: " + SessionUser.user.activation_code+SessionUser.token)
                                        getprofile(action, user_id)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                    } else {
                                        goLiveMvp.setError(tokenresponse.errorBody().toString())
                                    }
                                }
                            })
                        } else {
                            goLiveMvp.setError("No Netwrok Connection")
                        }

                    }else{
                        if (response.body() != null) {
                            val genericResponse: BroadProfileModel? = response.body()
                            Log.i(
                                "autolog",
                                "genericResponse: " + genericResponse!!.body
                            )
                            goLiveMvp.getprofilesuccess(genericResponse.status,genericResponse.message,genericResponse.body)
                        } else {
                            goLiveMvp.setError(response.errorBody().toString())
                        }
                    }
                }
            })
        } else {
            goLiveMvp.setError("No Netwrok Connection")
        }

    }

    override fun getaudience(action: String, page: String, length: String, user_id: String) {
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient_NEW.instance.getAudience(action,length,page,user_id).enqueue(object : Callback<AudienceModel> {
                override fun onFailure(call: Call<AudienceModel>, t: Throwable) {
                    Log.i("autolog", "t: " + call.request().url())
                    goLiveMvp.setError(t.message)
                }

                override fun onResponse(
                    call: Call<AudienceModel>,
                    response: Response<AudienceModel>
                ) {
                    Log.i("autolog", "response: " + response.raw().request().url())
                    Log.i("autolog", "response: " + response.code())
                    Log.i("autolog", "response: " + response.raw().request().headers())




                    if (response.code()==401){
                        if (Utils.isNetworkAvailableapi()) {
                            RetrofitClient.instance.auth("android")!!.enqueue(object :
                                Callback<Tokens.tokensres> {
                                override fun onFailure(call: Call<Tokens.tokensres>, t: Throwable) {
                                    Log.i("autolog", "t: " + call.request().url())
                                    goLiveMvp.setError(t.message)
                                }

                                override fun onResponse(
                                    call: Call<Tokens.tokensres>,
                                    tokenresponse: Response<Tokens.tokensres>
                                ) {
                                    Log.i("autolog", "tokenresponse: " + tokenresponse.raw().request().url())
                                    if (tokenresponse.body() != null) {
                                        val genericResponse: Tokens.tokensres? = tokenresponse.body()
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        SessionUser.saveToken(genericResponse!!.body.token)
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                        Log.i("autolog", "bearer: " + SessionUser.user.activation_code + ":" + SessionUser.token)
                                        Log.i("autolog", "tokens: " + SessionUser.user.activation_code+SessionUser.token)
                                        getaudience(action, page, length, user_id)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                    } else {
                                        goLiveMvp.setError(tokenresponse.errorBody().toString())
                                    }
                                }
                            })
                        } else {
                            goLiveMvp.setError("No Netwrok Connection")
                        }

                    }else{
                        if (response.body() != null) {
                            val genericResponse: AudienceModel? = response.body()
                            Log.i(
                                "autolog",
                                "genericResponse: " + genericResponse!!.body
                            )
                            goLiveMvp.getaudiencesuccess(genericResponse!!.status,genericResponse!!.message,genericResponse)
                        } else {
                            goLiveMvp.setError(response.errorBody().toString())
                        }
                    }
                }
            })
        } else {
            goLiveMvp.setError("No Netwrok Connection")
        }

    }
    override fun addguest(action: String, page: String, length: String, user_id: String) {
Log.i("autolog", "action: " + action+page+length+user_id)
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient_NEW.instance.addOrRemoveGuest(user_id,action,length,page).enqueue(object : Callback<GuestResponse> {
                override fun onFailure(call: Call<GuestResponse>, t: Throwable) {
                    Log.i("autolog", "t: " + call.request().url())
                    goLiveMvp.setError(t.message)
                }

                override fun onResponse(
                    call: Call<GuestResponse>,
                    response: Response<GuestResponse>
                ) {
                    Log.i("autolog", "response: " + response.raw().request().url())
                    Log.i("autolog", "response: " + response.code())
                    Log.i("autolog", "response: " + response.raw().request().headers())




                    if (response.code()==401){
                        if (Utils.isNetworkAvailableapi()) {
                            RetrofitClient.instance.auth("android")!!.enqueue(object :
                                Callback<Tokens.tokensres> {
                                override fun onFailure(call: Call<Tokens.tokensres>, t: Throwable) {
                                    Log.i("autolog", "t: " + call.request().url())
                                    goLiveMvp.setError(t.message)
                                }

                                override fun onResponse(
                                    call: Call<Tokens.tokensres>,
                                    tokenresponse: Response<Tokens.tokensres>
                                ) {
                                    Log.i("autolog", "tokenresponse: " + tokenresponse.raw().request().url())
                                    if (tokenresponse.body() != null) {
                                        val genericResponse: Tokens.tokensres? = tokenresponse.body()
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        SessionUser.saveToken(genericResponse!!.body.token)
                                        Log.i("autolog", "saveToken: " + SessionUser.token)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                        Log.i("autolog", "bearer: " + SessionUser.user.activation_code + ":" + SessionUser.token)
                                        Log.i("autolog", "tokens: " + SessionUser.user.activation_code+SessionUser.token)
                                        getaudience(action, page, length, user_id)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                    } else {
                                        goLiveMvp.setError(tokenresponse.errorBody().toString())
                                    }
                                }
                            })
                        } else {
                            goLiveMvp.setError("No Netwrok Connection")
                        }

                    }else{
                        if (response.body() != null) {
                            val genericResponse: GuestResponse? = response.body()
                            Log.i(
                                "autolog",
                                "genericResponse: " + genericResponse!!.body
                            )
                            if (genericResponse.status.equals("1")){
                                goLiveMvp.setError(genericResponse!!.message)
                            }else{
                                goLiveMvp.addguestsuccess(genericResponse!!.status,genericResponse!!.message,genericResponse)
                            }
                        } else {
                            goLiveMvp.setError(response.errorBody().toString())
                        }
                    }
                }
            })
        } else {
            goLiveMvp.setError("No Netwrok Connection")
        }

    }

}