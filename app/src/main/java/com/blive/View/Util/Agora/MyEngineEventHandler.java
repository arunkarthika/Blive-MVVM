package com.blive.View.Util.Agora;

import android.content.Context;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import static io.fabric.sdk.android.Fabric.TAG;


public class MyEngineEventHandler {
    private static final String LOG_TAG = "EnginHandler";

    private final ConcurrentHashMap<AGEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap<>();

    public void addEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.put(handler, 0);
    }

    public void removeEventHandler(AGEventHandler handler) {
        this.mEventHandlerList.remove(handler);
    }

/*
    final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            Log.i(LOG_TAG, "onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);

            for (AGEventHandler handler : mEventHandlerList.keySet()) {
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            }
        }

        @Override
        public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
            Log.i(LOG_TAG, "onFirstRemoteVideoFrame " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
            Log.i(LOG_TAG, "onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Log.i(LOG_TAG, "onUserJoined " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            for (AGEventHandler handler : mEventHandlerList.keySet()) {
                handler.onUserJoined(uid, elapsed);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            for (AGEventHandler handler : mEventHandlerList.keySet()) {
                handler.onUserOffline(uid, reason);
            }
        }

        @Override
        public void onUserMuteVideo(int uid, boolean muted) {
        }

        @Override
        public void onRtcStats(RtcStats stats) {
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            Log.i(LOG_TAG, "onLeaveChannel " + stats);
        }

        @Override
        public void onLastmileQuality(int quality) {
            Log.i(LOG_TAG, "onLastmileQuality " + quality);

            for (AGEventHandler handler : mEventHandlerList.keySet()) {
                handler.onLastmileQuality(quality);
            }
        }

        @Override
        public void onError(int err) {
            super.onError(err);

            Log.i(LOG_TAG, "onError " + err);
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.i(LOG_TAG, "onJoinChannelSuccess " + channel + " " + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            for (AGEventHandler handler : mEventHandlerList.keySet()) {
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }


            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }

        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.i(LOG_TAG, "onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
        }

        public void onWarning(int warn) {
            for (AGEventHandler handler : mEventHandlerList.keySet()) {
                handler.onExtraCallback(warn,"");
            }

            Log.i(LOG_TAG, "onWarning " + warn);

        }
    };
*/

    final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
            log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
            }
        }

        @Override
        public void onFirstLocalVideoFrame(int width, int height, int elapsed) {
            log.debug("onFirstLocalVideoFrame " + width + " " + height + " " + elapsed);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Log.i(LOG_TAG, "onUserJoined " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            for (AGEventHandler handler : mEventHandlerList.keySet()) {
                handler.onUserJoined(uid, elapsed);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);

            // FIXME this callback may return times
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onUserOffline(uid, reason);
            }
        }

        @Override
        public void onUserMuteVideo(int uid, boolean muted) {
            log.debug("onUserMuteVideo " + (uid & 0xFFFFFFFFL) + " " + muted);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_MUTED, uid, muted);
            }
        }

        @Override
        public void onRtcStats(RtcStats stats) {
        }

        @Override
        public void onRemoteVideoStats(RemoteVideoStats stats) {
            log.debug("onRemoteVideoStats " + stats.uid + " " + stats.receivedBitrate + " " + stats.rendererOutputFrameRate + " " + stats.width + " " + stats.height);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_STATS, stats);
            }
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakerInfos, int totalVolume) {
            if (speakerInfos == null) {
                // quick and dirty fix for crash
                // TODO should reset UI for no sound
                return;
            }

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS, (Object) speakerInfos);
            }
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {

        }



        @Override
        public void onLastmileQuality(int quality) {
            log.debug("onLastmileQuality " + quality);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onLastmileQuality(quality);
            }
        }

        @Override
        public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {
            log.debug("onLastmileProbeResult " + result);
            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onLastmileProbeResult(result);
            }
        }

        @Override
        public void onError(int error) {
            log.debug("onError " + error + " " + RtcEngine.getErrorDescription(error));

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR, error, RtcEngine.getErrorDescription(error));
            }
        }

        @Override
        public void onConnectionLost() {
            log.debug("onConnectionLost");

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_APP_ERROR, ConstantApp.AppError.NO_NETWORK_CONNECTION);
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            log.debug("onJoinChannelSuccess " + channel + " " + uid + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            /*mConfig.mUid = uid;*/

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }

        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            log.debug("onRejoinChannelSuccess " + channel + " " + uid + " " + elapsed);
        }

        @Override
        public void onAudioRouteChanged(int routing) {
            log.debug("onAudioRouteChanged " + routing);

            Iterator<AGEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                AGEventHandler handler = it.next();
                handler.onExtraCallback(AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED, routing);
            }
        }

        public void onWarning(int warn) {
            log.debug("onWarning " + warn);
        }
    };

}
