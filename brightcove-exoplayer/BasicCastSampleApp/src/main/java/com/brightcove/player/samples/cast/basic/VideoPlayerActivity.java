package com.brightcove.player.samples.cast.basic;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.cast.util.CastMediaUtil;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.drm.BrightcoveMediaDrmCallback;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.ViewCompat;

public class VideoPlayerActivity extends BrightcovePlayerActivity {

    public static final String INTENT_EXTRA_VIDEO_ID = "com.brightcove.player.samples.cast.basic.VideoPlayerActivity.VIDEO_ID";

    public static final String PROPS_LONG_DESCRIPTION = "long_description";
    public static final String PROPS_SHORT_DESCRIPTION = "description";
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
    public static String AUDIO_SPEC_LIST = "audioSpecList";
    public static String AMPAS_CUSTOM_CHANNEL = "urn:x-cast:bc.cast.theacademy";

    public static String videoUrl = "https://ampas-advanced.akamaized.net/wmt:eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg5NDlPU0NBUlNCUCIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MTcyMzUyMDAsImlhdCI6MTU4MzE5MDc4MSwiaXNzIjoiQU1QQVMiLCJ3bWlkIjoiQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJCQkJCQkJCQkJCQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFBQUFBQUFBQUFBQkJCQkJCQkJCQkFBQUFBQUFBQUEiLCJ3bWlkZm10IjoiYWIiLCJ3bXZlciI6MX0.lnwT3A46vKOmpzMc4rsPSOWTioJ9_cydeBBJN8ZkMYE6mJ7il-N46IEmz_BWXgnofYUeOiHspTCclhwGNoklN9PuIu5GCJ0fSNiMnTM3sdddLUDnxmoUzanVvmWM06416JBJqN9I7mJGoF5arnoCtrnrCwK3fxQF4QaMrXmbDw2UDoe0LGqpuHtdHkzz5GELPAehlZAkENv6PSivEjZOP2_vumj7V0KGFAYJHjH8qktWtKxnEGtRE2UyRFPKSpBK421wx3C2BGn_q0uLN6mkc_QABcfAN8nV_BmwwiL9Zx4XXd6FVMRoeRHkTJKhHqNhZhi1vSxJeeDh-tDjvmxObA/Da5Bloods_5min_TestClip_W_UHD_6CH_SixTrack_2398_ProResHQ_dash/12192k-eac3/b.Da5Bloods_5min_TestClip_W_UHD_6CH_SixTrack_2398_ProResHQ-eac3-12192k.mpd";
    public static String videoKey = "PEtleU9TQXV0aGVudGljYXRpb25YTUw+PERhdGE+PEdlbmVyYXRpb25UaW1lPjIwMjAtMDktMzAgMTc6NDA6MDYuNDE3PC9HZW5lcmF0aW9uVGltZT48RXhwaXJhdGlvblRpbWU+MjAyMC0xMC0wNyAxNzo0MDowNi40MTc8L0V4cGlyYXRpb25UaW1lPjxVbmlxdWVJZD42Zjc2OTNhNWE4M2Y0YjEwOWFlZDE0MWNkMjlhY2UzYzwvVW5pcXVlSWQ+PFJTQVB1YktleUlkPmYyNDhlZDQ1MjQ5M2M3NzA2NWMwMzBmYTlhZjk3YjZiPC9SU0FQdWJLZXlJZD48V2lkZXZpbmVQb2xpY3kgZmxfQ2FuUGxheT0idHJ1ZSIgZmxfQ2FuUGVyc2lzdD0iZmFsc2UiIC8+PFdpZGV2aW5lQ29udGVudEtleVNwZWMgVHJhY2tUeXBlPSJIRCI+PFNlY3VyaXR5TGV2ZWw+MTwvU2VjdXJpdHlMZXZlbD48L1dpZGV2aW5lQ29udGVudEtleVNwZWM+PEZhaXJQbGF5UG9saWN5IHBlcnNpc3RlbnQ9ImZhbHNlIiAvPjxMaWNlbnNlIHR5cGU9InNpbXBsZSIgLz48L0RhdGE+PFNpZ25hdHVyZT5kNUgrSkY5L1M2NXhpTGE5NzBaZmc4UzdKMUhrWkc1RWtPZnJOQUE1aEhGbEFXUG8zenRoUnpWTUFiYTNTQU5nQ1IxS3Q3YWVoWVI1NWxTd0kwcUoxckdVWGZaYWc5OG5xanllWDZUckowc1A0OGNyd3JPblhJWDFOUGRMMGVKRUovblhYL0lPMFFpV1BWdERUWGh4b3pKUEVGV2c4WHBYSWNZVURvRm0zSlcxRkxaTk8reUVTcStWVlJTNTJQZXcrci9jaHFQMG56bU9jS2pmb1BYMWNBd0ZsOVdyMWdLaTF1L2tjMWV5bUNSbkJQZndZM3BlWlhpM2RNT1ZmM1NxWHNGY0NBNDlsMlpjVzloMnJabkFIbG9WS0YwT2liWnJoNE1tNFNxUk04NVNNMFZmUDRNbzJrUThPNEZsdktvS0VnMmhSNFpFK2FYNUZzdUUreTZRY2c9PTwvU2lnbmF0dXJlPjwvS2V5T1NBdXRoZW50aWNhdGlvblhNTD4=";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
//        baseVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        baseVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        ViewCompat.setTransitionName(baseVideoView, getString(R.string.transition_image));

//        String videoId = getIntent().getStringExtra(VideoPlayerActivity.INTENT_EXTRA_VIDEO_ID);
//        Catalog catalog = new Catalog(baseVideoView.getEventEmitter(), getString(R.string.account), getString(R.string.policy));
//        catalog.findVideoByID(videoId, new VideoListener() {
//            @Override
//            public void onVideo(Video video) {
//
//                String title = video.getName();
//                if (!TextUtils.isEmpty(title)) {
//                    TextView textView = findViewById(R.id.video_title_text);
//                    textView.setText(title);
//                }
//
//                Object descriptionObj = video.getProperties().get(PROPS_LONG_DESCRIPTION);
//                if (descriptionObj instanceof String) {
//                    TextView longDesc = findViewById(R.id.video_description_text);
//                    longDesc.setText((String) descriptionObj);
//                }
//
//
//                baseVideoView.add(video);
//
//                setUpCast(video);
//
//            }
//        });

