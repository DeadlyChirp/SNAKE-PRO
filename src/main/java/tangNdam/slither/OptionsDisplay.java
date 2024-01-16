package tangNdam.slither;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class OptionsDisplay extends JFrame {
    private BufferedImage unmuteImage;
    private BufferedImage muteImage;
    private BufferedImage background;
    private AudioPlayer audioPlayer;

    public OptionsDisplay() {
        initMenu();
    }

    public OptionsDisplay(BufferedImage unmuteImage, BufferedImage muteImage, BufferedImage background) {
        this.unmuteImage = unmuteImage;
        this.muteImage = muteImage;
        this.background = background;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OptionsPanel panel = new OptionsPanel(unmuteImage, muteImage, background, this);
        setContentPane(panel);

        setVisible(true);
    }

    private void initMenu() {
        unmuteImage = loadImage("src/main/java/tangNdam/slither/images/unmute.png");
        muteImage = loadImage("src/main/java/tangNdam/slither/images/mute.png");
        background = loadImage("src/main/java/tangNdam/slither/images/bgopt.png");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OptionsPanel panel = new OptionsPanel(unmuteImage, muteImage, background, this);
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
        private boolean isMusicPlaying = true; // Initial state is playing
        private boolean isUnmuted = true; // Initial state is unmuted

        public OptionsPanel(BufferedImage unmuteImage, BufferedImage muteImage, BufferedImage background, OptionsDisplay parentFrame) {
            this.unmuteImage = unmuteImage;
            this.muteImage = muteImage;
            this.background = background;
            this.parentFrame = parentFrame;

            setLayout(null); // Set the layout to null for absolute positioning

            // Create the unmute and mute image panels
            ImagePanel unmutePanel = new ImagePanel(unmuteImage);
            ImagePanel mutePanel = new ImagePanel(muteImage);

            // Calculate positions based on screen size and image dimensions
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            int unmutePanelX = centerX - unmuteImage.getWidth() / 2; // Center horizontally
            int unmutePanelY = centerY - unmuteImage.getHeight() / 2; // Center vertically

            int mutePanelX = centerX + unmuteImage.getWidth() / 2 + 10; // Adjust as needed
            int mutePanelY = centerY - muteImage.getHeight() / 2; // Center vertically

            // Set bounds for the image panels
            unmutePanel.setBounds(unmutePanelX, unmutePanelY, unmuteImage.getWidth(), unmuteImage.getHeight());
            mutePanel.setBounds(mutePanelX, mutePanelY, muteImage.getWidth(), muteImage.getHeight());

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
                if (isUnmuted) {
                    audioPlayer.stopMusic(); // Stop music when unmute is clicked
                    isUnmuted = false;
                } else {
                    audioPlayer.playMusic(); // Start music when mute is clicked
                    isUnmuted = true;
                }
                repaint();
            });

            mutePanel.setClickAction(() -> {
                if (isUnmuted) {
                    audioPlayer.playMusic(); // Start music when mute is clicked
                    isUnmuted = false;
                } else {
                    audioPlayer.stopMusic(); // Stop music when unmute is clicked
                    isUnmuted = true;
                }
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image to fill the entire panel
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

            // Draw the appropriate image (unmute or mute) based on the current state
            BufferedImage currentImage = isUnmuted ? unmuteImage : muteImage;
            g.drawImage(currentImage, (getWidth() - currentImage.getWidth()) / 2, (getHeight() - currentImage.getHeight()) / 2, this);
        }
    }

    static class ImagePanel extends JPanel {
        private BufferedImage image;
        private Runnable clickAction;

        public ImagePanel(BufferedImage image) {
            this.image = image;
            setOpaque(false);
            setSize(image.getWidth(), image.getHeight());
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }

        public void setClickAction(Runnable action) {
            this.clickAction = action;
        }
    }
}
