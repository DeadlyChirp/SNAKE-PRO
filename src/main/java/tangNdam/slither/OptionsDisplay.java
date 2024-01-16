package tangNdam.slither;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class OptionsDisplay extends JFrame {
    private BufferedImage unmuteImage;
    private BufferedImage muteImage;
    private BufferedImage background;
    private AudioPlayer audioPlayer;



    public OptionsDisplay() {
        this.audioPlayer = new AudioPlayer("src/main/java/tangNdam/slither/images/gamemusic.mp3");
        initMenu();
    }



    public OptionsDisplay(BufferedImage unmuteImage, BufferedImage muteImage, BufferedImage background, AudioPlayer audioPlayer) {
        this.unmuteImage = unmuteImage;
        this.muteImage = muteImage;
        this.background = background;
        this.audioPlayer = audioPlayer; // Set the audio player

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OptionsPanel panel = new OptionsPanel(unmuteImage, muteImage, background, this, audioPlayer);
        setContentPane(panel);

        setVisible(true);
    }

    public OptionsDisplay(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        initMenu();
    }

    private void initMenu() {
        unmuteImage = loadImage("src/main/java/tangNdam/slither/images/unmute1.png");
        muteImage = loadImage("src/main/java/tangNdam/slither/images/mute1.png");
        background = loadImage("src/main/java/tangNdam/slither/images/bgopt.png");

        if (this.audioPlayer == null) {
            this.audioPlayer = new AudioPlayer("src/main/java/tangNdam/slither/images/gamemusic.mp3"); // Update with correct path
        }

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OptionsPanel panel = new OptionsPanel(unmuteImage, muteImage, background, this, audioPlayer);
        setContentPane(panel);
        setVisible(true);
    }

    private BufferedImage loadImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }

    class OptionsPanel extends JPanel {
        private BufferedImage unmuteImage;
        private BufferedImage muteImage;
        private BufferedImage background;
        private OptionsDisplay parentFrame;

        private boolean isUnmuted = true; // Initial state is unmuted
        private AudioPlayer audioPlayer;
        private ImagePanel unmutePanel; // Declare as instance variable
        private ImagePanel mutePanel;
        public OptionsPanel(BufferedImage unmuteImage, BufferedImage muteImage, BufferedImage background, OptionsDisplay parentFrame, AudioPlayer audioPlayer) {
            this.audioPlayer = audioPlayer;
            this.unmuteImage = unmuteImage;
            this.muteImage = muteImage;
            this.background = background;
            this.parentFrame = parentFrame;

            setLayout(null); // Set the layout to null for absolute positioning

            // Create the unmute and mute image panels
            unmutePanel = new ImagePanel(unmuteImage);
            mutePanel = new ImagePanel(muteImage);

            // Calculate positions based on screen size and image dimensions
            int spaceBetween = 50; // Space between buttons
            int panelWidth = unmuteImage.getWidth() + spaceBetween + muteImage.getWidth();
            int startX = (getWidth() - panelWidth) / 2; // To center the buttons together

            int unmutePanelX = startX;
            int mutePanelX = startX + unmuteImage.getWidth() + spaceBetween;

            // Assuming you want to center the buttons vertically as well
            int panelY = (getHeight() - unmuteImage.getHeight()) / 2;

            // Now use setBounds to position the image panels
            unmutePanel.setBounds(unmutePanelX, panelY, unmuteImage.getWidth(), unmuteImage.getHeight());
            mutePanel.setBounds(mutePanelX, panelY, muteImage.getWidth(), muteImage.getHeight());

            // Add the image panels to this OptionsPanel
            add(unmutePanel);
            add(mutePanel);

            // Create and add the return button
            BufferedImage returnButtonImage = loadImage("src/main/java/tangNdam/slither/images/backbtn.png");
            ImageIcon returnIcon = new ImageIcon(returnButtonImage);

            JButton returnButton = new JButton(returnIcon);
            returnButton.setBorder(BorderFactory.createEmptyBorder());
            returnButton.setContentAreaFilled(false);
            returnButton.setFocusPainted(false);
            returnButton.addActionListener(e -> {
                parentFrame.dispose();
                new GameMenu().setVisible(true);
            });

            returnButton.setBounds(10, 10, returnIcon.getIconWidth(), returnIcon.getIconHeight());
            add(returnButton);

            // Add a mouse listener to the unmute and mute panels
            unmutePanel.setClickAction(() -> {
                if (!isUnmuted) {
                    audioPlayer.playMusic(); // Start music when currently muted
                    isUnmuted = true;
                }
                repaint();
            });

// Add a mouse listener to the mute panel
            mutePanel.setClickAction(() -> {
                if (isUnmuted) {
                    audioPlayer.stopMusic(); // Stop music when currently unmuted
                    isUnmuted = false;
                }
                repaint();
            });

            addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    updateButtonPositions();
                }
            });
        }



        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);


        }

        private void updateButtonPositions() {
            int spaceBetween = 50; // Space between buttons
            int panelWidth = unmuteImage.getWidth() + spaceBetween + muteImage.getWidth();
            int startX = (getWidth() - panelWidth) / 2; // To center the buttons together

            int unmutePanelX = startX;
            int mutePanelX = startX + unmuteImage.getWidth() + spaceBetween;
            int panelY = (getHeight() - unmuteImage.getHeight()) / 2;

            // Set bounds for the image panels
            unmutePanel.setBounds(unmutePanelX, panelY, unmuteImage.getWidth(), unmuteImage.getHeight());
            mutePanel.setBounds(mutePanelX, panelY, muteImage.getWidth(), muteImage.getHeight());
        }

    }

    static class ImagePanel extends JPanel {
        private BufferedImage image;
        private Runnable clickAction;

        public ImagePanel(BufferedImage image) {
            this.image = image;
            setOpaque(false);
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (clickAction != null) {
                        clickAction.run(); // Execute the click action when clicked
                    }
                }
            });
        }

        public void setClickAction(Runnable action) {
            this.clickAction = action;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
