package tangNdam.slither;

import java.awt.EventQueue;
import java.io.File;
import java.net.URL;

public class Main {
    private static AudioPlayer player;

    public static void main(String[] args) {
        try {
            // Assuming the file is located at this path relative to the project root
            String filepath = "src/main/java/tangNdam/slither/images/gamemusic.mp3";
            File audioFile = new File(filepath).getAbsoluteFile();
            player = new AudioPlayer(audioFile.getAbsolutePath());
            player.playMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            GameMenu menu = new GameMenu();
            menu.setVisible(true);
        });
    }
}
