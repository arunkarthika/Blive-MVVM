package com.blive.Models

import com.google.gson.annotations.SerializedName

class Country {
    @SerializedName("code")
    var code: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("dialCode")
    var dialCode: String? = null

    override fun toString(): String {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dialCode='" + dialCode + '\'' +
                '}'
    }
}