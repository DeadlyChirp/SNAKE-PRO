package tangNdam.slither; // Use the base package of slither.io project

import snek.resources.snek.Snake;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameMenu extends JFrame {

    public GameMenu() {
        initMenu();
    }

    private void initMenu() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        JButton btnSnake = new JButton("Play Snake");
        btnSnake.addActionListener(e -> startSnakeGame());
        panel.add(btnSnake);

        JButton btnSlither = new JButton("Play Slither.io");
        btnSlither.addActionListener(e -> startSlitherGame());
        panel.add(btnSlither);

        pack();
        setTitle("Game Menu");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void startSnakeGame() {
        // Code to start the snake game
        Snake snakeGame = new Snake();
        snakeGame.setVisible(true);
        this.dispose(); // Close the menu
    }

    private void startSlitherGame() {
        SlitherJFrame slitherGame = new SlitherJFrame();
        slitherGame.setVisible(true);
        this.dispose(); // Close the menu
    }
}
