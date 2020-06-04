package com.blive.View.Util.Retrofit

import com.blive.BuildConfig
import com.blive.View.Util.Session.SessionUser
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient{

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer " + SessionUser.bearertoken)
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