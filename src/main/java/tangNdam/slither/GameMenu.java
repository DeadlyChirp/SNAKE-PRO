package tangNdam.slither;

import snek.resources.snek.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameMenu extends JFrame {
    private static final String GAME_NAME = "Retro Games";
    private BufferedImage snakeImage;
    private BufferedImage slitherImage;

    public GameMenu() {
        initMenu();
    }

    private void initMenu() {
        // Load your images here
        snakeImage = loadImage("src/main/java/tangNdam/slither/images/snakegame.png");
        slitherImage = loadImage("src/main/java/tangNdam/slither/images/slithergame.png");

        // Set to fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(GAME_NAME);

        MenuPanel panel = new MenuPanel();
        add(panel);

        pack();
        setLocationRelativeTo(null);
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

    private void startSnakeGame() {
        // Code to start the snake game
        dispose();
        new Snake().setVisible(true);
    }

    private void startSlitherGame() {
        // Code to start the slither.io game
        dispose();
        new SlitherJFrame().setVisible(true);
    }

    class MenuPanel extends JPanel {
        private boolean onSnakeSide;
        private final int headerHeight = 100;

        public MenuPanel() {
            int panelWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 2 / 3);
            int panelHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
            setPreferredSize(new Dimension(panelWidth, panelHeight));

            // Scale images to fit the new panel size
            snakeImage = scaleImage(snakeImage, panelWidth / 2, panelHeight);
            slitherImage = scaleImage(slitherImage, panelWidth / 2, panelHeight);

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    onSnakeSide = e.getX() < getWidth() / 2;
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onSnakeSide) {
                        startSnakeGame();
                    } else {
                        startSlitherGame();
                    }
                }
            });
        }

        private BufferedImage scaleImage(BufferedImage original, int width, int height) {
            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.drawImage(original, 0, 0, width, height, null);
            g2d.dispose();
            return scaledImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight() - headerHeight; // Adjust height for the header

            // Draw the header area
            g.setColor(Color.BLACK); // Background color for the header
            g.fillRect(0, 0, width, headerHeight); // Draw the header

            // Set the font and color for the game name
            g.setFont(new Font("Retro", Font.BOLD, 48));
            g.setColor(Color.WHITE);

            // Calculate the position for the game name and draw it
            FontMetrics fm = g.getFontMetrics();
            int x = (width - fm.stringWidth(GAME_NAME)) / 2;
            int y = fm.getAscent() + (headerHeight - fm.getHeight()) / 2;
            g.drawString(GAME_NAME, x, y);

            // Draw the game images below the header
            BufferedImage leftImage = onSnakeSide ? snakeImage : createGrayscaleImage(snakeImage);
            BufferedImage rightImage = onSnakeSide ? createGrayscaleImage(slitherImage) : slitherImage;
            g.drawImage(leftImage, 0, headerHeight, width / 2, height, this);
            g.drawImage(rightImage, width / 2, headerHeight, width / 2, height, this);
        }


        private BufferedImage createGrayscaleImage(BufferedImage source) {
            BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < source.getHeight(); y++) {
                for (int x = 0; x < source.getWidth(); x++) {
                    int rgba = source.getRGB(x, y);
                    Color col = new Color(rgba, true);
                    int grayLevel = (int) (0.299 * col.getRed() + 0.587 * col.getGreen() + 0.114 * col.getBlue());
                    int gray = (col.getAlpha() << 24) + (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                    image.setRGB(x, y, gray);
                }
            }
            return image;
        }
    }

}
