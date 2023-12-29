import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GameManager {
    private List<Player> players;
    private SlitherModel gameModel;
    private static final Set<Integer> pressedKeys = new HashSet<>();
    private static Point mousePosition = new Point(0, 0);
    private static boolean mouseClicked = false;

    public GameManager(SlitherModel model) {
        this.gameModel = model;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void initializePlayers() {
        // Initialize players with their control schemes
        Player player1 = new Player.PlayerKeyboard("Player 1");
        Player player2 = new Player.PlayerMouse("Player 2");

        addPlayer(player1);
        addPlayer(player2);
    }

    public void updateGame() {
        for (Player player : players) {
            player.update(gameModel); // This calls update on Player, handling both keyboard and mouse
        }
        // ... other game logic
    }


    public static boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    public static Point getMousePosition() {
        return mousePosition;
    }

    public static boolean isMouseClicked() {
        return mouseClicked;
    }

    // Keyboard and Mouse listeners to track input
    public class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pressedKeys.add(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            pressedKeys.remove(e.getKeyCode());
        }
    }

    public class GameMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            mouseClicked = true;
            mousePosition = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseClicked = false;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mousePosition = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mousePosition = e.getPoint();
        }
    }

    // Implement a method to add listeners to your game canvas or panel
    public void addInputListeners(Component component) {
        GameKeyListener keyListener = new GameKeyListener();
        GameMouseListener mouseListener = new GameMouseListener();

        component.addKeyListener(keyListener);
        component.addMouseListener(mouseListener);
        component.addMouseMotionListener(mouseListener);

        // Ensure the component can be focused to receive key events
        component.setFocusable(true);
        component.requestFocusInWindow();
    }
}

// Modify Player classes if necessary to adapt them to the GameManager's static methods
// Ensure the Player.Keyboard and Player.Mouse classes are correctly using the GameManager's static methods
