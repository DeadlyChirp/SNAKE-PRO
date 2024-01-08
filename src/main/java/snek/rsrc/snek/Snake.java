package snek.rsrc.snek;

import javax.swing.JFrame;

// This class is responsible for setting up the main window (JFrame) of the Snake game.
public class Snake extends JFrame {

    public Snake() {
        initUI();
    }

    private void initUI() {

        add(new Board());
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen mode
        setUndecorated(true); // Optional: remove window borders for true full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle("Snake");
        pack(); // Pack the frame around the components
        setLocationRelativeTo(null); // Center the window
        setVisible(true); // Make the frame visible
    }
}

