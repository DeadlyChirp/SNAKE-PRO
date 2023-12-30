package tangNdam.slither;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;


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
    private static final int DEFAULT_WORLD_BOUNDARY_RADIUS = 1000;
    private static final int DEFAULT_WORLD_SECTOR_SIZE = 50;
    private static final double DEFAULT_SPEED_ANGLE_DIV = 1.0;
    private static final double DEFAULT_SPEED_CALC_BASE = 1.0;
    private static final double DEFAULT_SPEED_CAL_FACTOR = 1.0;
    private static final double DEFAULT_SNAKE_CAL_MODIFIER = 1.0;
    private static final double DEFAULT_ANGULAR_VELOCITY_FACTOR = 1.0;
    private static final double DEFAULT_PREY_ANGULAR_VELOCITY_FACTOR = 1.0;
    private static final double DEFAULT_CST = 1.0;
    private static final int DEFAULT_MAX_SIZE_FOR_SPEED_CALCULATION = 100;


    SlitherModel() {
        this(DEFAULT_WORLD_BOUNDARY_RADIUS, DEFAULT_WORLD_SECTOR_SIZE, DEFAULT_SPEED_ANGLE_DIV,
                DEFAULT_SPEED_CALC_BASE, DEFAULT_SPEED_CAL_FACTOR, DEFAULT_SNAKE_CAL_MODIFIER,
                DEFAULT_ANGULAR_VELOCITY_FACTOR, DEFAULT_PREY_ANGULAR_VELOCITY_FACTOR,
                DEFAULT_CST, DEFAULT_MAX_SIZE_FOR_SPEED_CALCULATION, null);
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
        return (int) (15 * (fpsls[bodyLength] + fillAmount * fmlts[bodyLength]) - 20);
    }

    void update() {
        synchronized (view.modelLock) {
            long newTime = System.currentTimeMillis();

            double deltaTimeWIP = (newTime - lastUpdateTime) / 8.0;
            deltaTimeWIP = Math.min(deltaTimeWIP, 5.0);
            final double deltaTime = deltaTimeWIP;

            activesnakes.values().forEach(cSnake -> {

                double snakeDeltaAngle = angularVelocityFactor * deltaTime * cSnake.getSnkAngle() * cSnake.getSnkspeed();
                double snakeDistance = cSnake.speed * deltaTime / 4.0;
                if (snakeDistance > 42) {
                    snakeDistance = 42;
                }

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
                    double angle2go = (cSnake.wantedAngle - cSnake.wantedAngle) % PI2;
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
    }

    void addSnake(int snakeID, String name, double x, double y, double wantedAngle, double actualangle, double speed, double foodAmount, Deque<SnakeBody> body) {
        synchronized (view.modelLock) {
            Snake newSnake = new Snake(snakeID, name, x, y, wantedAngle, actualangle, speed, foodAmount, body, this);
            if (snake == null) {
                snake = newSnake;
            }
            activesnakes.put(snakeID, newSnake);
        }
    }

    Snake getSnake(int snakeID) {
        return activesnakes.get(snakeID);
    }

    void removeSnake(int snakeID) {
        synchronized (view.modelLock) {
            activesnakes.remove(snakeID);
        }
    }

    void addPrey(int id, double x, double y, double radius, int dir, double wang, double ang, double sp) {
        synchronized (view.modelLock) {
            activepreys.put(id, new Prey(x, y, radius, dir, wang, ang, sp));
        }
    }

    Prey getPrey(int id) {
        return activepreys.get(id);
    }

    void removePrey(int id) {
        synchronized (view.modelLock) {
            activepreys.remove(id);
        }
    }

    void addFood(int x, int y, double size, boolean fastSpawn) {
        synchronized (view.modelLock) {
            activefoods.put(y * worldBoundaryRadius * 3 + x, new Food(x, y, size, fastSpawn));
        }
    }

    void removeFood(int x, int y) {
        synchronized (view.modelLock) {
            activefoods.remove(y * worldBoundaryRadius * 3 + x);
        }
    }

    void addSector(int x, int y) {
        synchronized (view.modelLock) {
            sectors[y][x] = true;
        }
    }

    void removeSector(int x, int y) {
        synchronized (view.modelLock) {
            sectors[y][x] = false;
            activefoods.values().removeIf(f -> {
                return f.x / worldsectorSize == x && f.y / worldsectorSize == y;
            });
        }
    }

    public void updatePlayerPosition(Player player, Double angle, Boolean boost) {
        // on assume que le joueur est un serpent
        if (this.snake != null) {
            this.snake.actualAngle = angle;

            // Si boost, alors la vitesse est plus grande sinon la vitesse est normale

            double boostSpeed = 1.5; // Exemple valeur pour la vitesse boost
            double normalSpeed = 1.0; // Exemple valeur pour la vitesse normale

            this.snake.targetspeed = boost ? boostSpeed : normalSpeed;

            // mise a jour de la vitesse
            if (this.snake.speed < this.snake.targetspeed) {
                this.snake.speed += (this.snake.targetspeed - this.snake.speed) * 0.1; // Augmenter la vitesse
            } else if (this.snake.speed > this.snake.targetspeed) {
                this.snake.speed -= (this.snake.speed - this.snake.targetspeed) * 0.1; // Ralentir la vitesse
            }


        }
    }


    public int getX() {
        return this.snake != null ? (int) this.snake.x : 0;
    }

    public int getY() {
        return this.snake != null ? (int) this.snake.y : 0;
    }


}