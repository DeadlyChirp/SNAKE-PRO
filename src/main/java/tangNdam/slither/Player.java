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

}
