package com.blive.Presenter

import android.content.Context
import android.util.Log
import com.blive.Models.ActiveUsers
import com.blive.Models.Tokens
import com.blive.View.Util.Retrofit.RetrofitClient
import com.blive.View.Util.Retrofit.RetrofitClient_NEW
import com.blive.View.Util.Session.SessionUser
import com.blive.View.MVP.LiveUserMvp
import com.blive.View.Util.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LiveuserPresenter(var liveUserMvp: LiveUserMvp.MainView, val context: Context) :
    LiveUserMvp.MainPresenter {
    override fun loadData(
        action: String,
        page: String,
        type: String,
        geo: String,
        country: String,
        city: String,
        length: String
    ) {
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient_NEW.instance.getactiveusers(
                action,
                page,
                type,
                geo,
                country,
                city,
                length
            ).enqueue(object : Callback<ActiveUsers.Activeusers> {
                override fun onFailure(call: Call<ActiveUsers.Activeusers>, t: Throwable) {
                    Log.i("autolog", "t: " + call.request().url())
                    liveUserMvp.setError(t.message)
                }

                override fun onResponse(
                    call: Call<ActiveUsers.Activeusers>,
                    response: Response<ActiveUsers.Activeusers>
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
                                    liveUserMvp.setError(t.message)
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
                                        liveUserMvp.settoken(genericResponse!!.body.token)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                        Log.i("autolog", "bearer: " + SessionUser.user.activation_code + ":" + SessionUser.token)
                                        Log.i("autolog", "tokens: " + SessionUser.user.activation_code+SessionUser.token)
                                        loadData(action, page, type, geo, country, city, length)
                                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                                    } else {
                                        liveUserMvp.setError(tokenresponse.errorBody().toString())
                                    }
                                }
                            })
                        } else {
                            liveUserMvp.setError("No Netwrok Connection")
                        }

                    }else{
                        if (response.body() != null) {
                            val genericResponse: ActiveUsers.Activeusers? = response.body()
                            Log.i(
                                "autolog",
                                "genericResponse: " + genericResponse!!.body.active_user_details.values
                            )
                            val valuesList: ArrayList<ActiveUsers.Datauser> =
                                ArrayList<ActiveUsers.Datauser>(genericResponse!!.body.active_user_details.values)
                            for (values in valuesList.indices){
                                Log.i(
                                    "autolog",
                                    "values: " + valuesList[values].profilePic
                                )

                            }

                            liveUserMvp.setAdapterData(valuesList)
                        } else {
                            liveUserMvp.setError(response.errorBody().toString())
                        }
                    }
                }
            })
        } else {
            liveUserMvp.setError("No Netwrok Connection")
        }

    }

    override fun gettoken() {
        if (Utils.isNetworkAvailableapi()) {
            RetrofitClient.instance.auth("android")!!.enqueue(object :
                Callback<Tokens.tokensres> {
                override fun onFailure(call: Call<Tokens.tokensres>, t: Throwable) {
                    Log.i("autolog", "t: " + call.request().url())
                    liveUserMvp.setError(t.message)
                }

                override fun onResponse(
                    call: Call<Tokens.tokensres>,
                    response: Response<Tokens.tokensres>
                ) {
                    Log.i("autolog", "response: " + response.raw().request().url())
                    if (response.body() != null) {
                        val genericResponse: Tokens.tokensres? = response.body()
                        SessionUser.saveToken(genericResponse!!.body.token)
                        liveUserMvp.settoken(genericResponse!!.body.token)
                        Log.i("autolog", "genericResponse: " + genericResponse!!.body.token)
                    } else {
                        liveUserMvp.setError(response.errorBody().toString())
                    }
                }
            })
        } else {
            liveUserMvp.setError("No Netwrok Connection")
        }
    }
}