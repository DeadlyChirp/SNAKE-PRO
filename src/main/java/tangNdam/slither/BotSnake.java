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
        // Bot logic for changing direction
        if (changeDirectionCooldown <= 0) {
            changeDirection();
            changeDirectionCooldown = randomCooldown();
        }
        changeDirectionCooldown -= deltaTime;
        super.update(deltaTime);
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
