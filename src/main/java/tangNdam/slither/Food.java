package tangNdam.slither;//Gère les entités alimentaires avec des attributs
// comme la position, la taille,
// le temps d'apparition et
// les mécaniques de croissance.


public class Food {
    final int x, y; // position
    private final double size; // initial size
    private final double respawn; // rate of respawn
    private final long spawnTime; // in milliseconds

    Food(int x, int y, double size, boolean fastSpawn) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.respawn = fastSpawn ? 4 : 1;
        spawnTime = System.currentTimeMillis();
    }

    double getSize() {
        return size;
    }

    double getRadius() {
        double fillRate = respawn * (System.currentTimeMillis() - spawnTime) / 1200;
        if (fillRate >= 1) {
            return size;
        } else {
            return (1 - Math.cos(Math.PI * fillRate)) / 2 * size;
        }
    }



}
