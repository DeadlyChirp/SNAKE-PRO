package tangNdam.slither;

import snek.rsrc.snek.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
        private BufferedImage background;
        private Font gameFont;
        public PlayPanel(BufferedImage leftImage, BufferedImage rightImage, BufferedImage background) {
            this.background = background;

            this.setLayout(null); // Set the layout to null for absolute positioning
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            try {
                gameFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/java/tangNdam/slither/images/PressStart2P-Regular.ttf")).deriveFont(30f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(gameFont);
            } catch (IOException | FontFormatException e) {
                e.printStackTrace(); // It's better to print the stack trace to see the error during development
                gameFont = new Font("SansSerif", Font.BOLD, 24); // Fallback font in case the custom font fails to load
            }

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

            // Add the game title labels
            JLabel snakeClassicLabel = createGameLabel("Snake Classic", gameFont);
            JLabel futurSnakeLabel = createGameLabel("Futur Snake", gameFont);

            // Add the labels directly to the PlayPanel, not the ImagePanel
            this.add(snakeClassicLabel);
            this.add(futurSnakeLabel);

            // Set bounds for the labels. Adjust these values as needed.
            int labelYOffset = leftImage.getHeight() + 10; // adjust this to position the label correctly
            snakeClassicLabel.setBounds(centerLeftX, centerY + labelYOffset, leftImage.getWidth(), 30);
            futurSnakeLabel.setBounds(centerRightX, centerY + labelYOffset, rightImage.getWidth(), 30);

            // Add the panels to the layout
            this.add(leftPanel);
            this.add(rightPanel);
        }

        private JLabel createGameLabel(String text, Font font) {
            JLabel label = new JLabel(text, SwingConstants.CENTER);
            label.setFont(font);
            label.setForeground(Color.ORANGE);
            label.setSize(new Dimension(screenSize.width / 2, 30)); // Set a fixed size for the label
            label.setHorizontalAlignment(SwingConstants.CENTER); // Make sure the text is centered
            return label;
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
