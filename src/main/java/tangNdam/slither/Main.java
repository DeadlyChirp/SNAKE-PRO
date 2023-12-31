package tangNdam.slither;

import java.awt.EventQueue;

public class Main {

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            tangNgdam.slither.GameMenu menu = new tangNgdam.slither.GameMenu();
            menu.setVisible(true);
        });
    }
}
