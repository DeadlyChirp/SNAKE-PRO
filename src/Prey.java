// entite de proie dans le jeu
// similaire à la classe Food mais peut se deplacer
public class Prey {
    double x, y;
    int dir; // direction
    double wantedAngle, actualAngle; // l'angle ou le serpent veut aller et l'angle actuel
    double speed;
    private final double size;
    private final long spawnTime;



    //Prey represente une entite de proie dans le jeu
    //comme food mais avec des deplacements
    //qui peut agrandir et se deplacer

    Prey(double x, double y, double size, int dir, double wantedAngle, double actualAngle, double speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.dir = dir;
        this.wantedAngle = wantedAngle; //indique la direction actuelle du serpent dans 1 espace 2D.
        this.actualAngle= actualAngle; //indique l'angle actuel du serpent
        this.speed = speed;

        this.spawnTime = System.currentTimeMillis();
    }

    double getRadius() { // Prendre en compte la croissance au fil du temps
        double fillRate = (System.currentTimeMillis() - spawnTime) / 1200.0;
        if (fillRate >= 1) {
            return size;
        } else {
            return (1 - Math.cos(Math.PI * fillRate)) / 2 * size;
        }
    }


    // methode pour deplacer la proie
    public void move() {
        // logique de deplacement au hasard de la proie
        // Exemple : la proie se deplace dans une direction aleatoire
        if (Math.random() < 0.1) { // change la direction 10% du temps
            actualAngle = Math.random() * 2 * Math.PI; // nouvelle direction aleatoire
        }

        // mettre a jour la position de la proie basee sur la direction et la vitesse
        x += Math.cos(actualAngle) * speed;
        y += Math.sin(actualAngle) * speed;
        // garder la proie dans les limites de l'ecran
        // ajouter ici
        if (x < 0) x = 0; // Replace 0 with minimum x boundary
        if (y < 0) y = 0; // Replace 0 with minimum y boundary

    }

    // methode pour verifier si la proie a ete mangee par un serpent
    public boolean checkCollision(Snake snake) {
        double snakeHeadX = snake.x; // Assume snake has x and y properties for its head
        double snakeHeadY = snake.y;
        double distance = Math.sqrt(Math.pow(snakeHeadX - x, 2) + Math.pow(snakeHeadY - y, 2));
        return distance < (getRadius() + snake.getHeadRadius()); // Assuming Snake has a method to get head radius
    }


    // mettre a jour l'etat de la proie
    public void updateState() {
        // mettre a jour la position de la proie ou la taille
        // ...
    }


}
