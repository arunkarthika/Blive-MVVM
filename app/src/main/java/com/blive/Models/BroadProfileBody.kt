package com.blive.Models

data class BroadProfileBody(
    val audienceList: List<AudienceResponse>,
    val broadcast_type: String,
    val current_gold_value: String,
    val diamond: String,
    val dpEffects: String,
    val fans: String,
    val followers: String,
    val friends: String,
    val guestList: List<Any>,
    val is_this_user_blocked: String,
    val kickOutList: List<Any>,
    val level: String,
    val name: String,
    val over_all_gold: String,
    val profile_pic: String,
    val reference_user_id: String,
    val status: String,
    val text_muted: String,
    val userRelationship: Int,
    val username: String
)