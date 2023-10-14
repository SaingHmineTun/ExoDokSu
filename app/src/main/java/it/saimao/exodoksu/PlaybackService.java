package it.saimao.exodoksu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CommandButton;
import androidx.media3.session.DefaultMediaNotificationProvider;
import androidx.media3.session.MediaNotification;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.MediaStyleNotificationHelper;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class PlaybackService extends MediaSessionService {

    private final int[] mediaItemsID = {R.raw.tmk1, R.raw.tmk2, R.raw.tmk3};
    private final String[] mediaItemsTitle = {"ၵႃႈပၼ်ႇၵွင်ႊ", "ၵႂၢမ်းၶွပ်ႈၸႂ်", "ၵႂၢမ်းၸူမ်းပီႈၼွင်ႉ"};
    private List<MediaItem> allMediaItems;
    private MediaSession mediaSession;
    private ExoPlayer exoPlayer;

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exoPlayer = new ExoPlayer.Builder(this).build();
        initAllMediaItems();
        setupNotificationDesign();
        mediaSession = new MediaSession.Builder(this, exoPlayer).build();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupNotificationDesign() {
        setMediaNotificationProvider(new MediaNotification.Provider() {
            @Override
            public MediaNotification createNotification(MediaSession mediaSession, ImmutableList<CommandButton> customLayout, MediaNotification.ActionFactory actionFactory, Callback onNotificationChangedCallback) {
                createNoti(mediaSession);
                return new MediaNotification(NOTIFICATION_ID, nBuilder.build());
            }

            @Override
            public boolean handleCustomCommand(MediaSession session, String action, Bundle extras) {
                return false;
            }
        });
    }

    private final String notificationId = "sai_mao";
    private final int NOTIFICATION_ID = 2846;
    private NotificationCompat.Builder nBuilder;
    private NotificationManager notificationManager;

    @OptIn(markerClass = UnstableApi.class)
    private void createNoti(MediaSession mediaSession) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(notificationId, "channel", NotificationManager.IMPORTANCE_LOW));

        final Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.noti_bg);
        nBuilder = new NotificationCompat.Builder(this, notificationId)
                .setSmallIcon(R.drawable.noti)
                .setLargeIcon(decodeResource)
                .setStyle(new MediaStyleNotificationHelper.MediaStyle(mediaSession));
    }

    @Override @OptIn(markerClass = UnstableApi.class)
    public void  onUpdateNotification(MediaSession session, boolean startInForegroundRequired) {
        super.onUpdateNotification(session, startInForegroundRequired);
        int currentIndex = exoPlayer.getCurrentMediaItemIndex();
        nBuilder.setContentTitle(mediaItemsTitle[currentIndex]);
        notificationManager.notify(NOTIFICATION_ID, nBuilder.build());
    }

    @OptIn(markerClass = UnstableApi.class)
    public void initAllMediaItems() {
        if (allMediaItems == null) {
            allMediaItems = new ArrayList<>();
            for (int buildRawResourceUri : mediaItemsID) {
                MediaItem mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(buildRawResourceUri));
                allMediaItems.add(mediaItem);
            }
        }
        exoPlayer.setMediaItems(allMediaItems);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
}
