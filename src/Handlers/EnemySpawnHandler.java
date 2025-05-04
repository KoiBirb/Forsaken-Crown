package Handlers;

import Entitys.Enemy;
import Handlers.Vector2;

import java.awt.Graphics2D;
import java.util.*;
public class EnemySpawnHandler {
    private static final List<Enemy> enemies = new ArrayList<Enemy>();

    // Place enemies
    public static void setup() {
        enemies.add(new Enemy(new Vector2(400, 200), 2, 5));
        enemies.add(new Enemy(new Vector2(600, 300), 2, 5));
    }

    /** Call every enemy object to update */
    public static void updateAll() {
        for (Enemy e : enemies) {
            e.update();
        }
    }

    /** Call every enemy object to draw */
    public static void drawAll(Graphics2D g2) {
        for (Enemy e : enemies) {
            e.draw(g2);
        }
    }

    /** Access to the list */
    public static List<Enemy> getAll() {
        return enemies;
    }
}
