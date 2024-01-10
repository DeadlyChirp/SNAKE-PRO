package tangNdam.slither;

import java.util.Deque;
import java.util.Random;

public class BotSnake extends Snake {
    private double changeDirectionCooldown = 0;

    // Constructor
    public BotSnake(int id, String name, double x, double y, double wantedAngle, double actualAngle, double speed, double foodAmount, Deque<SnakeBody> body, SlitherModel gamemodel, Player player) {
        super(id, name, x, y, wantedAngle, actualAngle, speed, foodAmount, body, gamemodel, player);
    }



    @Override
    public void update(double deltaTime) {
        super.update(deltaTime); // Call the base update logic

        // Update the cooldown timer
        if (changeDirectionCooldown > 0) {
            changeDirectionCooldown -= deltaTime;
        } else {
            // Time to change direction
            Random rand = new Random();
            this.wantedAngle = rand.nextDouble() * 2 * Math.PI;
            changeDirectionCooldown = 5.0; // 5 seconds until next direction change
        }
    }
}

