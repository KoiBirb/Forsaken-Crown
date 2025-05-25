package Handlers;

import Entitys.Enemies.Enemy;
import Entitys.Enemies.Ghoul;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EnemySpawnHandler {
    // List to hold all enemies
    private static final List<Enemy> enemies = new ArrayList<>();

    /**
     * Spawns enemies at game start
     */
    public static void setup() {

        // Spawn enemy 16 tiles to the right of the player, placed on the same Y-level
        double spawnX = 400;
        double spawnY = 100;

        enemies.clear(); // Remove existing enemies
        enemies.add(new Ghoul(new Vector2(spawnX, spawnY), 1, 8)); // Add new one
    }

    /**
     * Updates all enemy objects in enemies list
     */
    public static void updateAll() {
        for (Enemy e : enemies) e.update();
    }

    /**
     * Draw each enemy object from enemies list
     * @param g2 Graphics 2D object to draw enemies
     */
    public static void drawAll(Graphics2D g2) {
        for (Enemy e : enemies) e.draw(g2);
    }
}