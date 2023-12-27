// Definir le joueur qui est capable de se déplacer direction et accelleration


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
            if (angle != null && (angle < 0 || angle >= PI2)) {
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
            // Implémentez la logique pour lire les entrées clavier (WASD ou flèches)
            // et retournez un nouvel objet Move

            return new Move(null, null);
        }
    }

    class PlayerMouse extends Player {
        PlayerMouse(String name) {
            super(name);
        }

        @Override
        public Move action(SlitherModel model) {
            // Implémentez la logique pour lire les entrées de la souris
            // et retournez un nouvel objet Move

            return new Move(null, null);
        }
    }


}