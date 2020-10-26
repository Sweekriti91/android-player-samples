package com.brightcove.player.samples.cast.basic;

import android.content.Context;
import android.util.Log;

import com.brightcove.cast.controller.BrightcoveCastMediaManager;
import com.brightcove.cast.util.CastMediaUtil;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomCastMediaManager extends BrightcoveCastMediaManager {
    public static Map<String, String> CODEC_JSON_LIST;
    static {
        CODEC_JSON_LIST = new HashMap<>();
        CODEC_JSON_LIST.put("mp4a.40.2", "{\"mimeType\": \"audio/mp4\", \"codecs\": \"mp4a.40.2\"}");
        CODEC_JSON_LIST.put("ac-3", "{\"mimeType\": \"audio/mp4\", \"codecs\": \"ac-3\"}");
        CODEC_JSON_LIST.put("mp4a.a5", "{\"mimeType\": \"audio/mp4\", \"codecs\": \"mp4a.a5\"}");
        CODEC_JSON_LIST.put("mp4a.a6", "{\"mimeType\": \"audio/mp4\", \"codecs\": \"mp4a.a6\"}");
        CODEC_JSON_LIST.put("ec-3", "{\"mimeType\": \"audio/mp4\", \"codecs\": \"ec-3\"}");
        CODEC_JSON_LIST.put("mhm1.0x0D", "{\"mimeType\": \"audio/mp4\", \"codecs\": \"mhm1.0x0D\"}");
    }

    private Source castableSource;
    private Video currentVideo;
    private String videoKey;

    public CustomCastMediaManager(Context context, EventEmitter eventEmitter, String videoKey) {
        super(context, eventEmitter);
        this.videoKey = videoKey;

        //fetch the current video and source from the eventEmitter
        eventEmitter.on(EventType.SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                currentVideo = (Video) event.properties.get("video");
                if (currentVideo != null) {
                    castableSource = findCastableSource(currentVideo);
                }
                Log.d("CAST", "cast controller video and source set");
            }
        });
    }

    // override addMediaInfo to use our createMediaInfo logic
    @Override
    protected void addMediaInfo() {
        if (this.isSessionAvailable()) {
            MediaInfo mediaInfo = this.createMediaInfo();
            if (mediaInfo != null) {
                this.addMediaInfo(mediaInfo);
            } else {
                Log.e("CAST", "Media Queue Item is null");
            }

        }
    }


    // override loadMediaInfo to use our createMediaInfo logic
    @Override
    protected void loadMediaInfo() {
        if (this.isSessionAvailable()) {
            MediaInfo mediaInfo = this.createMediaInfo();
            if (mediaInfo != null) {
                this.updateBrightcoveMediaController(true);
                Log.d("CAST", "Loading Media info with the following structure: " + mediaInfo.toJson());
                this.loadMediaInfo(mediaInfo);
                sendCodecsMessage();
            } else {
                Log.e("CAST", "Media Queue Item is null");
            }

        }
    }

    /**
     * this function creates a MediaInfo object from video source and adds the required custom data for licenseHeaders
     *
     * @return MediaInfo object
     */
    private MediaInfo createMediaInfo() {
        MediaInfo mediaInfo = null;
        if (currentVideo != null && castableSource != null) {
            MediaMetadata metadata = new MediaMetadata();
            metadata.putString(MediaMetadata.KEY_SUBTITLE, this.currentVideo.getDescription());
            metadata.putString(MediaMetadata.KEY_TITLE, this.currentVideo.getName());

            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject();
                // add licenseUrl
                jsonObj.put("licenseUrl", "https://wv-keyos.licensekeyserver.com/");

                JSONObject licenseHeaders = new JSONObject();
                licenseHeaders.put("customdata", videoKey);
                // add license headers map
                jsonObj.put("licenseKeyHeaders", licenseHeaders);
            } catch (JSONException e) {
                Log.e("CAST", "Failed to create custom JSONObject", e);
            }

            mediaInfo = CastMediaUtil.toMediaInfo(currentVideo, castableSource, metadata, jsonObj);
            Log.d("CAST", "Created mediaInfo with id = " + mediaInfo.getContentId());
        }
        return mediaInfo;
    }

    private void sendCodecsMessage() {
        // send available codecs to receiver for 5.1 audio support
        if (this.isSessionAvailable() &&
            CastContext.getSharedInstance().getSessionManager() != null &&
            CastContext.getSharedInstance().getSessionManager().getCurrentCastSession() != null
        ) {
            CastSession castSession = CastContext.getSharedInstance().getSessionManager().getCurrentCastSession();

            // send message to the receiver with available codecs
            JSONObject messageObject = createCodecsMessage(castableSource);
            castSession.sendMessage(VideoPlayerActivity.AMPAS_CUSTOM_CHANNEL, messageObject.toString());
            Log.d("CAST", "sent the following message to the receiver: \n" + messageObject.toString());
        }
    }

    public static Source findCastableSource(Video video) {
        Source savedDashSource = null;

        if (!video.getSourceCollections().isEmpty()
            && video.getSourceCollections().containsKey(DeliveryType.DASH)
            && video.getSourceCollections().get(DeliveryType.DASH) != null) {
            for (Source dashSource : video.getSourceCollections().get(DeliveryType.DASH).getSources()) {
                savedDashSource = dashSource;
                if (dashSource.getUrl().contains("ac-3_avc1_ec-3_mp4a")) {
                    // prefer 5.1 dash source
                    return dashSource;
                }
            }
            return savedDashSource;
        }
        return null;
    }

    public static JSONObject createCodecsMessage(Source source) {
        JSONObject jsonObject = new JSONObject();
        String sourceUrl = source.getUrl();

        JSONArray availableCodecs = new JSONArray();
        for (String key : CODEC_JSON_LIST.keySet()) {
            if (sourceUrl.contains(key)) {
                try {
                    availableCodecs.put(new JSONObject(CODEC_JSON_LIST.get(key)));
                } catch (Exception e) {
                    Log.e("CAST", "Exception adding codec to JSONArray: " + e.getLocalizedMessage());
                }
            }
        }
        JSONObject messageObject = new JSONObject();
        try {
            messageObject.put(VideoPlayerActivity.AUDIO_SPEC_LIST, availableCodecs);
        } catch (Exception e) {
            Log.e("CAST", "Exception adding codecs array to mesageObject: " + e.getLocalizedMessage());
        }
        return messageObject;
    }
}
