package com.blive.View.Util.Session;

import android.content.Context;
import android.content.SharedPreferences;

import com.blive.BliveApplication;


public class SessionLogin {

    private static SharedPreferences pref = BliveApplication.getInstance().getSharedPreferences("SessionLogin", Context.MODE_PRIVATE);

    public static void saveLoginSession() {
        pref.edit().putBoolean("isValid", true).apply();
    }

    public static void clearLoginSession() {
        pref.edit().clear().apply();
    }

    public static boolean getLoginSession() {
        return pref.getBoolean("isValid", false);
    }
}

