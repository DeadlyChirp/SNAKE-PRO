package tangNdam.slither;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;


import java.awt.geom.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;




// Graphique du jeu
//dessin les entités du jeu, arriere plan, nourriture, etc.
// mecanique de jeu : zoom, deplacement, etc.
public class SlitherCanvas extends JPanel { // JPanel est une classe de Swing
    private static final Color BACKGROUND_COLOR = new Color(0x2B2B2B);
    private static final Color FOREGROUND_COLOR = new Color(0xA9B7C6);
    private static final Color SECTOR_COLOR = new Color(0x803C3F41, true);
    private static final Color FOOD_COLOR = new Color(0xCC7832);
    private static final Color PREY_COLOR = new Color(0xFFFF00);
    private static final float[] PREY_HALO_FRACTIONS = new float[]{0.5f, 1f};
    private static final Color[] PREY_HALO_COLORS = new Color[]{new Color(0x60FFFF00, true), new Color(0x00FFFF00, true)};
    private static final Color SNAKE_COLOR = new Color(0x287BDE);
    private static final Color OWN_SNAKE_COLOR = new Color(0x39AFFF);
    private static final float[] SNAKE_HALO_FRACTIONS = new float[]{0.5f, 1f};
    private static final Color[] SNAKE_HALO_COLORS = new Color[]{new Color(0x60287BDE, true), new Color(0x00287BDE, true)};
    private static final Color[] OWN_SNAKE_HALO_COLORS = new Color[]{new Color(0x6039AFFF, true), new Color(0x0039AFFF, true)};
    private static final Color SNAKE_BODY_COLOR = new Color(0x6A8759);
    private static final Color OWN_SNAKE_BODY_COLOR = new Color(0xA5C261);
    private static final Color MAP_COLOR = new Color(0xA0A9B7C6, true);
    private static final Color MAP_POSITION_COLOR = new Color(0xE09E2927, true);
    private static final Color NAME_SHADOW_COLOR = new Color(0xC02B2B2B, true);
    private static final Font NAME_FONT = Font.decode("SansSerif-BOLD");
    private static final Font DEBUG_FONT = Font.decode("SansSerif-PLAIN-12");



    private MouseInput mouseInput;
    private int zoom = 7;

    private boolean[] map;
    private final SlitherJFrame view;
    private long lastFrameTime;
    private double fps;
    private final Timer repaintTimer;
    private SlitherModel model;

    private int worldBoundaryRadius;

    private double viewScale; // add this as a member variable



    // Constructor
    // Constructor
    public SlitherCanvas(SlitherJFrame view) {
        super();
        this.view = view;
        setBackground(BACKGROUND_COLOR);
        setForeground(FOREGROUND_COLOR);

        this.worldBoundaryRadius = 2000;

        // Initialize mouse controls
        initMouseControls();

        // Scheduled repaint using Timer
        repaintTimer = new Timer("Repaint Timer", true);
        repaintTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, 17); // Approximately 60 FPS

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (model != null && model.getSnake(1) != null) {
                    model.getSnake(1).startBoosting();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (model != null && model.getSnake(1) != null) {
                    model.getSnake(1).stopBoosting();
                }
            }
        });
    }

    public void stopRepaintTimer() {
        repaintTimer.cancel();
    }

    private void initMouseControls() {
        mouseInput = new MouseInput();

        //mouse scroller
//        addMouseWheelListener(e -> {
//            zoom -= e.getWheelRotation();
//            zoom = Math.max(zoom, 0);
//            zoom = Math.min(zoom, 18);
//        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseInput.readWang(e);
            }
        });
        

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseInput.boost = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseInput.boost = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseInput.readWang(e);
                model.updatePlayerPosition(model.snake, mouseInput.wang, mouseInput.boost);
            }
        });
    }
    private class MouseInput {
        Double wang; // The desired angle of the snake's head
        boolean boost; // Whether the snake is boosting

        private void readWang(MouseEvent e) {
            double deltaX = e.getX() - (getWidth() / 2.0);
            double deltaY = e.getY() - (getHeight() / 2.0);
            wang = Math.atan2(deltaY, deltaX);
        }

        // Method to apply the desired direction and boost to the snake
        public void applyToSnake(Snake snake) {
            if (wang != null) {
                snake.setDirection(wang);
            }
            snake.setBoosting(boost);
        }
    }

    void setMap(boolean[] map) {
        this.map = map;
    }

    //test
    @Override

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!(graphics instanceof Graphics2D g)) {
            return;
        }

        // Set rendering hints for quality
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int m = Math.min(w, h);

