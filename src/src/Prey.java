// entite de proie dans le jeu
// similaire à la classe Food mais peut se deplacer
public class Prey {
    double x, y;
    int dir; // direction
    double currentAngle, angularVelocity; // angle and angular velocity
    double speed;
    private final double size;
    private final long spawnTime;


    //Prey represente une entite de proie dans le jeu
    //comme food mais avec des deplacements
    //qui peut agrandir et se deplacer

    Prey(double x, double y, double size, int dir, double currentAngle, double angularVelocity, double speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.dir = dir;
        this.currentAngle = currentAngle;
        this.angularVelocity = angularVelocity;
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
            angularVelocity = Math.random() * 2 * Math.PI; // nouvelle direction aleatoire
        }

        // mettre a jour la position de la proie basee sur la direction et la vitesse
        x += Math.cos(angularVelocity) * speed;
        y += Math.sin(angularVelocity) * speed;
        // garder la proie dans les limites de l'ecran
        // ajouter ici
    }

    // methode pour verifier si la proie a ete mangee par un serpent
    public boolean checkCollision(Snake snake) {
        // logique pour verifier si la proie a ete mangee par un serpent
        // ...
        return false; // return false pour la detection de collision actuelle
    }

    // mettre a jour l'etat de la proie
    public void updateState() {
        // mettre a jour la position de la proie ou la taille
        // ...
    }


}
