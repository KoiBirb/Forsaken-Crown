package Handlers;

import Entitys.Enemy;
import Handlers.Vector2;

import java.awt.Graphics2D;
import java.util.*;
public class EnemySpawnHandler {
    private static final List<Enemy> enemies = new ArrayList<Enemy>();

    // Place enemies
    public static void setup() {
        Vector2 p = Main.Panels.GamePanel.player.getPosition();
        double spawnX = p.x + 150;
        double spawnY = p.y - 22;
        enemies.add(new Enemy(new Vector2(spawnX, spawnY), 5, 40));
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
