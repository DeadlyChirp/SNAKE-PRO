package tangNdam.slither;

import java.awt.*;
import java.awt.event.KeyEvent;

import static tangNdam.slither.SlitherModel.PI2;

abstract class Player {

    final String name;
//    private Double angle;
//    private Boolean boost;

    Player(String name) {
        this.name = name;
//        this.angle = 0.0; // Start at 0 radians
//        this.boost = false;
    }

    public abstract Wish action(SlitherModel model);

    static class Wish {
        final Double angle;
        final Boolean boost;

        Wish(Double angle, Boolean boost) {
            if (angle != null && (angle < 0 || angle >= PI2)) {
                throw new IllegalArgumentException("angle not in range 0 to PI2");
            }
            this.angle = angle;
            this.boost = boost;
        }
    }

//    public void update(SlitherModel model) {
//        // Update angle and boost based on the mainTest.GameManager's input
//        if (this instanceof PlayerKeyboard) {
//            if (GameManager.isKeyPressed(KeyEvent.VK_W)) {
//                boost = true;
//            } else {
//                boost = false;
//            }
//            if (GameManager.isKeyPressed(KeyEvent.VK_A)) {
//                angle -= 0.1; // Turn left
//            }
//            if (GameManager.isKeyPressed(KeyEvent.VK_D)) {
//                angle += 0.1; // Turn right
//            }
//            angle = angle % (2 * Math.PI); // Ensure the angle stays within the 0 to 2*PI range
//        } else if (this instanceof PlayerMouse) {
//            Point mousePosition = GameManager.getMousePosition();
//            angle = calculateAngleToMouse(model, mousePosition);
//            boost = GameManager.isMouseClicked();
//        }
//
//        // Now, apply the move based on the updated angle and boost
//        applyMove(model);
//    }
//
//    // Apply the move to the model (you'll need to implement this in your model)
//    private void applyMove(SlitherModel model) {
//        model.updatePlayerPosition(this, angle, boost);
//    }
//
//    // PlayerMouse doesn't need to override the action method because it uses the mainTest.GameManager's mouse state directly
//    private Double calculateAngleToMouse(SlitherModel model, Point mousePosition) {
//        double dx = mousePosition.x - model.getX();
//        double dy = mousePosition.y - model.getY();
//        return Math.atan2(dy, dx);
//    }
//
//    // PlayerKeyboard class uses the mainTest.GameManager's keyboard state directly
//    static class PlayerKeyboard extends Player {
//        PlayerKeyboard(String name) {
//            super(name);
//        }
//    }
//
//    static class PlayerMouse extends Player {
//        PlayerMouse(String name) {
//            super(name);
//        }
//    }
}