        //FW thing
        Video video = Video.createVideo(videoUrl, DeliveryType.DASH);
        video.getProperties().put(BrightcoveMediaDrmCallback.DEFAULT_URL, "https://wv-keyos.licensekeyserver.com/");
        Map<String, String> widevineHeaders = new HashMap<>();
        widevineHeaders.put("customdata", videoKey);
        video.getProperties().put(Video.Fields.WIDEVINE_HEADERS, widevineHeaders);
        baseVideoView.add(video);
        try {
            setUpCast(video);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        baseVideoView.start();
    }

    void setUpCast(Video video) throws JSONException {
        EventEmitter eventEmitter = baseVideoView.getEventEmitter();
        GoogleCastComponent googleCastComponent = new GoogleCastComponent(eventEmitter, this);

        // Initialize the android_cast_plugin.
        String url = videoUrl;

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // listen for messages from the receiver
                if (googleCastComponent.isSessionAvailable() &&
                    CastContext.getSharedInstance().getSessionManager() != null &&
                    CastContext.getSharedInstance().getSessionManager().getCurrentCastSession()
                        != null
                ) {
                    CastSession castSession = CastContext.getSharedInstance().getSessionManager().getCurrentCastSession();
                    try {
                        castSession.setMessageReceivedCallbacks(AMPAS_CUSTOM_CHANNEL, (castDevice, namespace, message) -> {
                        Log.d("CAST",
                            "received message in namespace '" + namespace + "': " + message);
                    });
                    } catch(IOException e){
                     Log.e("CAST",
                          "IOException sending cast message: " + e.getLocalizedMessage());
                    }
                }
            }
        });
        eventEmitter.on(GoogleCastEventType.CAST_SESSION_ENDED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Connection Ended
            }
        });

        Source source = findCastableSource(video);

        if(url != null)
        {
            JSONObject jsonObj = new JSONObject();

            // add licenseUrl
            jsonObj.put("licenseUrl", "https://wv-keyos.licensekeyserver.com/");

            JSONObject licenseHeaders = new JSONObject();
            licenseHeaders.put("customdata", videoKey);
            // add license headers map
            jsonObj.put("licenseKeyHeaders", licenseHeaders);
            MediaInfo mediaInfo = CastMediaUtil.toMediaInfo(video, source, null, jsonObj);
            googleCastComponent.loadMediaInfo(mediaInfo);

            // send available codecs to receiver for 5.1 audio support
            if (googleCastComponent.isSessionAvailable() &&
                CastContext.getSharedInstance().getSessionManager() != null &&
                CastContext.getSharedInstance().getSessionManager().getCurrentCastSession() != null
            ) {
                CastSession castSession = CastContext.getSharedInstance().getSessionManager().getCurrentCastSession();

                // send message to the receiver with available codecs
                JSONObject messageObject = createCodecsMessage(source);
                castSession.sendMessage(AMPAS_CUSTOM_CHANNEL, messageObject.toString());
                Log.d("CAST", "sent the following message to the receiver: \n" + messageObject.toString());
            }
        }
        else
        {
            MediaInfo mediaInfo = CastMediaUtil.toMediaInfo(video, source, null, null);
            googleCastComponent.loadMediaInfo(mediaInfo);
        }

        //You can check if there is a session available
        googleCastComponent.isSessionAvailable();
    }


    public static Source findCastableSource(Video video) {
        Source savedDashSource = null;

        if (!video.getSourceCollections().isEmpty()
                && video.getSourceCollections().containsKey(DeliveryType.DASH)
                && video.getSourceCollections().get(DeliveryType.DASH).getSources() != null) {
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
            messageObject.put(AUDIO_SPEC_LIST, availableCodecs);
        } catch (Exception e) {
            Log.e("CAST", "Exception adding codecs array to mesageObject: " + e.getLocalizedMessage());
        }
        return messageObject;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        GoogleCastComponent.setUpMediaRouteButton(this, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        EventEmitter eventEmitter = baseVideoView.getEventEmitter();
        ActionBar actionBar = getSupportActionBar();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            eventEmitter.emit(EventType.EXIT_FULL_SCREEN);
            if (actionBar != null) {
                actionBar.show();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            eventEmitter.emit(EventType.ENTER_FULL_SCREEN);
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }
}