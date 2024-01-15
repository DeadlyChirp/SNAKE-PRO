package snek.rsrc.snek;

import javax.swing.JFrame;
import java.awt.*;

// This class is responsible for setting up the main window (JFrame) of the Snake game.
public class Snake extends JFrame {

    public Snake() {
        initUI();
    }

    private void initUI() {
        Board board = new Board();
        add(board, BorderLayout.CENTER);
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Maximize the window
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        setVisible(true);
    }


}

