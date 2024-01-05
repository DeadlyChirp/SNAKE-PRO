package tangNdam.slither;

import java.util.*;


class SlitherModel {

    static final double PI2 = Math.PI * 2;

    final int worldBoundaryRadius; // la taille du map
    final int worldsectorSize; // la taille de chaque secteur, donc le map est divise en secteurs
    final double speedAnglediv; // la vitess de rotation, a quel point le serpent tourne vite
    final double speedCalculBase, speedCalFactor, snakeCalModifier;
    private final double angularVelocityFactor, preyAngularVelocityFactor;
    private final int maxSizeForSpeedCalculation;
    private final double[] fpsls, fmlts;

    final Map<Integer, Snake> activesnakes = new LinkedHashMap<>();
    final Map<Integer, Prey> activepreys = new LinkedHashMap<>();
    final Map<Integer, Food> activefoods = new LinkedHashMap<>();
    final boolean[][] sectors;

    private long lastUpdateTime;

    private final SlitherJFrame view;

    Snake snake;



    //test test values
    public static final int DEFAULT_WORLD_BOUNDARY_RADIUS = 1000;
    public static final int DEFAULT_WORLD_SECTOR_SIZE = 50;
    public  static final double DEFAULT_SPEED_ANGLE_DIV = 1.0;
    public static final double DEFAULT_SPEED_CALC_BASE = 5.0; // Was 1.0, now doubled
    public static final double DEFAULT_SPEED_CAL_FACTOR = 5.0; // Was 1.0, now doubled

    public static final double DEFAULT_SNAKE_CAL_MODIFIER = 1.0;
    public static final double DEFAULT_ANGULAR_VELOCITY_FACTOR = 1.0;
    public  static final double DEFAULT_PREY_ANGULAR_VELOCITY_FACTOR = 1.0;
    public  static final double DEFAULT_CST = 1.0;
    public static final int DEFAULT_MAX_SIZE_FOR_SPEED_CALCULATION = 100;


    private long lastFrameTime; // Field to store the time of the last frame



    public SlitherModel(SlitherJFrame view) {
        this(DEFAULT_WORLD_BOUNDARY_RADIUS, DEFAULT_WORLD_SECTOR_SIZE,
                DEFAULT_SPEED_ANGLE_DIV, DEFAULT_SPEED_CALC_BASE, DEFAULT_SPEED_CAL_FACTOR,
                DEFAULT_SNAKE_CAL_MODIFIER, DEFAULT_ANGULAR_VELOCITY_FACTOR,
                DEFAULT_PREY_ANGULAR_VELOCITY_FACTOR, DEFAULT_CST,
                DEFAULT_MAX_SIZE_FOR_SPEED_CALCULATION, view);
    }


    SlitherModel(int worldBoundaryRadius, int worldsectorSize, double spangdv, double nsp1, double speedCalFactor, double snakeCalModifier, double angularVelocityFactor, double preyAngularVelocityFactor, double cst, int maxSizeForSpeedCalculation, SlitherJFrame view) {
        this.worldBoundaryRadius = worldBoundaryRadius;
        this.worldsectorSize = worldsectorSize;
        this.speedAnglediv = spangdv;
        this.speedCalculBase = nsp1;
        this.speedCalFactor = speedCalFactor;
        this.snakeCalModifier = snakeCalModifier;
        this.angularVelocityFactor = angularVelocityFactor;
        this.preyAngularVelocityFactor = preyAngularVelocityFactor;
        this.maxSizeForSpeedCalculation = maxSizeForSpeedCalculation;
        this.view = view;
        lastFrameTime = System.currentTimeMillis();

        sectors = new boolean[worldBoundaryRadius * 2 / worldsectorSize][worldBoundaryRadius * 2 / worldsectorSize];

        fmlts = new double[maxSizeForSpeedCalculation + 1];
        fpsls = new double[maxSizeForSpeedCalculation + 1];
        for (int i = 0; i < maxSizeForSpeedCalculation; i++) {
            double base = (double) (maxSizeForSpeedCalculation - i) / maxSizeForSpeedCalculation;
            fmlts[i] = 1 / (base * base * Math.sqrt(Math.sqrt(base)));
            fpsls[i + 1] = fpsls[i] + fmlts[i];
        }

        lastUpdateTime = System.currentTimeMillis();
    }

    int getSnakeLength(int bodyLength, double fillAmount) {
        bodyLength = Math.min(bodyLength, maxSizeForSpeedCalculation);
        return (int) (15 * (fpsls[bodyLength] + fillAmount * fmlts[bodyLength]));
    }

