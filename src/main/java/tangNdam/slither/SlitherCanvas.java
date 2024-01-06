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
    private int zoom = 12;

    private boolean[] map;
    private final SlitherJFrame view;
    private long lastFrameTime;
    private double fps;
    private final Timer repaintTimer;
    private SlitherModel model;

    private int worldBoundaryRadius;


    // Constructor
    // Constructor
    public SlitherCanvas(SlitherJFrame view) {
        super();
        this.view = view;
        setBackground(BACKGROUND_COLOR);
        setForeground(FOREGROUND_COLOR);

        this.worldBoundaryRadius = 500;

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
        addMouseWheelListener(e -> {
            zoom -= e.getWheelRotation();
            zoom = Math.max(zoom, 0);
            zoom = Math.min(zoom, 18);
        });

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

//    @Override
//    protected void paintComponent(Graphics graphics) {
//        super.paintComponent(graphics);
//        System.out.println("paintComponent is called");
//
//        if (!(graphics instanceof Graphics2D g)) {
//            return;
//        }
//
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        int w = getWidth();
//        int h = getHeight();
//        int m = Math.min(w, h);
//
//        // Save the original stroke and transform
//        Stroke originalStroke = g.getStroke();
//        AffineTransform originalTransform = g.getTransform();
//
//        // Draw the static grid background before the transform is applied
//        int gridSize = model.worldsectorSize;
//        int gridCount = model.worldBoundaryRadius * 2 / gridSize;
//        g.setColor(SECTOR_COLOR);
//        for (int i = -gridCount; i <= gridCount; i++) {
//            for (int j = -gridCount; j <= gridCount; j++) {
//                g.drawRect(w / 2 + i * gridSize - gridSize / 2, h / 2 + j * gridSize - gridSize / 2, gridSize, gridSize);
//            }
//        }
//        // Apply transform for snake and other game elements
//        double viewScale;
//        double translateX;
//        double translateY;
//        if (model.snake == null) {
//            viewScale = 1d * m / (model.worldBoundaryRadius * 2);
//            translateX = w / 2.0;
//            translateY = h / 2.0;
//        } else {
//            viewScale = Math.pow(1.25, zoom) * m / (model.worldBoundaryRadius * 2);
//            translateX = w / 2.0 - viewScale * model.snake.x;
//            translateY = h / 2.0 - viewScale * model.snake.y;
//        }
//
//        // Apply the transformations
//        AffineTransform transform = AffineTransform.getTranslateInstance(translateX, translateY);
//        transform.scale(viewScale, viewScale);
//        g.setTransform(transform);
//
//        modelPaintBlock:
//        synchronized (view.modelLock) {
//            SlitherModel model = view.model;
//            if (model == null) {
//                break modelPaintBlock;
//            }
//            if (model.snake != null) {
//                int playerX = model.getX();
//                int playerY = model.getY();
//                // Map these coordinates to the minimap
//                // Assuming the minimap size is 80x80 and located at the bottom right
//                double minimapScale = 80.0 / (model.worldBoundaryRadius * 2);
//                int minimapX = (int)(playerX * minimapScale) + w - 80;
//                int minimapY = (int)(playerY * minimapScale) + h - 80;
//
//                g.setColor(Color.RED); // mainTest.Player position color
//                g.fillOval(minimapX, minimapY, 5, 5); // Adjust size as needed
//            }
//
//
//            // Zoom and translate the graphics context
//            AffineTransform oldTransform = g.getTransform();
//            double scale;
//            if (zoom == 0 || model.snake == null) {
//                scale = 1d * m / (model.worldBoundaryRadius * 2);
//                g.translate(w / 2.0, h / 2.0); // Translate to the center of the window
//                g.scale(scale, scale);
//                g.translate(-model.worldBoundaryRadius, -model.worldBoundaryRadius); // Translate to center the game world
//            } else {
//                scale = Math.pow(1.25, zoom + 1) * m / (model.worldBoundaryRadius * 2);
//                g.translate(w / 2.0, h / 2.0); // Translate to the center of the window
//                g.scale(scale, scale);
//                g.translate(-model.snake.x, -model.snake.y); // Translate so that the snake is in the center
//            }
//
//
//            //mouse control
//            if (mouseInput != null && model.snake != null) {
//                mouseInput.applyToSnake(model.snake);
//            }
//
//            g.setColor(SECTOR_COLOR);
//            for (int y = 0; y < model.sectors.length; y++) {
//                for (int x = 0; x < model.sectors[y].length; x++) {
//                    if (model.sectors[y][x]) {
//                        g.fillRect(x * model.worldsectorSize + 1, y * model.worldsectorSize + 1, model.worldsectorSize - 2, model.worldsectorSize - 2);
//                    }
//                }
//            }
//
//            g.setColor(FOOD_COLOR);
//            model.activefoods.values().forEach(food -> {
//                final double radius = food.getRadius(); // Make the variable final or effectively final
//                g.fill(new Ellipse2D.Double(food.x - radius, food.y - radius, radius * 2, radius * 2));
//            });
////            g.setColor(FOREGROUND_COLOR);
////            Stroke oldStroke = g.getStroke();
////            g.setStroke(new BasicStroke(128));
////            g.drawOval(-64, -64, model.worldBoundaryRadius * 2 + 128, model.worldBoundaryRadius * 2 + 128);
////            g.setStroke(oldStroke);
////            oldStroke = g.getStroke();
//
//
//            model.activepreys.values().forEach(prey -> {
//                double preyRadius = prey.getRadius();
//                if (preyRadius <= 0) {
//                    return;
//                }
//                g.setPaint(new RadialGradientPaint((float) (prey.x - 0.5 / scale), (float) (prey.y - 0.5 / scale), (float) (preyRadius * 2), PREY_HALO_FRACTIONS, PREY_HALO_COLORS));
//                g.fillRect((int) Math.floor(prey.x - preyRadius * 2 - 1), (int) Math.floor(prey.y - preyRadius * 2 - 1), (int) (preyRadius * 4 + 3), (int) (preyRadius * 4 + 3));
//                g.setColor(PREY_COLOR);
//                g.fill(new Ellipse2D.Double(prey.x - preyRadius, prey.y - preyRadius, preyRadius * 2, preyRadius * 2));
//            });
//
//
//            g.setFont(NAME_FONT.deriveFont((float) (18 / Math.pow(scale, 0.75))));
//            model.activesnakes.values().forEach(snake -> {
//                double snakeScale = Math.min(snake.getScale(), Snake.MAX_SCALE); // Ensure the scale does not exceed the max
//                double thickness = 16 + snakeScale * 10; // Calculate the thickness of the snake
//                if (snake.body.size() >= 2) {
//                    g.setColor(snake == model.snake ? OWN_SNAKE_BODY_COLOR : SNAKE_BODY_COLOR);
//                    g.setStroke(new BasicStroke((float) thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//
//                    double totalLength = 0;
//                    double lastX = 0, lastY = 0;
//                    for (SnakeBody bodyPart : snake.body) {
//                        if (bodyPart != snake.body.getFirst()) {
//                            totalLength += Math.sqrt((bodyPart.x - lastX) * (bodyPart.x - lastX) + (bodyPart.y - lastY) * (bodyPart.y - lastY));
//                        }
//                        if (bodyPart != snake.body.getLast()) {
//                            lastX = bodyPart.x;
//                            lastY = bodyPart.y;
//                        }
//                    }
//
//                    Path2D.Double snakePath = new Path2D.Double();
//                    snakePath.moveTo(snake.x, snake.y);
//
//                    lastX = snake.x;
//                    lastY = snake.y;
//
//                    for (SnakeBody bodyPart : snake.body) {
//                        double partLength = Math.sqrt((bodyPart.x - lastX) * (bodyPart.x - lastX) + (bodyPart.y - lastY) * (bodyPart.y - lastY));
//                        if (partLength > totalLength) {
//                            snakePath.lineTo(lastX + (totalLength / partLength) * (bodyPart.x - lastX), lastY + (totalLength / partLength) * (bodyPart.y - lastY));
//                            break;
//                        }
//                        snakePath.lineTo(bodyPart.x, bodyPart.y);
//                        totalLength -= partLength;
//                        lastX = bodyPart.x;
//                        lastY = bodyPart.y;
//                    }
//
//                    g.draw(snakePath);
//                }
//
//                if (snake.isBoosting()) {
//                    g.setPaint(new RadialGradientPaint((float) (snake.x - 0.5 / scale), (float) (snake.y - 0.5 / scale),
//                            (float) (thickness * 4 / 3), SNAKE_HALO_FRACTIONS,
//                            snake == model.snake ? OWN_SNAKE_HALO_COLORS : SNAKE_HALO_COLORS));
//                    g.fillRect((int) Math.round(snake.x - thickness * 3 / 2 - 1), (int) Math.round(snake.y - thickness * 3 / 2 - 1), (int) (thickness * 3 + 2), (int) (thickness * 3 + 2));
//                }
//                g.setColor(snake == model.snake ? OWN_SNAKE_COLOR : SNAKE_COLOR);
//                g.fill(new Ellipse2D.Double(snake.x - thickness * 2 / 3, snake.y - thickness * 2 / 3, thickness * 4 / 3, thickness * 4 / 3));
//
//                String lengthText = String.valueOf(model.getSnakeLength(snake.body.size(), snake.getFood()));
//
//                g.setColor(NAME_SHADOW_COLOR);
//                g.drawString(snake.name,
//                        (float) (snake.x - g.getFontMetrics().stringWidth(snake.name) / 2.0 + g.getFontMetrics().getHeight() / 12.0),
//                        (float) (snake.y - thickness * 2 / 3 - g.getFontMetrics().getHeight() + g.getFontMetrics().getHeight() / 12.0));
//                g.drawString(lengthText,
//                        (float) (snake.x - g.getFontMetrics().stringWidth(lengthText) / 2.0 + g.getFontMetrics().getHeight() / 12.0),
//                        (float) (snake.y - thickness * 2 / 3 + g.getFontMetrics().getHeight() / 12.0));
//
//                g.setColor(FOREGROUND_COLOR);
//                g.drawString(snake.name, (float) (snake.x - g.getFontMetrics().stringWidth(snake.name) / 2.0), (float) (snake.y - thickness * 2 / 3 - g.getFontMetrics().getHeight()));
//                g.drawString(lengthText, (float) (snake.x - g.getFontMetrics().stringWidth(lengthText) / 2.0), (float) (snake.y - thickness * 2 / 3));
//            });
//
//            g.setTransform(oldTransform);
//            g.setStroke(new BasicStroke(1));
//            g.setColor(MAP_COLOR);
//            g.drawOval(w - 80, h - 80, 79, 79);
//            // Restore the original stroke and transform for further drawing
//            g.setStroke(originalStroke);
//            g.setTransform(originalTransform);
//            boolean[] currentMap = map;
//            if (currentMap != null) {
//                for (int i = 0; i < currentMap.length; i++) {
//                    if (currentMap[i]) {
//                        g.fillRect((i % 80) + w - 80, (i / 80) + h - 80, 1, 1);
//                    }
//                }
//            }
//            if (zoom != 0 && model.snake != null) {
//                double zoomScale = Math.pow(1.25, zoom + 1);
//                g.setColor(MAP_POSITION_COLOR);
////                oldStroke = g.getStroke();
//                g.setStroke(new BasicStroke(2));
//                g.draw(new Rectangle2D.Double(
//                        model.snake.x * 80 / (model.worldBoundaryRadius * 2) - w / zoomScale / m * 40 + w - 80,
//                        model.snake.y * 80 / (model.worldBoundaryRadius * 2) - h / zoomScale / m * 40 + h - 80,
//                        w / zoomScale / m * 80,
//                        h / zoomScale / m * 80
//                ));
////                g.setStroke(oldStroke);
//            }
//
//        }
//        // At the end of the method, reset the graphics context to its original state
//        g.setStroke(originalStroke);
//        g.setTransform(originalTransform);
//
//        g.setFont(DEBUG_FONT);
//        g.setColor(FOREGROUND_COLOR);
//        long newFrameTime = System.currentTimeMillis();
//        if (newFrameTime > lastFrameTime) {
//            fps = 0.95 * fps + 0.05 * 1000.0 / (newFrameTime - lastFrameTime);
//        }
//        g.drawString("FPS: " + Math.round(fps), 0, g.getFontMetrics().getAscent());
//        lastFrameTime = newFrameTime;
//    }



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
        int boundaryRadius = model.getWorldBoundaryRadius(); // Use the getter method

        // Set wall color and thickness
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3)); // Thick line for visibility

        // Draw a rectangle representing the wall at the boundary
        int wallThickness = 10; // Change as needed
        int wallOffset = wallThickness / 2;
        int boundary = boundaryRadius * 2;

        // You need to draw the wall considering the scale and translation of the game world
        g.drawRect(-boundaryRadius - wallOffset, -boundaryRadius - wallOffset, boundary + wallThickness, boundary + wallThickness);
    }


    private void applyGameWorldTransform(Graphics2D g, int m) {
        double viewScale;
        double translateX;
        double translateY;

        if (model.snake == null) {
            viewScale = 1d * m / (model.worldBoundaryRadius * 2);
            translateX = m / 2.0;
            translateY = m / 2.0;
        } else {
            viewScale = Math.pow(1.25, zoom) * m / (model.worldBoundaryRadius * 2);
            translateX = m / 2.0 - viewScale * model.snake.x;
            translateY = m / 2.0 - viewScale * model.snake.y;
        }

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
                drawMinimap(g);
            }
        }
    }

    private void drawUIElements(Graphics2D g) {
        g.setFont(DEBUG_FONT);
        g.setColor(FOREGROUND_COLOR);
        g.drawString("FPS: " + Math.round(fps), 10, 20);
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
            g.setColor(NAME_SHADOW_COLOR);
            g.drawString(name, (float) (snake.x - g.getFontMetrics().stringWidth(name) / 2.0),
                    (float) (snake.y - thickness * 2 / 3 - g.getFontMetrics().getHeight()));
            g.drawString(lengthText, (float) (snake.x - g.getFontMetrics().stringWidth(lengthText) / 2.0),
                    (float) (snake.y + thickness * 2 / 3 + g.getFontMetrics().getHeight()));
        });
    }

    private void drawFoods(Graphics2D g) {
        g.setColor(FOOD_COLOR);
        model.activefoods.values().forEach(food -> {
            Ellipse2D.Double shape = new Ellipse2D.Double(food.x - food.getRadius(),
                    food.y - food.getRadius(),
                    2 * food.getRadius(),
                    2 * food.getRadius());
            g.fill(shape);
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

    private void drawMinimap(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();
        double minimapScale = 80.0 / (model.worldBoundaryRadius * 2);
        int minimapX = (int) (model.snake.x * minimapScale) + w - 80;
        int minimapY = (int) (model.snake.y * minimapScale) + h - 80;

        g.setColor(MAP_COLOR);
        g.fillRect(w - 80, h - 80, 80, 80);  // Minimap background

        g.setColor(MAP_POSITION_COLOR);
        g.fillOval(minimapX - 2, minimapY - 2, 4, 4);  // Player's position on the minimap
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