//        // Draw the static grid background before any transformations
//        drawStaticGrid(g, w, h);

        // Save the original stroke and transform for later restoration
        Stroke originalStroke = g.getStroke();
        AffineTransform originalTransform = g.getTransform();

        // Apply transform for game elements like snake, food, etc.
        applyGameWorldTransform(g, m);

        // Now draw the game elements like snake, food, and preys with the transformed graphics object
        drawGameElements(g);

        // Draw the boundary wall
        drawBoundaryWall(g);


        // Restore the original stroke and transform before drawing UI elements
        g.setStroke(originalStroke);
        g.setTransform(originalTransform);

        // Draw UI elements like FPS counter, etc.
        drawUIElements(g);

        // Draw the minimap
        drawMinimap(g);

        // Store the time of the last repaint for FPS calculation
        storeLastRepaintTime();
    }


    private void drawStaticGrid(Graphics2D g, int w, int h) {
        g.setColor(SECTOR_COLOR);
        int gridSize = model.worldsectorSize;
        int gridCount = model.worldBoundaryRadius * 2 / gridSize;
        for (int i = -gridCount; i <= gridCount; i++) {
            for (int j = -gridCount; j <= gridCount; j++) {
                g.drawRect(w / 2 + i * gridSize - gridSize / 2, h / 2 + j * gridSize - gridSize / 2, gridSize, gridSize);
            }
        }
    }
    private void drawBoundaryWall(Graphics2D g) {
        if (model == null) {
            return; // Don't draw if the model is not set
        }
        int boundaryRadius = getWorldBoundaryRadius(); // Use the getter method

        // Set wall color and thickness
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));
        g.drawOval(-worldBoundaryRadius, -worldBoundaryRadius, worldBoundaryRadius * 2, worldBoundaryRadius * 2);
    }


    private void applyGameWorldTransform(Graphics2D g, int m) {
        // Here we are getting the actual width and height of the canvas
        int w = getWidth();
        int h = getHeight();

        // The viewScale should increase as the zoom level increases, making the view closer.
        // If zoom is a level with 0 being default, 1 being zoomed in once, etc., you can use:
        viewScale = Math.pow(1.25, zoom) * m / (worldBoundaryRadius * 2);

        double translateX;
        double translateY;

        // Centering the view on the player's snake
        if (model.snake != null) {
            translateX = w / 2.0 - viewScale * model.snake.x;
            translateY = h / 2.0 - viewScale * model.snake.y;
        } else {
            // If for some reason the player's snake doesn't exist, center the view on the world origin
            translateX = w / 2.0;
            translateY = h / 2.0;
        }

        // Apply the transformations
        AffineTransform transform = AffineTransform.getTranslateInstance(translateX, translateY);
        transform.scale(viewScale, viewScale);
        g.setTransform(transform);
    }






    private void drawGameElements(Graphics2D g) {
        // Assuming you have methods like drawSnake, drawFoods, drawPreys, etc.
        synchronized (view.modelLock) {
            if (model != null) {
                drawSnakes(g);
                drawFoods(g);
                drawPreys(g);
//                drawMinimap(g);
            }
        }
    }

    private void drawUIElements(Graphics2D g) {
        g.setFont(DEBUG_FONT);
        g.setColor(FOREGROUND_COLOR);
        g.drawString("FPS: " + Math.round(fps) + "  Press esc to exit", 10, 20);
        // Any other UI elements you want to draw
    }

    private void storeLastRepaintTime() {
        long newFrameTime = System.currentTimeMillis();
        if (newFrameTime > lastFrameTime) {
            fps = 0.95 * fps + 0.05 * 1000.0 / (newFrameTime - lastFrameTime);
        }
        lastFrameTime = newFrameTime;
    }

    private void drawSnakes(Graphics2D g) {
        model.activesnakes.values().forEach(snake -> {
            double snakeScale = Math.min(snake.getScale(), Snake.MAX_SCALE);
            double thickness = 16 + snakeScale * 10;
            g.setColor(snake == model.snake ? OWN_SNAKE_BODY_COLOR : SNAKE_BODY_COLOR);
            g.setStroke(new BasicStroke((float) thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Path2D.Double snakePath = new Path2D.Double();
            snakePath.moveTo(snake.x, snake.y);

            snake.body.forEach(bodyPart -> {
                snakePath.lineTo(bodyPart.x, bodyPart.y);
            });

            g.draw(snakePath);

            if (snake.isBoosting()) {
                g.setPaint(new RadialGradientPaint(
                        new Point2D.Float((float) snake.x, (float) snake.y),
                        (float) thickness * 4 / 3,
                        SNAKE_HALO_FRACTIONS,
                        snake == model.snake ? OWN_SNAKE_HALO_COLORS : SNAKE_HALO_COLORS
                ));
                g.fillRect((int) (snake.x - thickness * 3 / 2 - 1),
                        (int) (snake.y - thickness * 3 / 2 - 1),
                        (int) (thickness * 3 + 2),
                        (int) (thickness * 3 + 2));
            }

            g.setColor(snake == model.snake ? OWN_SNAKE_COLOR : SNAKE_COLOR);
            g.fill(new Ellipse2D.Double(snake.x - thickness * 2 / 3,
                    snake.y - thickness * 2 / 3,
                    thickness * 4 / 3,
                    thickness * 4 / 3));

            // Draw the name and length of the snake
            String name = snake.name;
            String lengthText = String.valueOf(model.getSnakeLength(snake.body.size(), snake.getFood()));
            g.setFont(NAME_FONT);
            g.setColor(Color.WHITE);
            g.drawString(name, (float) (snake.x - g.getFontMetrics().stringWidth(name) / 2.0),
                    (float) (snake.y - thickness * 2 / 3 - g.getFontMetrics().getHeight()));
            g.drawString(lengthText, (float) (snake.x - g.getFontMetrics().stringWidth(lengthText) / 2.0),
                    (float) (snake.y + thickness * 2 / 3 + g.getFontMetrics().getHeight()));
        });
    }

    private void drawFoods(Graphics2D g) {
        // Scale factor to increase the size of the food
        double scaleFactor = 3;  // You can adjust this value to increase or decrease the size

        Color[] vibrantColors = new Color[]{
                new Color(0xFF4500), // Orange-Red
                new Color(0x32CD32), // Lime Green
                new Color(0x00FA9A), // Medium Spring Green
                new Color(0x1E90FF), // Dodger Blue
                new Color(0x9400D3), // Dark Violet
                // Add more colors as desired
        };
        model.activefoods.values().forEach(food -> {
            Color foodColor = vibrantColors[(int) (Math.random() * vibrantColors.length)];

            RadialGradientPaint paint = new RadialGradientPaint(
                    new Point2D.Double(food.x, food.y),
                    (float) (food.getRadius() * scaleFactor * 1.5f), // Apply scale factor here
                    new float[]{0.0f, 1.0f},
                    new Color[]{foodColor, new Color(foodColor.getRed(), foodColor.getGreen(), foodColor.getBlue(), 0)}
            );

            g.setPaint(paint);
            g.fill(new Ellipse2D.Double(
                    food.x - food.getRadius() * scaleFactor,  // Apply scale factor here
                    food.y - food.getRadius() * scaleFactor,  // Apply scale factor here
                    2 * food.getRadius() * scaleFactor,       // Apply scale factor here
                    2 * food.getRadius() * scaleFactor        // Apply scale factor here
            ));
            g.setColor(foodColor.brighter());
            g.fill(new Ellipse2D.Double(
                    food.x - food.getRadius() * scaleFactor * 0.5,  // Apply scale factor here
                    food.y - food.getRadius() * scaleFactor * 0.5,  // Apply scale factor here
                    food.getRadius() * scaleFactor,                 // Apply scale factor here
                    food.getRadius() * scaleFactor                  // Apply scale factor here
            ));
        });
    }




    private void drawPreys(Graphics2D g) {
        model.activepreys.values().forEach(prey -> {
            // Draw the halo effect for prey
            RadialGradientPaint paint = new RadialGradientPaint(
                    new Point2D.Float((float) prey.x, (float) prey.y),
                    (float) prey.getRadius() * 2,
                    PREY_HALO_FRACTIONS,
                    PREY_HALO_COLORS
            );
            g.setPaint(paint);
            g.fillRect((int) (prey.x - prey.getRadius() * 2),
                    (int) (prey.y - prey.getRadius() * 2),
                    (int) (prey.getRadius() * 4),
                    (int) (prey.getRadius() * 4));

            // Draw the prey itself
            g.setColor(PREY_COLOR);
            g.fill(new Ellipse2D.Double(prey.x - prey.getRadius(),
                    prey.y - prey.getRadius(),
                    2 * prey.getRadius(),
                    2 * prey.getRadius()));
        });
    }

    // In the SlitherCanvas class

    private void drawMinimap(Graphics2D g) {
        int minimapSize = 80; // The diameter of the circular minimap
        int w = getWidth();
        int h = getHeight();
        int minimapX = w - minimapSize - 10; // Adjust for padding from edges
        int minimapY = h - minimapSize - 10;

        // Draw the minimap background as a circle
        g.setColor(MAP_COLOR);
        g.fillOval(minimapX, minimapY, minimapSize, minimapSize);

        // Calculate the scale of the minimap relative to the entire game world
        double minimapScale = (double) minimapSize / (model.worldBoundaryRadius * 2);

        // Draw all food on the minimap within the circular boundary
        for (Food food : SlitherModel.activefoods.values()) {
            double foodMinimapX = (food.x + model.worldBoundaryRadius) * minimapScale;
            double foodMinimapY = (food.y + model.worldBoundaryRadius) * minimapScale;

            if (isWithinCircle(foodMinimapX, foodMinimapY, minimapSize / 2)) {
                g.setColor(FOOD_COLOR);
                g.fillRect((int) (minimapX + foodMinimapX - 1), (int) (minimapY + foodMinimapY - 1), 2, 2); // Draw food as small dots
            }
        }

        // Draw the snake's position as a distinct point on the minimap
        if (model.snake != null) {
            double snakeMinimapX = (model.snake.x + model.worldBoundaryRadius) * minimapScale;
            double snakeMinimapY = (model.snake.y + model.worldBoundaryRadius) * minimapScale;

            g.setColor(new Color(0, 255, 0)); // Green color for the snake's point
            g.fillRect((int) (minimapX + snakeMinimapX - 2), (int) (minimapY + snakeMinimapY - 2), 4, 4); // Draw the snake's position larger
        }
    }

    // Helper method to check if a point is within the circular minimap boundary
    private boolean isWithinCircle(double x, double y, double radius) {
        // Translate the coordinates to be relative to the center of the minimap
        double centerX = radius;
        double centerY = radius;
        double dx = x - centerX;
        double dy = y - centerY;
        return (dx * dx + dy * dy) <= (radius * radius);
    }


    // Helper method to clamp positions within the minimap circle
    private double clampPosition(double position, int size) {
        double radius = size / 2.0;
        double angle = Math.atan2(position - radius, position - radius);
        double distance = Math.min(radius, Math.hypot(position - radius, position - radius));
        return radius + distance * Math.cos(angle);
    }



    public void setModel(SlitherModel model) {
        this.model = model;
    }


    public int getWorldBoundaryRadius() {
        return worldBoundaryRadius;
    }

    public void setWorldBoundaryRadius(int worldBoundaryRadius) {
        this.worldBoundaryRadius = worldBoundaryRadius;
    }


}