    void update() {
        try {
        synchronized (view != null ? view.modelLock : new Object()){
            long newTime = System.currentTimeMillis();
            double deltaTime = (newTime - lastUpdateTime) / 100.0; // Convert to seconds
            lastUpdateTime = newTime;

            for (Snake snake : activesnakes.values()) {
                snake.update(deltaTime); // Update each snake
            }

            //boost pour snake
            if (this.snake != null) {
                this.snake.updateBoostState(deltaTime);
            }

            activesnakes.values().forEach(cSnake -> {

                double snakeDeltaAngle = angularVelocityFactor * deltaTime * cSnake.getTurnRadiusFactor() * cSnake.getSpeedTurnFactor();
                double snakeDistance = cSnake.speed * deltaTime / 4.0;
                if (snakeDistance > 42) {
                    snakeDistance = 42;
                }
//                System.out.println("Snake updated");
                if (cSnake.targetspeed != cSnake.speed) {
                    if (cSnake.targetspeed < cSnake.speed) {
                        cSnake.targetspeed += 0.3;
                        if (cSnake.targetspeed > cSnake.speed) {
                            cSnake.targetspeed = cSnake.speed;
                        }
                    } else {
                        cSnake.targetspeed -= 0.3;
                        if (cSnake.targetspeed < cSnake.speed) {
                            cSnake.targetspeed = cSnake.speed;
                        }
                    }
                }

                if (cSnake.dir == 1) {
                    cSnake.actualAngle -= snakeDeltaAngle;
                    cSnake.actualAngle %= PI2;
                    if (cSnake.actualAngle < 0) {
                        cSnake.actualAngle += PI2;
                    }
                    double angle2go = (cSnake.wantedAngle - cSnake.actualAngle) % PI2;
                    if (angle2go < 0) {
                        angle2go += PI2;
                    }
                    if (angle2go <= Math.PI) {
                        cSnake.actualAngle = cSnake.wantedAngle;
                        cSnake.dir = 0;
                    }
                } else if (cSnake.dir == 2) {
                    cSnake.actualAngle += snakeDeltaAngle;
                    cSnake.actualAngle %= PI2;
                    if (cSnake.actualAngle < 0) {
                        cSnake.actualAngle += PI2;
                    }
                    double angle2go = (0.0) % PI2;
                    if (angle2go < 0) {
                        angle2go += PI2;
                    }
                    if (angle2go > Math.PI) {
                        cSnake.actualAngle = cSnake.wantedAngle;
                        cSnake.dir = 0;
                    }
                } else {
                    cSnake.actualAngle = cSnake.wantedAngle;
                }

                cSnake.x += Math.cos(cSnake.actualAngle) * snakeDistance;
                cSnake.y += Math.sin(cSnake.actualAngle) * snakeDistance;
            });

            // TODO: eahang
            double preyDeltaAngle = preyAngularVelocityFactor * deltaTime;
            activepreys.values().forEach(prey -> {
                double preyDistance = prey.speed * deltaTime / 4.0;

                if (prey.dir == 1) {
                    prey.actualAngle -= preyDeltaAngle;
                    prey.actualAngle %= PI2;
                    if (prey.actualAngle < 0) {
                        prey.actualAngle += PI2;
                    }
                    double angle2go = (prey.wantedAngle - prey.actualAngle) % PI2;
                    if (angle2go < 0) {
                        angle2go += PI2;
                    }
                    if (angle2go <= Math.PI) {
                        prey.actualAngle = prey.wantedAngle;
                        prey.dir = 0;
                    }
                } else if (prey.dir == 2) {
                    prey.actualAngle += preyDeltaAngle;
                    prey.actualAngle %= PI2;
                    if (prey.actualAngle < 0) {
                        prey.actualAngle += PI2;
                    }
                    double angle2go = (prey.wantedAngle - prey.actualAngle) % PI2;
                    if (angle2go < 0) {
                        angle2go += PI2;
                    }
                    if (angle2go > Math.PI) {
                        prey.actualAngle = prey.wantedAngle;
                        prey.dir = 0;
                    }
                } else {
                    prey.actualAngle = prey.wantedAngle;
                }

                prey.x += Math.cos(prey.actualAngle) * preyDistance;
                prey.y += Math.sin(prey.actualAngle) * preyDistance;
            });

            lastUpdateTime = newTime;
        }


//terrain sans bords :
            for (Snake snake : activesnakes.values()) {
                double headX = snake.x;
                double headY = snake.y;

                // Check boundaries and wrap around if necessary
                if (headX < -worldBoundaryRadius) {
                    snake.x = worldBoundaryRadius - (headX % worldBoundaryRadius);
                } else if (headX > worldBoundaryRadius) {
                    snake.x = -worldBoundaryRadius + (headX % worldBoundaryRadius);
                }

                if (headY < -worldBoundaryRadius) {
                    snake.y = worldBoundaryRadius - (headY % worldBoundaryRadius);
                } else if (headY > worldBoundaryRadius) {
                    snake.y = -worldBoundaryRadius + (headY % worldBoundaryRadius);
                }
            }
            checkFoodCollisions();
    } catch (Exception e) {
        e.printStackTrace(); // This will print any exceptions to the console
    }
    }

