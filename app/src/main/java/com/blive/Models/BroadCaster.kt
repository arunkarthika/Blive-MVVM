package com.blive.Models

import com.google.gson.annotations.SerializedName

data class BroadCaster(
    val broadcast_type: String,
    val diamond: String,
    val dpEffects: String,
    val fans: String,
    val followers: String,
    val freeGifts: String,
    val friends: String,
    val guestList: List<String>,
    val kickOutList: List<Any>,
    val level: String,
    val moon_level: String,
    val moon_value: String,
    @SerializedName("profileName") val name: String,
    val over_all_gold: String,
    val profile_pic: String,
    val reference_user_id: String,
    val status: String,
    val username: String
)
