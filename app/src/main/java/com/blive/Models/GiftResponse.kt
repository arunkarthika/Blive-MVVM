package com.blive.Models

data class GiftResponse(
//    val free_list: GiftListX,
    val gift_list: GiftListX
//    val tool_list: GiftListX
){
    data class GiftListX(
        val all: ArrayList<Gift>,
        val group: List<String>,
        val pk: List<String>,
        val solo: List<String>
    )
}