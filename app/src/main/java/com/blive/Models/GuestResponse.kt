package com.blive.Models

data class GuestResponse(
    val body: Body,
    val message: String,
    val status: String
){
    data class Body(
        val guest_count: Int,
        val guest_list: List<Viewers>
    )
}