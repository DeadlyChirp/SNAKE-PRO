package tangNdam.slither;

import snek.rsrc.snek.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PlayMenu extends JFrame {

    private BufferedImage leftGameImage;
    private BufferedImage rightGameImage;
    private BufferedImage background;

    public PlayMenu() {
        initMenu();
    }

    private void initMenu() {
        // Load your images here
        leftGameImage = loadImage("src/main/java/tangNdam/slither/images/snakeplay.png");
        rightGameImage = loadImage("src/main/java/tangNdam/slither/images/slitherplay.png");
        background = loadImage("src/main/java/tangNdam/slither/images/playbg.png");

        // Set to fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true); // Remove window borders for true fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set the menu panel with the correct arguments
        PlayPanel panel = new PlayPanel(leftGameImage, rightGameImage, background);
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

    class PlayPanel extends JPanel {
        private Dimension screenSize;
        private BufferedImage leftGameImage;
        private BufferedImage rightGameImage;
        private BufferedImage background;
        public PlayPanel(BufferedImage leftImage, BufferedImage rightImage, BufferedImage background) {
            this.leftGameImage = leftImage;
            this.rightGameImage = rightImage;
            this.background = background;

            this.setLayout(null); // Set the layout to null for absolute positioning
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            // Create left and right panels
            // Assuming you want to use the full size of the images
            ImagePanel leftPanel = new ImagePanel(leftImage);
            ImagePanel rightPanel = new ImagePanel(rightImage);

            // Calculate the positions based on the dimensions of the background image
            int centerLeftX = (screenSize.width / 2 - leftImage.getWidth()) / 2; // Center of the left half
            int centerRightX = (3 * screenSize.width / 2 - rightImage.getWidth()) / 2; // Center of the right half
            int centerY = (screenSize.height - leftImage.getHeight()) / 2; // Center vertically

            // Set the bounds of the image panels based on the calculated positions
            leftPanel.setBounds(centerLeftX, centerY, leftImage.getWidth(), leftImage.getHeight());
            rightPanel.setBounds(centerRightX, centerY, rightImage.getWidth(), rightImage.getHeight());

            //launch game
            leftPanel.setClickAction(this::startSnakeGame);
            rightPanel.setClickAction(this::startSlitherGame);


            // Add the panels to the layout
            this.add(leftPanel);
            this.add(rightPanel);
        }

        private void startSnakeGame() {
            // Close the current window
            dispose();
            // Launch the Snake game
            new Snake().setVisible(true);
        }

        private void startSlitherGame() {
            // Close the current window
            dispose();
            // Launch the Slither game
            new SlitherJFrame().setVisible(true);
        }

        private void setupMouseListener(JPanel panel, Runnable action) {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    action.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background to fill the entire area of the panel
            g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    static class ImagePanel extends JPanel {
        private BufferedImage image;


        public ImagePanel(BufferedImage image) {
            this.image = image;
            this.setOpaque(false);
            this.setSize(image.getWidth(), image.getHeight());
            this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        public void setClickAction(Runnable action) {
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    action.run();
                }
            });
        }
    }
}
