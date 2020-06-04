package com.blive.model

/**
 * Created by yt on 2017/12/14/014.
 */
class MessageBean(
    var account: String,
    var message: String,
    var isBeSelf: Boolean,
    var isWarning: Boolean,
    var isGift: Boolean
) {
    var background = 0

}