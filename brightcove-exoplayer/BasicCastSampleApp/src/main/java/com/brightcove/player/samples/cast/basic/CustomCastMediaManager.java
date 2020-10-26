package com.brightcove.player.samples.cast.basic;

import android.content.Context;
import android.util.Log;

import com.brightcove.cast.controller.BrightcoveCastMediaManager;
import com.brightcove.cast.util.CastMediaUtil;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomCastMediaManager extends BrightcoveCastMediaManager {
    private Source currentSource;
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
                currentSource = (Source) event.properties.get("source");
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
        if (this.currentVideo != null && this.currentSource != null) {
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

            mediaInfo = CastMediaUtil.toMediaInfo(this.currentVideo, this.currentSource, metadata, jsonObj);
            Log.d("CAST", "Created mediaInfo with id = " + mediaInfo.getContentId());
        }
        return mediaInfo;
    }
}
