package com.blive.View.Activity;



import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.blive.BliveApplication;
import com.blive.BuildConfig;
import com.blive.View.Util.Agora.ConstantApp;
import com.blive.View.Util.Agora.EngineConfig;
import com.blive.View.Util.Agora.MyEngineEventHandler;
import com.blive.View.Util.Agora.WorkerThread;
import com.blive.View.Util.Utils;
import com.google.firebase.FirebaseApp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


import io.agora.rtc.RtcEngine;




public abstract class BaseActivity extends AppCompatActivity {
    private final static Logger log = LoggerFactory.getLogger(BaseActivity.class);
    public Activity mActivity;
    public Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = BaseActivity.this;
        utils = new Utils(mActivity);
        View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                initUIandEvent();
            }
        });
    }

    protected abstract void initUIandEvent();

    protected abstract void deInitUIandEvent();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                boolean checkPermissionResult = checkSelfPermissions();

                if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
                    // so far we do not use OnRequestPermissionsResultCallback
                }
            }
        }, 500);
    }

    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onDestroy() {
        deInitUIandEvent();
        super.onDestroy();
    }

    public final void closeIME(View v) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0); // 0 force close IME
        v.clearFocus();
    }

    public final void closeIMEWithoutFocus(View v) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0); // 0 force close IME
    }

    public void openIME(final EditText v) {
        final boolean focus = v.requestFocus();
        if (v.hasFocus()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    boolean result = mgr.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                    log.debug("openIME " + focus + " " + result);
                }
            });
        }
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        log.debug("checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }

        if (Manifest.permission.CAMERA.equals(permission)) {
            ((BliveApplication) getApplication()).initWorkerThread();
        }
        return true;
    }

    protected RtcEngine rtcEngine() {
        return ((BliveApplication) getApplication()).getWorkerThread().getRtcEngine();
    }

    protected final WorkerThread worker() {
        return ((BliveApplication) getApplication()).getWorkerThread();
    }

    protected final EngineConfig config() {
        return ((BliveApplication) getApplication()).getWorkerThread().getEngineConfig();
    }

    protected final MyEngineEventHandler event() {
        return ((BliveApplication) getApplication()).getWorkerThread().eventHandler();
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        log.debug("onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(grantResults));
        switch (requestCode) {
            case ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, ConstantApp.PERMISSION_REQ_ID_CAMERA);
                } else {
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
                    ((BliveApplication) getApplication()).initWorkerThread();
                } else {
                    finish();
                }
                break;
            }
            case ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    finish();
                }
                break;
            }
        }
    }
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }


    protected void initVersionInfo() {
        String version = "V " + BuildConfig.VERSION_NAME + "(Build: " + BuildConfig.VERSION_CODE
                + ", " + ConstantApp.APP_BUILD_DATE + ", SDK: " + RtcEngine.getSdkVersion() + ")";
//        TextView textVersion = (TextView) findViewById(R.id.app_version);
//        textVersion.setText(version);
    }
}
