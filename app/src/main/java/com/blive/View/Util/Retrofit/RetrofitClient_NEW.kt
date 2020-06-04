package com.blive.View.Util.Retrofit

import android.util.Base64
import android.util.Log
import com.blive.BuildConfig
import com.blive.View.Util.Session.SessionUser
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient_NEW {

    var credentials: String = SessionUser.bearertoken + ":" + SessionUser.token
//    var credentials: String = "b138dfcf8ed6"+ ":" + "9498840ec33b4c52a737"
    // create Base64 encodet string
    var basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            credentials = SessionUser.bearertoken + ":" + SessionUser.token
            basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            Log.i("autolog", "credentials: " + credentials)

            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", basic)
                .addHeader("Accept", "application/json")
                .method(original.method(), original.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()
    val gson = GsonBuilder()
        .setLenient()
        .create()
    val instance: Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.api_host_name)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        retrofit.create(Api::class.java)
    }


}