package tangNdam.slither;

import java.awt.EventQueue;
import java.io.File;
import java.net.URL;

public class Main {
    private static AudioPlayer player;

    public static void main(String[] args) {



        EventQueue.invokeLater(() -> {
            GameMenu menu = new GameMenu();
            menu.setVisible(true);
        });
    }
}
