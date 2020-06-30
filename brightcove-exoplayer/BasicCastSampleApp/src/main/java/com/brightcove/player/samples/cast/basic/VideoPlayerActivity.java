package com.brightcove.player.samples.cast.basic;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.TextView;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.cast.util.CastMediaUtil;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.drm.BrightcoveMediaDrmCallback;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.android.gms.cast.MediaInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VideoPlayerActivity extends BrightcovePlayerActivity {

    public static final String INTENT_EXTRA_VIDEO_ID = "com.brightcove.player.samples.cast.basic.VideoPlayerActivity.VIDEO_ID";

    public static final String PROPS_LONG_DESCRIPTION = "long_description";
    public static final String PROPS_SHORT_DESCRIPTION = "description";

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
        Video video = Video.createVideo("https://ampas-advanced.akamaized.net//wmt:eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg5NDlPU0NBUlNCUCIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MTcyMzUyMDAsImlhdCI6MTU4MzE5MDc4MSwiaXNzIjoiQU1QQVMiLCJ3bWlkIjoiQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJCQkJCQkJCQkJCQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFBQUFBQUFBQUFBQkJCQkJCQkJCQkFBQUFBQUFBQUEiLCJ3bWlkZm10IjoiYWIiLCJ3bXZlciI6MX0.lnwT3A46vKOmpzMc4rsPSOWTioJ9_cydeBBJN8ZkMYE6mJ7il-N46IEmz_BWXgnofYUeOiHspTCclhwGNoklN9PuIu5GCJ0fSNiMnTM3sdddLUDnxmoUzanVvmWM06416JBJqN9I7mJGoF5arnoCtrnrCwK3fxQF4QaMrXmbDw2UDoe0LGqpuHtdHkzz5GELPAehlZAkENv6PSivEjZOP2_vumj7V0KGFAYJHjH8qktWtKxnEGtRE2UyRFPKSpBK421wx3C2BGn_q0uLN6mkc_QABcfAN8nV_BmwwiL9Zx4XXd6FVMRoeRHkTJKhHqNhZhi1vSxJeeDh-tDjvmxObA/20BP_BestPic_Bombshell_W_DYN_V1R0_1080_6CH_dash/20BP_BestPic_Bombshell_W_DYN_V1R0_1080_6CH-playlist.mpd", DeliveryType.DASH);
        video.getProperties().put(BrightcoveMediaDrmCallback.DEFAULT_URL, "https://wv-keyos.licensekeyserver.com/");
        Map<String, String> widevineHeaders = new HashMap<>();
        widevineHeaders.put("customdata", "PEtleU9TQXV0aGVudGljYXRpb25YTUw+PERhdGE+PEdlbmVyYXRpb25UaW1lPjIwMjAtMDYtMjkgMjI6MTU6NTguMTU1PC9HZW5lcmF0aW9uVGltZT48RXhwaXJhdGlvblRpbWU+MjAyMC0wNy0wNCAyMjoxNTo1OC4xNTU8L0V4cGlyYXRpb25UaW1lPjxVbmlxdWVJZD5hNDMzMmEyNDNkZDA0YzhlYjk2YjkzNDhjNTQ3MWQwYTwvVW5pcXVlSWQ+PFJTQVB1YktleUlkPmYyNDhlZDQ1MjQ5M2M3NzA2NWMwMzBmYTlhZjk3YjZiPC9SU0FQdWJLZXlJZD48V2lkZXZpbmVQb2xpY3kgZmxfQ2FuUGxheT0idHJ1ZSIgZmxfQ2FuUGVyc2lzdD0iZmFsc2UiIC8+PFdpZGV2aW5lQ29udGVudEtleVNwZWMgVHJhY2tUeXBlPSJIRCI+PFNlY3VyaXR5TGV2ZWw+MTwvU2VjdXJpdHlMZXZlbD48L1dpZGV2aW5lQ29udGVudEtleVNwZWM+PEZhaXJQbGF5UG9saWN5IHBlcnNpc3RlbnQ9ImZhbHNlIiAvPjxMaWNlbnNlIHR5cGU9InNpbXBsZSIgLz48L0RhdGE+PFNpZ25hdHVyZT5VNDY1aEJmdFo5QXhUSTB1VDhqUjZKQXY0TytCZ1hJT1BYdkZTMnlpUmprT3NPbURiUzByQTVPYVZVdzRLdTdrNnNNUlNkWlhFZEdTdDFVOGdMemVXU1BaRitUM2JKRXQ5S2QzdUZMeS85RkQ1akw2NWUvTS93ZTI4bkJCalU2dldLL1RraklTMG5qY1Z3QnVVOWIzV3h6cHduSnRkRmZobzhWU1RGK2JyNjhWdS9pckNzMHJ3d2xOTGw0RmhheXVkSXlFODdLemVoTXZ5R2lLMXNmckloSVp5eXlGQlVZeGljOWQ1WXJzdWJlZEN1ZnNPbkNueWxhaFNvWUF4MmFPR1RUQlRkRWJPUGxSSFcvUmJvbWhmK2dpSGY0L0xSc2VJcXJ3QmNnRm1MNHZWZ2I4bXNWYzVaZExQdG1KRkZPZWlVcGVMUzZMQ2hhbGpma3FqZUxSQWc9PTwvU2lnbmF0dXJlPjwvS2V5T1NBdXRoZW50aWNhdGlvblhNTD4=");
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

        // Initialize the android_cast_plugin.
        String url = "https://ampas-advanced.akamaized.net//wmt:eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg5NDlPU0NBUlNCUCIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MTcyMzUyMDAsImlhdCI6MTU4MzE5MDc4MSwiaXNzIjoiQU1QQVMiLCJ3bWlkIjoiQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQkJCQkJBQUFBQUJCQkJCQkJCQkJCQkJCQkFBQUFBQkJCQkJBQUFBQUJCQkJCQUFBQUFCQkJCQkJCQkJCQUFBQUFCQkJCQkFBQUFBQUFBQUFCQkJCQkFBQUFBQkJCQkJBQUFBQUFBQUFBQkJCQkJCQkJCQkFBQUFBQkJCQkJCQkJCQkJCQkJCQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUJCQkJCQUFBQUFBQUFBQUFBQUFBQkJCQkJCQkJCQkFBQUFBQUFBQUEiLCJ3bWlkZm10IjoiYWIiLCJ3bXZlciI6MX0.lnwT3A46vKOmpzMc4rsPSOWTioJ9_cydeBBJN8ZkMYE6mJ7il-N46IEmz_BWXgnofYUeOiHspTCclhwGNoklN9PuIu5GCJ0fSNiMnTM3sdddLUDnxmoUzanVvmWM06416JBJqN9I7mJGoF5arnoCtrnrCwK3fxQF4QaMrXmbDw2UDoe0LGqpuHtdHkzz5GELPAehlZAkENv6PSivEjZOP2_vumj7V0KGFAYJHjH8qktWtKxnEGtRE2UyRFPKSpBK421wx3C2BGn_q0uLN6mkc_QABcfAN8nV_BmwwiL9Zx4XXd6FVMRoeRHkTJKhHqNhZhi1vSxJeeDh-tDjvmxObA/20BP_BestPic_Bombshell_W_DYN_V1R0_1080_6CH_dash/20BP_BestPic_Bombshell_W_DYN_V1R0_1080_6CH-playlist.mpd";

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Connection Started
            }
        });
        eventEmitter.on(GoogleCastEventType.CAST_SESSION_ENDED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Connection Ended
            }
        });

        Source source = findCastableSource(video);
        
        GoogleCastComponent googleCastComponent = new GoogleCastComponent(eventEmitter, this);

        if(url != null)
        {
            JSONObject jsonObj = new JSONObject();

            // add licenseUrl
            jsonObj.put("licenseUrl", "{https://wv-keyos.licensekeyserver.com/}");

            Map<String, String> licenseHeaders = new HashMap<>();
            licenseHeaders.put("customdata", "PEtleU9TQXV0aGVudGljYXRpb25YTUw+PERhdGE+PEdlbmVyYXRpb25UaW1lPjIwMjAtMDYtMjkgMjI6MTU6NTguMTU1PC9HZW5lcmF0aW9uVGltZT48RXhwaXJhdGlvblRpbWU+MjAyMC0wNy0wNCAyMjoxNTo1OC4xNTU8L0V4cGlyYXRpb25UaW1lPjxVbmlxdWVJZD5hNDMzMmEyNDNkZDA0YzhlYjk2YjkzNDhjNTQ3MWQwYTwvVW5pcXVlSWQ+PFJTQVB1YktleUlkPmYyNDhlZDQ1MjQ5M2M3NzA2NWMwMzBmYTlhZjk3YjZiPC9SU0FQdWJLZXlJZD48V2lkZXZpbmVQb2xpY3kgZmxfQ2FuUGxheT0idHJ1ZSIgZmxfQ2FuUGVyc2lzdD0iZmFsc2UiIC8+PFdpZGV2aW5lQ29udGVudEtleVNwZWMgVHJhY2tUeXBlPSJIRCI+PFNlY3VyaXR5TGV2ZWw+MTwvU2VjdXJpdHlMZXZlbD48L1dpZGV2aW5lQ29udGVudEtleVNwZWM+PEZhaXJQbGF5UG9saWN5IHBlcnNpc3RlbnQ9ImZhbHNlIiAvPjxMaWNlbnNlIHR5cGU9InNpbXBsZSIgLz48L0RhdGE+PFNpZ25hdHVyZT5VNDY1aEJmdFo5QXhUSTB1VDhqUjZKQXY0TytCZ1hJT1BYdkZTMnlpUmprT3NPbURiUzByQTVPYVZVdzRLdTdrNnNNUlNkWlhFZEdTdDFVOGdMemVXU1BaRitUM2JKRXQ5S2QzdUZMeS85RkQ1akw2NWUvTS93ZTI4bkJCalU2dldLL1RraklTMG5qY1Z3QnVVOWIzV3h6cHduSnRkRmZobzhWU1RGK2JyNjhWdS9pckNzMHJ3d2xOTGw0RmhheXVkSXlFODdLemVoTXZ5R2lLMXNmckloSVp5eXlGQlVZeGljOWQ1WXJzdWJlZEN1ZnNPbkNueWxhaFNvWUF4MmFPR1RUQlRkRWJPUGxSSFcvUmJvbWhmK2dpSGY0L0xSc2VJcXJ3QmNnRm1MNHZWZ2I4bXNWYzVaZExQdG1KRkZPZWlVcGVMUzZMQ2hhbGpma3FqZUxSQWc9PTwvU2lnbmF0dXJlPjwvS2V5T1NBdXRoZW50aWNhdGlvblhNTD4=");
            // add license headers map
            jsonObj.put("licenseKeyHeaders", licenseHeaders);
            MediaInfo mediaInfo = CastMediaUtil.toMediaInfo(video, source, null, jsonObj);
            googleCastComponent.loadMediaInfo(mediaInfo);
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
                    return dashSource;
                }
            }
            return savedDashSource;
        }
        return null;
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