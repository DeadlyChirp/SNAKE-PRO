package tangNdam.slither;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class GameMenu extends JFrame {

    public GameMenu() {
        initMenu();
    }

    private void initMenu() {
        setTitle("Take It Retro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        MainMenuPanel mainMenuPanel = new MainMenuPanel();
        setContentPane(mainMenuPanel);
        setVisible(true);
    }

    private BufferedImage loadImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        int scaledWidth = (int)(img.getWidth(null));
        int scaledHeight = (int)(img.getHeight(null));
        BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        return bufferedImage;
    }

    class MainMenuPanel extends JPanel {
        private JPanel playButton;
        private JPanel optionsButton;
        private JPanel creditsButton;
        private JPanel quitButton;
        private BufferedImage backgroundImage;
        private Font customFont; // Custom font variable
        private CardLayout cardLayout = new CardLayout(); // CardLayout to manage the content panels
        private JPanel cardsPanel;

        public MainMenuPanel() {
            // Load the custom font
            try {
                customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/java/tangNdam/slither/images/PressStart2P-Regular.ttf")).deriveFont(50f); // Specify the correct path to your TTF file and desired font size
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
            } catch (IOException | FontFormatException e) {
                e.printStackTrace(); // It's better to print the stack trace to see the error during development
                customFont = new Font("SansSerif", Font.BOLD, 40); // Fallback font in case the custom font fails to load
            }


            backgroundImage = loadImage("src/main/java/tangNdam/slither/images/lastbg.png");
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));




            playButton = createButton("Play");
            optionsButton = createButton("Options");
            creditsButton = createButton("Credits");
            quitButton = createButton("Quit");

            add(Box.createVerticalStrut(700)); // Adjust this value as necessary
            add(playButton);
            add(optionsButton);
            add(creditsButton);
            add(quitButton);

            add(Box.createVerticalStrut(100)); // Adjust this value as necessary
            setupKeyboardNavigation();
        }

        private JPanel createButton(String text) {
            Icon leftArrowIcon = new ImageIcon("src/main/java/tangNdam/slither/images/arrow.png");
            Icon rightArrowIcon = new ImageIcon("src/main/java/tangNdam/slither/images/arrow2.png");
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setOpaque(false);

            JLabel leftArrowLabel = new JLabel(leftArrowIcon);
            leftArrowLabel.setVisible(false);
            JLabel rightArrowLabel = new JLabel(rightArrowIcon);
            rightArrowLabel.setVisible(false);


            JButton button = new JButton(text);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setForeground(Color.ORANGE);
            button.setFont(customFont);
            button.addActionListener(e -> onButtonClicked(e, text));

            buttonPanel.add(leftArrowLabel, BorderLayout.WEST);
            buttonPanel.add(button, BorderLayout.CENTER);
            buttonPanel.add(rightArrowLabel, BorderLayout.EAST);


            // Initially, the arrows are not visible
            leftArrowLabel.setVisible(false);
            rightArrowLabel.setVisible(false);

            // Add the arrows and button to the panel
            buttonPanel.add(leftArrowLabel, BorderLayout.WEST);
            buttonPanel.add(button, BorderLayout.CENTER);
            buttonPanel.add(rightArrowLabel, BorderLayout.EAST);


            // Adjust the size of the panel to match the preferred size of the button
            Dimension buttonSize = button.getPreferredSize();
            Dimension arrowSize = new Dimension(leftArrowIcon.getIconWidth(), leftArrowIcon.getIconHeight());
            int width = buttonSize.width + (2 * arrowSize.width);
            int height = Math.max(buttonSize.height, arrowSize.height);
            buttonPanel.setMaximumSize(new Dimension(width, height));

            if ("Play".equals(text)) {
                button.addActionListener(e -> new PlayMenu()); // This will display the Play window when clicked
            } else if ("Credits".equals(text)) {
                button.addActionListener(e -> new CreditsDisplay()); // This will display the Credits window when clicked
            } else if ("Quit".equals(text)) {
                button.addActionListener(e -> System.exit(0)); // This will exit the application when clicked
            } else if ("Options".equals(text)) {
                button.addActionListener(e -> new OptionsDisplay()); // This will display the Options window when clicked
            }

            // Add focus listener to the button to show/hide the arrows
            button.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    leftArrowLabel.setVisible(true);
                    rightArrowLabel.setVisible(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    leftArrowLabel.setVisible(false);
                    rightArrowLabel.setVisible(false);
                }
            });

            return buttonPanel;
        }

        private void onButtonClicked(ActionEvent e, String button) {
            System.out.println(button + " button clicked");
            // Implement your button actions here
        }


        private void setupKeyboardNavigation() {
            setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
            setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet());
            JButton playBtn = (JButton) playButton.getComponent(1);
            JButton optionsBtn = (JButton) optionsButton.getComponent(1);
            JButton creditsBtn = (JButton) creditsButton.getComponent(1);
            JButton quitBtn = (JButton) quitButton.getComponent(1);

            playBtn.setFocusable(true);
            optionsBtn.setFocusable(true);
            creditsBtn.setFocusable(true);
            quitBtn.setFocusable(true);

            playBtn.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    navigateButtons(e, playButton, quitButton, optionsButton);
                }
            });
            optionsBtn.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    navigateButtons(e, optionsButton, playButton, creditsButton);
                }
            });
            creditsBtn.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    navigateButtons(e, creditsButton, optionsButton, quitButton);
                }
            });
            quitBtn.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    navigateButtons(e, quitButton, creditsButton, playButton);
                }
            });
        }

        private void navigateButtons(KeyEvent e, JPanel currentPanel, JPanel upPanel, JPanel downPanel) {
            JButton currentButton = (JButton) currentPanel.getComponent(1); // Assumes JButton is in the center (index 1)
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                ((JButton) upPanel.getComponent(1)).requestFocus();
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                ((JButton) downPanel.getComponent(1)).requestFocus();
            }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        }
    }
}
