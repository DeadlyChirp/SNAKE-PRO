import javax.swing.*;
import java.awt.*;

// Graphique du jeu
//dessin les entités du jeu, arriere plan, nourriture, etc.
// mecanique de jeu : zoom, deplacement, etc.
public class SlitherCanvas extends JPanel { // JPanel est une classe de Swing
    int screenWidth;
    private int screenHeight;

    // Constructor
    public SlitherCanvas() {
        // Mettre par defaut la taille de l'ecran
        this.screenWidth = 800; // Example width
        this.screenHeight = 600; // Example height

        // Mettre par defaut la taille du canvas
        setPreferredSize(new Dimension(screenWidth, screenHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Mis a jour de la taille de l'ecran
        screenWidth = getWidth();
        screenHeight = getHeight();

        //logique de dessin du jeu
        // ...
    }

    public double getScreenWidth() {
        return screenWidth;
    }
    public double getScreenHeight() {
        return screenHeight;
    }
}
