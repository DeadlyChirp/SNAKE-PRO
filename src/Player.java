// Definir le joueur qui est capable de se déplacer direction et accelleration

// Path: src/Player.java


import java.awt.*;
import java.awt.event.KeyEvent;

abstract class Player {

    final String name;

    Player(String name) {
        this.name = name;
    }

    public abstract Move action(SlitherModel model);

    static class Move {

        final Double angle;
        final Boolean boost;

        Move(Double angle, Boolean boost) {
            if (angle != null && (angle < 0 || angle >= SlitherModel.PI2)) {
                throw new IllegalArgumentException("angle not in range 0 to PI2");
            }
            this.angle = angle;
            this.boost = boost;
        }
    }

    static class PlayerKeyboard extends Player {
        PlayerKeyboard(String name) {
            super(name);
        }

        @Override
        public Move action(SlitherModel model) {
            // Init angle et boost
            Double angle = null; // Null = pas de changement d'angle
            Boolean boost = false; // False = pas de boost


            if (GameManager.isKeyPressed(KeyEvent.VK_W)) {
                boost = true;
            }
            if (GameManager.isKeyPressed(KeyEvent.VK_A)) {
                angle = model.getCurrentAngle() - 0.1; // Adjust angle left
            }
            if (GameManager.isKeyPressed(KeyEvent.VK_D)) {
                angle = model.getCurrentAngle() + 0.1; // Adjust angle right
            }

            return new Move(angle, boost);
        }

        private Double adjustAngle(SlitherModel model, double adjustment) {
            // Ajouter ici la logique pour ajuster l'angle du serpent
            // Exemple: ajuster l'angle du serpent en fonction de la position de la souris
            return model.getCurrentAngle() + adjustment; // Example adjustment
        }
    }


    class PlayerMouse extends Player {
        PlayerMouse(String name) {
            super(name);
        }

        @Override
        public Move action(SlitherModel model) {
            Point mousePosition = GameManager.getMousePosition();
            Double angle = calculateAngleToMouse(model, mousePosition);
            Boolean boost = GameManager.isMouseClicked();

            return new Move(angle, boost);
        }

        private Double calculateAngleToMouse(SlitherModel model, Point mousePosition) {
            double dx = mousePosition.x - model.getX();
            double dy = mousePosition.y - model.getY();
            return Math.atan2(dy, dx);
        }
    }



}