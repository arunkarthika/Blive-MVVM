package com.blive.Models

import com.google.gson.annotations.SerializedName

class ActiveUsers {
    data class Activeusers(val error_code: String, val body: active_user_details, val active_user_Id: List<String>)
    data class active_user_details(val active_user_details: Map<String, Datauser>)
    data class Datauser(
        @SerializedName("profileName") val name: String,
        @SerializedName("profile_pic") val profilePic: String,
        @SerializedName("viewer_count") val viewerCount: String,
        @SerializedName("user_id") val userid: String,
        @SerializedName("username") val username: String
    )
}
