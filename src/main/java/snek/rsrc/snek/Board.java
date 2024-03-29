package snek.rsrc.snek;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import tangNdam.slither.GameMenu;

public class Board extends JPanel implements ActionListener {

    private final int NUM_CELLS = 20; // Number of cells in each direction
    private final int DOT_SIZE; // Size of each cell/grid element
    private final int B_WIDTH; // Width of the board
    private final int B_HEIGHT; // Height of the board
    private final int ALL_DOTS; // Maximum number of possible dots on the board
    private final int RAND_POS; // Range for random positioning of the apple
    private final int DELAY = 250; // Game speed

    private final int[] x;
    private final int[] y;

    private int dots;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image headUp;
    private Image headDown;
    private Image headLeft;
    private Image headRight;


    private int score = 0;

    private int speedIncrease = 10;

    private Image background;
    private int boardTopLeftX;
    private int boardTopLeftY;


    public Board() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        DOT_SIZE = Math.min(screenWidth, screenHeight) / NUM_CELLS;
        B_WIDTH = DOT_SIZE * NUM_CELLS;
        B_HEIGHT = DOT_SIZE * NUM_CELLS;
        boardTopLeftX = (screenWidth - B_WIDTH) / 2;
        boardTopLeftY = (screenHeight - B_HEIGHT) / 2;
        ALL_DOTS = NUM_CELLS * NUM_CELLS;
        RAND_POS = NUM_CELLS;
        setPreferredSize(screenSize);

        x = new int[ALL_DOTS];
        y = new int[ALL_DOTS];

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon bg = new ImageIcon("src/main/java/snek/rsrc/backgroundSnake.png");
        background = bg.getImage().getScaledInstance(B_WIDTH, B_HEIGHT, Image.SCALE_SMOOTH);


        ImageIcon iid = new ImageIcon(new ImageIcon("src/main/java/snek/rsrc/snekbody.png").getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH));
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon(new ImageIcon("src/main/java/snek/rsrc/food.png").getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH));
        apple = iia.getImage();

        ImageIcon iihU = new ImageIcon("src/main/java/snek/rsrc/head_up.png");
        headUp = iihU.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);

        ImageIcon iihD = new ImageIcon("src/main/java/snek/rsrc/head_down.png");
        headDown = iihD.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);

        ImageIcon iihL = new ImageIcon("src/main/java/snek/rsrc/head_left.png");
        headLeft = iihL.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);

        ImageIcon iihR = new ImageIcon("src/main/java/snek/rsrc/head_right.png");
        headRight = iihR.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);
    }


    private void initGame() {
        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = -z * DOT_SIZE; // Start at 0 and go left for each subsequent dot
            y[z] = DOT_SIZE; // Start one DOT_SIZE down from the top
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
        updateScore();
    }
    public int getB_WIDTH() {
        return B_WIDTH;
    }

    public int getB_HEIGHT() {
        return B_HEIGHT;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // The background should be drawn without any translation
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            System.out.println("Background image is not loaded.");
        }
        doDrawing(g);
    }




    private void doDrawing(Graphics g) {
        // Define colors for the checkerboard pattern
        Color lightColor = new Color(208, 232, 161);  // A lighter green
        Color darkColor = new Color(129, 140, 112);   // A darker green
        g.drawImage(background, 0, 0, this);
        // Draw the checkerboard pattern
        for (int row = 0; row < B_HEIGHT / DOT_SIZE; row++) {
            for (int col = 0; col < B_WIDTH / DOT_SIZE; col++) {
                // Alternate between lightColor and darkColor
                if ((row + col) % 2 == 0) {
                    g.setColor(lightColor);
                } else {
                    g.setColor(darkColor);
                }
                g.fillRect(col * DOT_SIZE, row * DOT_SIZE, DOT_SIZE, DOT_SIZE);
            }
        }

        // Draw the grid lines
        g.setColor(Color.black);
        for (int i = 0; i <= B_WIDTH; i += DOT_SIZE) {
            g.drawLine(i, 0, i, B_HEIGHT); // Vertical lines
        }
        for (int i = 0; i <= B_HEIGHT; i += DOT_SIZE) {
            g.drawLine(0, i, B_WIDTH, i); // Horizontal lines
        }

        if (inGame) {
            // Draw the apple ensuring it aligns with the grid
            g.drawImage(apple, apple_x, apple_y, this);

            // Draw the snake ensuring each segment aligns with the grid
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    // Draw the head with the correct rotation
                    if (leftDirection) {
                        g.drawImage(headLeft, x[z], y[z], this);
                    } else if (rightDirection) {
                        g.drawImage(headRight, x[z], y[z], this);
                    } else if (upDirection) {
                        g.drawImage(headUp, x[z], y[z], this);
                    } else if (downDirection) {
                        g.drawImage(headDown, x[z], y[z], this);
                    }
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {
            gameOver(g);
        }
        if (inGame) {
            drawScore(g);
        } else {
            gameOver(g);
        }
    }


    private void drawScore(Graphics g) {
        String msg = "Score: " + score;
        Font font = new Font("Digital-7bo", Font.ITALIC, 12);
        FontMetrics metrics = getFontMetrics(font);

        int x = (B_WIDTH - metrics.stringWidth(msg)) - DOT_SIZE;
        int y = DOT_SIZE;

        g.setColor(Color.red);
        g.setFont(font);
        g.drawString(msg, x, y);
    }


    private void gameOver(Graphics g) {

        String msg = "Game Over."+ "\n" +"Buy starter pack to win!\n Press ENTER to play again, press ESC to exit";
        Font small = new Font("Digital-7", Font.BOLD, 18);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.red);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        drawScore(g);
    }

    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            locateApple();
            updateScore();
            increaseSpeed(); // Call to increase the speed of the snake
        }
    }

    private void increaseSpeed() {
        if (DELAY - speedIncrease > 0) {
            timer.setDelay(DELAY - speedIncrease); // Update the delay to increase the speed
            speedIncrease += 10; // Increase the speed by a larger increment each time
        }
    }
    private void updateScore() {
        score = (dots - 3);
    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
                break;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        int r = (int) (Math.random() * RAND_POS);
        apple_x = r * DOT_SIZE; // Ensure it's a multiple of DOT_SIZE

        r = (int) (Math.random() * RAND_POS);
        apple_y = r * DOT_SIZE; // Ensure it's a multiple of DOT_SIZE
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            

            if (key == KeyEvent.VK_ESCAPE) {
                Window window = SwingUtilities.getWindowAncestor(Board.this);
                if (window != null) {
                    window.setVisible(false);
                    window.dispose();
                }
                GameMenu menu = new GameMenu();
                menu.setVisible(true);
            }

            if (key == KeyEvent.VK_ENTER) {
                if (!inGame) {
                    inGame = true;
                    leftDirection = false;
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                    initGame();
                }
            }
        }
    }
}

