package tangNdam.slither;


import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class GameMenu extends JFrame {

    public GameMenu() {
        initMenu();
    }

    private void initMenu() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Set a default size or use pack() after adding components
        setLocationRelativeTo(null); // Center the window

        MainMenuPanel mainMenuPanel = new MainMenuPanel();
        setContentPane(mainMenuPanel);
        setVisible(true);
    }

    static class MainMenuPanel extends JPanel {
        private JButton playButton;
        private JButton settingsButton;
        private JButton quitButton;

        public MainMenuPanel() {
             setLayout(new BorderLayout());

            // Attempt to load the background GIF
            URL backgroundUrl = getClass().getResource("/menusrc/menubg.gif");
            if (backgroundUrl == null) {
                System.err.println("Failed to load background image.");
                return; // Don't proceed further if the background can't be loaded
            }
            ImageIcon backgroundImageIcon = new ImageIcon(backgroundUrl);

            JLabel backgroundLabel = new JLabel(backgroundImageIcon);
            add(backgroundLabel);
            backgroundLabel.setLayout(new BorderLayout());

            // Attempt to load the logo GIF
            URL logoUrl = getClass().getResource("/menusrc/logogame.gif");
            if (logoUrl == null) {
                System.err.println("Failed to load logo image.");
                return; // Don't proceed further if the logo can't be loaded
            }
            ImageIcon logoImageIcon = new ImageIcon(logoUrl);

            JLabel logoLabel = new JLabel(logoImageIcon, SwingConstants.CENTER);
            backgroundLabel.add(logoLabel, BorderLayout.NORTH);

            // Add the buttons panel in the center under the logo
            JPanel buttonsPanel = createButtonsPanel();
            backgroundLabel.add(buttonsPanel, BorderLayout.CENTER);

            // Force a refresh of the panel to display the newly added components
            revalidate();
            repaint();
        }



        private JPanel createButtonsPanel() {
            JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // for layout with rows, 1 column, hgap, vgap
            buttonsPanel.setOpaque(false); // Make panel transparent

            playButton = new JButton("Play");
            settingsButton = new JButton("Settings");
            quitButton = new JButton("Quit");

            // Add button actions
            playButton.addActionListener(e -> showGameSelection());
            settingsButton.addActionListener(e -> showSettings());
            quitButton.addActionListener(e -> System.exit(0));

            buttonsPanel.add(playButton);
            buttonsPanel.add(settingsButton);
            buttonsPanel.add(quitButton);

            return buttonsPanel;
        }

        private void showGameSelection() {
            // Implement the method to show the game selection menu
        }

        private void showSettings() {
            // Implement the method to show the settings menu
        }
    }
}
