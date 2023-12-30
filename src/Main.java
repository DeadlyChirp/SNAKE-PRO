// Config l'environnement du jeu,
//apparance de la fenêtre, taille, titre, etc.
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGame();
            }
        });
    }

    private static void createAndShowGame() {
        // Create the main game window (JFrame)
        SlitherJFrame gameFrame = new SlitherJFrame();

        // Initialize the game model and canvas
        SlitherModel gameModel = new SlitherModel(1000, 50, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 100, gameFrame);
        gameFrame.model = gameModel; // Set the model in the frame
        SlitherCanvas gameCanvas = new SlitherCanvas(gameFrame);
        gameFrame.canvas = gameCanvas; // Set the canvas in the frame

        // Set up the game manager
        GameManager gameManager = new GameManager(gameModel);
        gameManager.initializePlayers(); // Add players to the game

        // Add input listeners
        gameManager.addInputListeners(gameCanvas);

        // Pack the frame's components
        gameFrame.pack();

        // Set the default close operation and make the frame visible
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);

        // Start the game loop
        gameFrame.startGameLoop();
    }
}
