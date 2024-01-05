package tangNdam.slither;

import java.awt.*;
import java.util.Deque;

//Ici Deque est une interface qui
// représente une file d'attente doublement liée.

//On utilise deque pour gerer les parties du corps du serpent.
// C'est nice car on peut modeliser plus facilement l'ajout
//de nouvelles parties du corps lors de la croissance du serpent
//ainsi la suppression de la derniere partie lors de ses deplacemnts

//Que veut dire les suppressions et ajouts de parties du corps?
//Quand le serpent avance, une nouvelle partie du corps est ajoutée a la tete
//et la derniere partie du corps est supprimée.
//C'est pour maintenir la taille constante du serpent.
//Ca nous aide a creer une illusion de mouvement fluide. Ca glisse

//Entite mainTest.Snake,
// avec des propriétés comme la position, la taille, la direction, la vitesse, etc.
//une deque de partie du corps.
public class Snake {
    final int id; // id
    final String name;
    double x, y;
    int dir; // direction
    double wantedAngle, actualAngle; // l'angle ou le serpent veut aller et l'angle actuel
    double speed, targetspeed; // speed and target speed

    private double foodAmount; // food eaten
    final Deque<SnakeBody> body; // body parts
    private final SlitherModel gamemodel; // model

    private boolean boosting;
    public static final double MAX_SCALE = 10.0;

    Snake(int id, String name, double x, double y, double wantedAngle, double actualAngle, double speed, double foodAmount, Deque<SnakeBody> body, SlitherModel gamemodel)  {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.dir = 0;
        this.wantedAngle = wantedAngle; //indique la direction actuelle du serpent dans 1 espace 2D.
        this.actualAngle= actualAngle; //vitesse du serpent quand il change de direction
        this.speed = speed;
        targetspeed = 0; //vitesse du serpent quand different quand il accelere ou ralentit
        this.foodAmount = foodAmount; //quantite de nourriture mangee. Utiliser pour calculer la croissance du serpent
        this.body = body; //represente differentes parties du corps du serpent, chaque segment est un element de la deque
        this.gamemodel = gamemodel;

    }

    double getScale() {
        double baseScale = Math.min(6, 1 + (body.size() - 2) / 106.0);
        double foodScaleFactor = 1 + foodAmount / 100.0; // Assuming each food unit increases size slightly
        return baseScale * foodScaleFactor;
    }
    double getTurnRadiusFactor() { //sert a calculer l'angle du serpent
        return 0.13 + 0.87 * Math.pow((7 - getScale()) / 6, 2);
    }

    double getSpeedTurnFactor() { //sert a calculer la vitesse de rotation du serpent

        return Math.min(speed / gamemodel.speedAnglediv, 1);
    }

    private double getBasespeed() {
        // Basic speed calculation
        double baseSpeed = gamemodel.speedCalculBase + gamemodel.speedCalFactor * getScale();

        // Reduce speed based on the size of the snake
        double sizeFactor = Math.max(0.5, 1 - (body.size() * 0.03)); // Reduces speed slightly as the snake grows

        return baseSpeed * sizeFactor; // Apply the size factor to the speed
    }


    boolean isBoosting() { //sert a calculer si le serpent accelere ou pas

        return targetspeed > getBasespeed();
    }

    double getFood() { //sert a calculer la quantite de nourriture mangee

        return foodAmount;
    }

    void setFood(double food) { //sert a modifier la quantite de nourriture mangee

        this.foodAmount = food;
    }

    public double getHeadRadius() {
        return 6.0 + 4.0 * getScale(); // 6.0 est la taille de la tete du serpent et 4.0 est la taille de chaque partie du corps
    }

    public void setDirection(Double wantedAngle) {
        if (wantedAngle != null) {
            this.wantedAngle = wantedAngle % (2 * Math.PI);

        }
    }

    public void setBoosting(boolean boost) {
        this.targetspeed =boost ? getBasespeed() * 3 : getBasespeed();
    }

    // Call this method to update the snake's position and body
    public void update(double deltaTime) {
        // Update the head position based on the direction
        double speedMultiplier = 2.0; // Adjust this multiplier as needed
        double headX = x + Math.cos(wantedAngle) * speed * deltaTime * speedMultiplier;
        double headY = y + Math.sin(wantedAngle) * speed * deltaTime * speedMultiplier;

        // Update the body segments
        double prevX = x;
        double prevY = y;
        for (SnakeBody bodyPart : body) {
            // Calculate the distance to the previous segment
            double dist = distance(prevX, prevY, bodyPart.x, bodyPart.y);
            // Calculate the target distance
            double targetDist = getSegmentSpacing();

            if (dist > targetDist) {
                // Calculate the interpolation factor (this controls the fluidity)
                double t = (dist - targetDist) / dist;
                // Interpolate the body part position
                bodyPart.x = interpolate(bodyPart.x, prevX, t);
                bodyPart.y = interpolate(bodyPart.y, prevY, t);
            }

            // Update previous segment position
            prevX = bodyPart.x;
            prevY = bodyPart.y;
        }


        // Update the head position
        x = headX;
        y = headY;
    }

    private double getSegmentSpacing() {
        // This should return the distance between segments, which could be a fixed value
        // or a value that changes with the snake's size or other factors.
        return 10; // This is an example value.
    }

    private double distance(double x1, double y1, double x2, double y2) {
        // Calculate the distance between two points
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    // Cette methode sert a calculer la position de chaque partie du corps du serpent
    private double interpolate(double start, double end, double t) {
        // Linearly interpolate between two values
        return start + (end - start) * t;
    }


    // Add a method to start boosting
    public void startBoosting() {
        this.boosting = true;
        this.targetspeed = getBasespeed() * 2; // Example: double the speed
    }

    // Add a method to stop boosting
    public void stopBoosting() {
        this.boosting = false;
        this.targetspeed = getBasespeed();
    }

    // Call this method when updating the snake's state
    public void updateBoostState(double deltaTime) {
        if (this.boosting && this.foodAmount > 0) {
            // Reduce the food amount to simulate "energy" consumption
            this.foodAmount -= deltaTime; // Reduce at a rate of 1 per second
            if (this.foodAmount < 0) {
                this.foodAmount = 0;
            }
            // Set the speed to the target speed while boosting
            this.speed = this.targetspeed;
        } else {
            // Gradually return to the base speed when not boosting
            this.speed = getBasespeed();
        }
    }


}
