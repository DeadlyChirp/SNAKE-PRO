//adding players to the game

import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class GameManager {
    private List<Player> players;
    private SlitherModel gameModel;
    private static final Set<Integer> pressedKeys = new HashSet<>();
    private static Point mousePosition = new Point(0, 0);
    private static boolean mouseClicked = false;

    public GameManager(SlitherModel model) {
        this.gameModel = model;
        players = new ArrayList<>();

        // ajout des keylistener et mouselistener
        // gamePanel.addKeyListener(this);
        // gamePanel.addMouseListener(this);
        // gamePanel.addMouseMotionListener(this);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }


    // Appelée pour initialiser les joueurs
    public void initializePlayers() {
        // Ici, vous pouvez ajouter la logique pour permettre aux joueurs de choisir leur mode de contrôle
        // Exemple: créer un joueur avec contrôle clavier ou souris en fonction de la sélection de l'utilisateur
        Player player1 = getUserSelectedControlType("Player 1"); // Méthode fictive pour obtenir le type de contrôle
        Player player2 = getUserSelectedControlType("Player 2");

        addPlayer(player1);
        addPlayer(player2);
    }

    // Méthode fictive pour illustrer la sélection du type de contrôle par l'utilisateur
    private Player getUserSelectedControlType(String playerName) {
        // Implémentez la logique pour que l'utilisateur choisisse le type de contrôle (clavier ou souris)
        // Retournez une instance de PlayerKeyboard ou PlayerMouse en fonction de la sélection
        return new Player.PlayerKeyboard(playerName); // Placeholder
    }
    public void updateGame() {
        for (Player player : players) {
            player.action(gameModel);
            // Ajoutez ici la logique de mise à jour du jeu pour chaque joueur
        }
        // Autres mises à jour du jeu
    }

    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
    }


    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }


    public void mousePressed(MouseEvent e) {
        mouseClicked = true;
    }


    public void mouseReleased(MouseEvent e) {
        mouseClicked = false;
    }


    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
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


}
