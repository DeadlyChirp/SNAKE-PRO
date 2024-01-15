package tangNdam.slither;

import java.util.Deque;
import java.util.Random;

public class BotSnake extends Snake {
    private double changeDirectionCooldown;
    static final Random random = new Random();

    // Constructor
    public BotSnake(int id, String name, double x, double y, double wantedAngle, double actualAngle, double speed, double foodAmount, Deque<SnakeBody> body, SlitherModel gamemodel) {
        super(id, name, x, y, wantedAngle, actualAngle, speed, foodAmount, body, gamemodel);
        changeDirectionCooldown = randomCooldown();
    }

    @Override
    public void update(double deltaTime) {
        // Check for nearby food and set direction towards it
        Food targetFood = findNearestFood();
        if (targetFood != null) {
            setDirectionTowardsFood(targetFood);
        }

        // Existing logic for changing direction
        if (changeDirectionCooldown <= 0) {
            changeDirection();
            changeDirectionCooldown = randomCooldown();
        }
        changeDirectionCooldown -= deltaTime;

        super.update(deltaTime);
    }

    private Food findNearestFood() {
        // Find the nearest food. This is a simplified logic.
        Food nearestFood = null;
        double minDistance = Double.MAX_VALUE;
        for (Food food : SlitherModel.activefoods.values()) {
            double distance = calculateDistance(this.x, this.y, food.x, food.y);
            if (distance < minDistance) {
                minDistance = distance;
                nearestFood = food;
            }
        }
        return nearestFood;
    }

    private void setDirectionTowardsFood(Food food) {
        double angleToFood = Math.atan2(food.y - this.y, food.x - this.x);
        this.wantedAngle = angleToFood;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void changeDirection() {
        // Logic to change the bot's direction
        // Here, we set a new random angle for the bot snake
        this.wantedAngle = random.nextDouble() * 2 * Math.PI; // Random angle between 0 and 2*PI
    }

    private double randomCooldown() {
        // Returns a random cooldown duration between 5 and 10 seconds
        return 5 + random.nextDouble() * 5;
    }
}
