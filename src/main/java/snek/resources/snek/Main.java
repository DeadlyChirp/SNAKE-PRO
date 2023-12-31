package snek.resources.snek;

import snek.resources.snek.Snake;

import java.awt.EventQueue;
import javax.swing.JFrame;

// This class contains the main method to run the Snake game.
public class Main {

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame ex = new Snake();
            ex.setVisible(true);
        });
    }
}
