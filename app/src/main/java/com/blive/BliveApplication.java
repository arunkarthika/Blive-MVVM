package com.blive;


import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.blive.View.Util.Agora.RTM.ChatManager;
import com.blive.View.Util.Agora.WorkerThread;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;

public class BliveApplication extends Application {
    private final String TAG = BliveApplication.class.getSimpleName();
    private static BliveApplication mInstance;
    private WorkerThread mWorkerThread;
    public static final String CHANNEL_ID = "BLiveApplication";
    private GoogleSignInClient mGoogleSignInClient;
    private static Activity activity = null;
    private ChatManager mChatManager;


    private FirebaseAnalytics mFirebaseAnalytics;
    public static BliveApplication getInstance() {
        return mInstance;
    }
    public ChatManager getChatManager() {
        return mChatManager;
    }

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
    public static void setCurrentActivity(Activity mCurrentActivity) {
        activity = mCurrentActivity;
    }
    public static Context getStaticContext() {
        return BliveApplication.getInstance().getApplicationContext();
    }

    public static Activity getCurrentActivity(){
        return activity;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        Firebase.setAndroidContext(this);
        mChatManager = new ChatManager(this);
        mChatManager.init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel();
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //setupAgoraEngine();
        mInstance = this;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "BLive Application",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public GoogleSignInClient getGoogleSignInClient(){ return mGoogleSignInClient; }

}
