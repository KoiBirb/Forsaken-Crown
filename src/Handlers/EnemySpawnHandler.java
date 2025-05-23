package Handlers;

import Entitys.Enemies.Enemy;
import Main.Panels.GamePanel;

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
        Vector2 p = GamePanel.player.getPosition();
        int ts = GamePanel.tileMap.getScaledTileSize();

        // Spawn enemy 16 tiles to the right of the player, placed on the same Y-level
        double spawnX = p.x + 16 * ts;
        double spawnY = ((int)((p.y + ts + 120) / ts)) * ts - Enemy.HEIGHT;

        enemies.clear(); // Remove existing enemies
        enemies.add(new Enemy(new Vector2(spawnX, spawnY), 1, 8)); // Add new one
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