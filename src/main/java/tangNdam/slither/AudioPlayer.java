package tangNdam.slither;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {
    private Player mp3Player;
    private String mp3FilePath;

    public AudioPlayer(String mp3FilePath) {
        this.mp3FilePath = mp3FilePath;
        initializePlayer();
    }

    private void initializePlayer() {
        try {
            InputStream is = new FileInputStream(mp3FilePath);
            mp3Player = new Player(is);
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }
    }

    public void playMusic() {
        if (mp3Player == null) {
            initializePlayer();
        }
        new Thread(() -> {
            try {
                if (mp3Player != null) {
                    mp3Player.play();
                }
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopMusic() {
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
    }

    // Adjust the volume (this method is currently not implemented for JLayer Player)
    public void setVolume(float level) {
        // Implement volume control if possible
    }

    public void cleanup() {
        if (mp3Player != null) {
            stopMusic();
        }
    }
}
