package com.blive.View.Util.Retrofit

import com.blive.Models.*
import retrofit2.Call
import retrofit2.http.*


interface Api {
    @POST("system/auth")
    fun auth( @Query("device") device: String?): Call<Tokens.tokensres>?

    @GET("user")
    fun getactiveusers(@Query("action") action: String?, @Query("page") page: String?, @Query("type") type: String?, @Query("geo") geo: String?, @Query("country") country: String?, @Query("city") city: String?, @Query("length") length: String?): Call<ActiveUsers.Activeusers>

    @GET("user")
    fun getprofile(@Query("action") action: String?, @Query("user_id") user_id: String?): Call<BroadProfileModel>

    @FormUrlEncoded
    @POST("user/goLive")
    fun golive(@Field("action") action: String?, @Field("broadcast_type") broadcast_type: String?, @Field("broadcasting_time") broadcasting_time: String?, @Field("idle_time") idle_time: String?, @Field("actual_broadcasting_time") actual_broadcasting_time: String?): Call<GoliveModel>

    @FormUrlEncoded
    @POST("user/audiance")
    fun getAudience(@Field("action") action: String?, @Field("length") length: String?, @Field("page") page: String?, @Field("user_id") user_id: String?): Call<AudienceModel>

    @GET("system/check")
    fun login(@Query("login_domain") action: String?, @Query("email") user_id: String?, @Query("mobile") page: String?, @Query("device_id") type: String?): Call<UserModel>

    @FormUrlEncoded
    @POST("system/register")
        fun register(@Field("name") name: String?,
                 @Field("username") userName: String?,
                 @Field("gender") gender: String?,
                 @Field("login_domain") loginDomain: String?,
                 @Field("email") Email: String?,
                 @Field("mobile") mobile: String?,
                 @Field("device_id") deviceId: String?,
                 @Field("referral") referral: String?,
                 @Field("gcm_registration_id") gcmRegistrationId: String?,
                 @Field("profile_pic") profilePic: String?): Call<Register>

    @FormUrlEncoded
    @POST("user/userRelation")
    fun userRelation(@Field("user_id")
                     userId: String?,@Field("action") action: String?): Call<GoliveModel>
    @FormUrlEncoded
    @POST("user/guest")
    fun addOrRemoveGuest(@Field("user_id")
                     userId: String?,@Field("action") action: String?, @Field("length") length: String?, @Field("page") page: String?): Call<GuestResponse>

    @GET("user/List")
    fun userList(@Query("user_id") userId: String?, @Query("action") action: String?,
                       @Query("page") page: String?, @Query("length") length: String?):
                        Call<ListUsers>

    @GET("user/search")
    fun search(@Query("gender") action: String?, @Query("searchTerm") page: String?,
                 @Query("page") type: String?, @Query("length") geo: String?):
            Call<ActiveUsers.Activeusers>

    @FormUrlEncoded
    @POST("user/updateProfile")
    fun updateProfile(@Field("name") name: String?,
                 @Field("gender") gender: String?,
                 @Field("country") country: String?,
                 @Field("state") state: String?,
                 @Field("city") city: String?,
                 @Field("dob") dob: String?,
                 @Field("reference_id") referenceId: String?,
                 @Field("bio") bio: String?,
                 @Field("profile_pic") profilePic: String?,
                 @Field("profile_pic1") profilePic1: String?,
                 @Field("profile_pic2") profilePic2: String?): Call<Register>
}