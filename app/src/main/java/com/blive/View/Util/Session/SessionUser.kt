package com.blive.View.Util.Session

import android.content.Context
import com.blive.BliveApplication
import com.blive.Models.Gift
import com.blive.Models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SessionUser {
    companion object {
        private val pref =
            BliveApplication.getInstance().getSharedPreferences("SessionUser", Context.MODE_PRIVATE)


        fun saveLoginSession() {
          pref.edit().putBoolean("isValid", true).apply()
        }

        fun clearLoginSession() {
            pref.edit().clear().apply()
        }

        fun getLoginSession(): Boolean {
            return pref.getBoolean("isValid", false)
        }

        val userData: User
            get() {
                val gson = Gson()
                val json = pref.getString("user", "")
                return gson.fromJson(
                    json,
                    User::class.java
                )
            }

        fun saveUserNew(user: User?) {
            val gson = Gson()
            val json = gson.toJson(user)
            pref.edit().putString("user", json).apply()
        }

        fun saveUser(user: User?) {
            val gson = Gson()
            val json = gson.toJson(user)
            pref.edit().putString("user", json).apply()
        }

        fun saveGiftList(list: ArrayList<Gift>) {
            val editor = pref.edit()
            val gson = Gson()
            val json = gson.toJson(list)
            editor.putString("gift", json)
            editor.apply()
        }

        fun getGiftList(): ArrayList<Gift> {
            val gson = Gson()
            val json = pref.getString("gift", null)
            val type: Type = object : TypeToken<ArrayList<Gift?>?>() {}.type
            return gson.fromJson(json, type)
        }

        val user: User
            get() {
                val gson = Gson()
                val json = pref.getString("user", "")
                return gson.fromJson(
                    json,
                    User::class.java
                )
            }

        fun isScreenSharing(isScreenSHaring: Boolean) {
            pref.edit().putBoolean("isScreenSharing", isScreenSHaring).apply()
        }

        val isScreenSharing: Boolean
            get() = pref.getBoolean("isScreenSharing", false)

        fun saveToken(token: String?) {
            pref.edit().putString("token", token).apply()
        }
        fun saveBearerToken(token: String?) {
            pref.edit().putString("bearertoken", token).apply()
        }


        val token: String?
            get() = pref.getString("token", "0")
        val bearertoken: String?
            get() = pref.getString("bearertoken", "0")

        fun clearUserSession() {
            pref.edit().clear().apply()
        }

        fun isRtmLoggedIn(isLoggedIn: Boolean) {
            pref.edit().putBoolean("isRTMLoggedIn", isLoggedIn).apply()
        }

        val rtmLoginSession: Boolean
            get() = pref.getBoolean("isRTMLoggedIn", false)
    }


}