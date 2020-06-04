package com.blive.Models

data class ListUsers(
    val body: listDetails,
    val message: String,
    val status: Int
){
    data class listDetails(
        val audience: AudienceResponse,
        val guest: GuestResponse.Body,
        val last_page: Int,
        val friends: ArrayList<Details>,
        val followers: ArrayList<Details>,
        val fans: ArrayList<Details>,
        val giftList: GiftResponse,
        val blocked: ArrayList<Details>
    ){
        data class Details(
            val dpEffects: String,
            val over_all_gold: String,
            val profileName: String,
            val profile_pic: String,
            val user_id: String,
            val username: String,
            val level: String
        )
    }
}
