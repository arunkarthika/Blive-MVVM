package com.blive.Models

class Seat {
    var userId: String? = null
    var isClosed = false

    constructor(userId: String?) {
        this.userId = userId
    }

    constructor(isClosed: Boolean) {
        this.isClosed = isClosed
    }

}