    void addSnake(int snakeID, String name, double x, double y, double wantedAngle, double actualangle, double speed, double foodAmount, Deque<SnakeBody> body) {
        synchronized (view != null ? view.modelLock : new Object()) {
            Snake newSnake = new Snake(snakeID, name, x, y, wantedAngle, actualangle, speed, foodAmount, body, this);
            if (snake == null) {
                snake = newSnake;
            }
            activesnakes.put(snakeID, newSnake);
        }
    }

    void checkFoodCollisions() {
        Snake snake = this.snake;
        double snakeHeadRadius = snake.getHeadRadius(); // Using the getHeadRadius method from Snake class

        List<Integer> foodToRemove = new ArrayList<>();
        activefoods.entrySet().forEach(entry -> {
            int foodId = entry.getKey();
            Food food = entry.getValue();
            double dx = snake.x - food.x;
            double dy = snake.y - food.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < snakeHeadRadius + food.getRadius()) {
                foodToRemove.add(foodId);
                // Here, increase snake size or score as appropriate
                snake.setFood(snake.getFood() + food.getSize()); // Increase the food amount by the size of the food
                // Add a new body part to the snake
                double newBodyPartX = snake.x; // You need to calculate this based on the snake's movement
                double newBodyPartY = snake.y; // You need to calculate this based on the snake's movement
                snake.body.addLast(new SnakeBody(newBodyPartX, newBodyPartY)); // Add the new body part at the end of the snake
            }
        });

        foodToRemove.forEach(this::removeFood); // Remove the eaten food from the game
    }

    private void removeFood(int foodId) {
        synchronized (view != null ? view.modelLock : new Object()) {
            activefoods.remove(foodId);
        }
    }


    Snake getSnake(int snakeID) {
        return activesnakes.get(snakeID);
    }

    void removeSnake(int snakeID) {
        synchronized (view != null ? view.modelLock : new Object()) {
            activesnakes.remove(snakeID);
        }
    }

    void addPrey(int id, double x, double y, double radius, int dir, double wang, double ang, double sp) {
        synchronized (view != null ? view.modelLock : new Object()) {
            activepreys.put(id, new Prey(x, y, radius, dir, wang, ang, sp));
        }
    }

    Prey getPrey(int id) {
        return activepreys.get(id);
    }

    void removePrey(int id) {
        synchronized (view != null ? view.modelLock : new Object()) {
            activepreys.remove(id);
        }
    }

    void addFood(int x, int y, double size, boolean fastSpawn) {
        synchronized (view != null ? view.modelLock : new Object()) {
            activefoods.put(y * worldBoundaryRadius * 3 + x, new Food(x, y, size, fastSpawn));
        }
    }



    void addSector(int x, int y) {
        synchronized (view != null ? view.modelLock : new Object()) {
            sectors[y][x] = true;
        }
    }

    void removeSector(int x, int y) {
        synchronized (view != null ? view.modelLock : new Object()) {
            sectors[y][x] = false;
            activefoods.values().removeIf(f -> {
                return f.x / worldsectorSize == x && f.y / worldsectorSize == y;
            });
        }
    }

    public void updatePlayerPosition(Snake player, Double angle, Boolean boost) {
        // on assume que le joueur est un serpent
        if (snake != null) {
            snake.setDirection(angle);
            snake.setBoosting(boost);
        }
    }

    // In SlitherModel class
    public int getWorldBoundaryRadius() {
        return worldBoundaryRadius;
    }


    public int getX() {
        return this.snake != null ? (int) this.snake.x : 0;
    }

    public int getY() {
        return this.snake != null ? (int) this.snake.y : 0;
    }

    public void initializeGameState() {
        // Set up the initial state of the game, like adding snakes, food, and prey
        Deque<SnakeBody> snakeBodyQueue = new ArrayDeque<SnakeBody>();
// populate snakeBodyQueue with SnakeBody objects as needed
        int centerX = worldBoundaryRadius / 2;
        int centerY = worldBoundaryRadius / 2;
        addSnake(1, "Player", centerX, centerY, 0, 0, 4.0, 0, snakeBodyQueue);
        System.out.println("Snake initialized");
        // Add initial food
        for (int i = 0; i < 100; i++) { // for example, add 100 food items
            addFood((int) (Math.random() * (worldBoundaryRadius * 2)) - worldBoundaryRadius,
                    (int) (Math.random() * (worldBoundaryRadius * 2)) - worldBoundaryRadius,
                    1, false); // Random position and size 1
        }

        // Add initial prey
        for (int i = 0; i < 10; i++) { // for example, add 10 prey
            addPrey(i, (int) (Math.random() * 1000), (int) (Math.random() * 1000), 5, 1, Math.random() * 2 * Math.PI, Math.random() * 2 * Math.PI, 6); // Random position, direction and speed
        }

        // Setup the initial sectors
        for (int i = 0; i < sectors.length; i++) {
            for (int j = 0; j < sectors[i].length; j++) {
                addSector(i, j); // Activate all sectors for simplicity
            }
        }
    }
}
