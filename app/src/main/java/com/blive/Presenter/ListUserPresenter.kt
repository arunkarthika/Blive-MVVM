package com.blive.Presenter

import android.content.Context
import android.util.Log
import com.blive.Models.*
import com.blive.View.MVP.ListViewMVP
import com.blive.View.Util.Retrofit.RetrofitClient
import com.blive.View.Util.Retrofit.RetrofitClient_NEW
import com.blive.View.Util.Session.SessionUser
import com.blive.View.Util.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListUserPresenter(var listViewMVP: ListViewMVP.MainView, val context: Context) : ListViewMVP.MainPresenter {
    override fun userList(userId: String, action: String, page: String, length: String) {
        if (Utils.isNetworkAvailableapi()) RetrofitClient_NEW.instance.userList(userId, action, page, length).enqueue(object : Callback<ListUsers> {
            override fun onFailure(call: Call<ListUsers>, t: Throwable) {
                Log.i("autolog", "t: " + call.request().url())
                Log.i("autolog", "t: " + t.localizedMessage)
                listViewMVP.setError(t.message)
            }

            override fun onResponse(call: Call<ListUsers>, response: Response<ListUsers>) {
                Log.i("autolog", "response: " + response.raw().request().url())
                Log.i("autolog", "response: " + response.code())
                Log.i("autolog", "response: " + response.raw().request().headers())

                if (response.code() == 401) {
                    if (Utils.isNetworkAvailableapi()) {
                        RetrofitClient.instance.auth("android")!!.enqueue(object :
                            Callback<Tokens.tokensres> {
                            override fun onFailure(call: Call<Tokens.tokensres>, t: Throwable) {
                                Log.i("autolog", "t: " + call.request().url())
                                listViewMVP.setError(t.message)
                            }

                            override fun onResponse(
                                call: Call<Tokens.tokensres>,
                                tokenresponse: Response<Tokens.tokensres>
                            ) {
                                Log.i(
                                    "autolog",
                                    "tokenresponse: " + tokenresponse.raw().request().url()
                                )
                                if (tokenresponse.body() != null) {
                                    val genericResponse: Tokens.tokensres? =
                                        tokenresponse.body()

                                    SessionUser.saveToken(genericResponse!!.body.token)
                                    Log.i("autolog", "saveToken: " + SessionUser.token)
                                    Log.i("autolog", "saveToken: " + SessionUser.token)
                                    Log.i(
                                        "autolog",
                                        "genericResponse: " + genericResponse!!.body.token
                                    )
                                    Log.i(
                                        "autolog",
                                        "bearer: " + SessionUser.user.activation_code + ":" + SessionUser.token
                                    )
                                    Log.i(
                                        "autolog",
                                        "tokens: " + SessionUser.user.activation_code + SessionUser.token
                                    )
                                    userList(userId, action, page, length)
                                    Log.i(
                                        "autolog",
                                        "genericResponse: " + genericResponse!!.body.token
                                    )
                                } else {
                                    listViewMVP.setError(tokenresponse.errorBody().toString())
                                }
                            }
                        })
                    } else {
                        listViewMVP.setError("No Netwrok Connection")
                    }

                } else {
                    if (response.body() != null) {
                        Log.i("autolog", "response: " + response.body()!!.status)
                        if (response.body()!!.status.equals(1)){
                            Log.i("autolog", "response: " + response.body()!!.status)
                            listViewMVP.setError(response.body()!!.message)
                        }else{
                            Log.i("autolog", "response: " + response.body()!!.status)
Log.i("autolog", "response: " + response.body())


                            listViewMVP.setAdapterData(response.body()!!.body)
                        }
                    } else {
                        listViewMVP.setError(response.errorBody().toString())
                    }
                }
            }
        }) else {
            listViewMVP.setError("No Netwrok Connection")
        }
    }
}