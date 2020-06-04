package com.blive.View.Util.Agora;


import android.content.Context;


import com.blive.Models.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.agora.rtc.video.VideoEncoderConfiguration;

public class ConstantApp {
    public static final String APP_BUILD_DATE = "today";

    public static final int BASE_VALUE_PERMISSION = 0X0001;
    public static final int PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1;
    public static final int PERMISSION_REQ_ID_CAMERA = BASE_VALUE_PERMISSION + 2;
    public static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3;

    public static VideoEncoderConfiguration.VideoDimensions VIDEO_DIMENSION = VideoEncoderConfiguration.VD_640x360;

    public static int CALL_IN = 0x01;
    public static int CALL_OUT = 0x02;
    public static int UNKNOWN = -99;
    public static VideoEncoderConfiguration.VideoDimensions[] VIDEO_DIMENSIONS = new VideoEncoderConfiguration.VideoDimensions[] {
            VideoEncoderConfiguration.VD_160x120,
            VideoEncoderConfiguration.VD_320x180,
            VideoEncoderConfiguration.VD_320x240,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            VideoEncoderConfiguration.VD_1280x720
    };


    public static class PrefManager {
        public static final String PREF_PROPERTY_PROFILE_IDX = "pref_profile_index";
        public static final String PREF_PROPERTY_UID = "pOCXx_uid";
    }

    public static final String ACTION_KEY_CHANNEL_NAME = "ecHANEL";
    public static final String ACTION_KEY_UID = "ecUID";
    public static final String ACTION_KEY_SUBSCRIBER = "exSubscriber";
    public static final String ACTION_KEY_REMOTE_INVITATION = "exSurEMOTE_INVITATIONr";
    public static final String ACTION_KEY_MakeOrReceive = "ecxxMakeOrRece";
    public static final int DEFAULT_PROFILE_IDX = 4; // default use 480P

    public static class AppError {
        public static final int NO_NETWORK_CONNECTION = 3;
    }

    public ArrayList<Country> getCountry(Context context) {

        ArrayList<Country> countryList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            JSONArray m_jArry = obj.getJSONArray("countryCode");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String countryDialCode = jo_inside.getString("dial_code");
                String countryCode = jo_inside.getString("code");
                String countryName = jo_inside.getString("name");
                Country country = new Country();
                country.setCode(countryCode);
                country.setName(countryName);
                country.setDialCode(countryDialCode);
                countryList.add(country);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return countryList;
    }

    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("countrycode.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
