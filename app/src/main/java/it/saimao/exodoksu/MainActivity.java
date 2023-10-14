package it.saimao.exodoksu;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Button btNext, btPrev, btPlay;
    private MediaController mediaController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btNext = findViewById(R.id.btNext);
        btNext.setOnClickListener(view -> mediaController.seekToNextMediaItem());
        btPrev = findViewById(R.id.btPrev);
        btPrev.setOnClickListener(view -> mediaController.seekToPreviousMediaItem());
        btPlay = findViewById(R.id.btPlay);
        btPlay.setOnClickListener(view -> {
            if (mediaController.isPlaying()) {
                mediaController.pause();
                btPlay.setText(R.string.play);
            } else {
                mediaController.play();
                btPlay.setText(R.string.pause);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                mediaController = controllerFuture.get();
                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        System.out.println(playbackState);
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());

    }
}
