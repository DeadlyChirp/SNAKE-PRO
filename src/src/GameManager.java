//adding players to the game

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Player> players;
    private SlitherModel gameModel;

    public GameManager(SlitherModel model) {
        this.gameModel = model;
        players = new ArrayList<>();
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


}
