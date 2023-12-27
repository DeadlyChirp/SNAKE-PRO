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

//Entite Snake,
// avec des propriétés comme la position, la taille, la direction, la vitesse, etc.
//une deque de partie du corps.
public class Snake {
    final int id; // id
    final String name;
    double x, y;
    int dir; // direction
    double currentAngle, angularVelocity; // angle and angular velocity
    double speed, targetspeed; // speed and target speed
    private double foodAmount; // food eaten
    final Deque<SnakeBody> body; // body parts
    private final SlitherModel gamemodel; // model

    Snake(int id, String name, double x, double y, double currentAngle, double angularVelocity, double speed, double foodAmount, Deque<SnakeBody> body, SlitherModel gamemodel) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.dir = 0;
        this.currentAngle = currentAngle; //indique la direction actuelle du serpent dans 1 espace 2D.
        this.angularVelocity= angularVelocity; //vitesse du serpent quand il change de direction
        this.speed = speed;
        targetspeed = 0; //vitesse du serpent quand different quand il accelere ou ralentit
        this.foodAmount = foodAmount; //quantite de nourriture mangee. Utiliser pour calculer la croissance du serpent
        this.body = body; //represente differentes parties du corps du serpent, chaque segment est un element de la deque
        this.gamemodel = gamemodel;
    }
}
