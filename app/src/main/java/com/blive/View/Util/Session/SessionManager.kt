package com.blive.View.Util.Session

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Created by sans on 9/27/2019.
 */
/**
 * Created by CDS121 on 06-07-2018.
 */
class SessionManager(private val mContext: Context) {
    var PRIVATE_MODE = 0
    fun getSessionStringValue(
        sessionName: String,
        sessionKey: String?
    ): String? {
        val settings = getSession(sessionName)
        // Reading String value from SharedPreferences
        Log.w("Success", "String::: " + settings.getString(sessionKey, null))
        return settings.getString(sessionKey, "0")
    }

    private fun getSession(sessionName: String): SharedPreferences {
        val PRIVATE_MODE = 0
        return mContext.getSharedPreferences(sessionName, PRIVATE_MODE)
    }

    fun storeSessionStringvalue(
        sessionName: String,
        key: String?,
        value: String?
    ) {
        val settings = getSession(sessionName)
        // Writing String data to SharedPreferences
        val editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

}