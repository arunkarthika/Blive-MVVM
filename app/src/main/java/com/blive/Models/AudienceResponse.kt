package com.blive.Models

data class AudienceResponse(
    val audience_count: Int,
    val entranceEffect: String,
    val last_page: Int,
    val broadCastList: BroadCaster,
    val viewers_list: List<Viewers>
)