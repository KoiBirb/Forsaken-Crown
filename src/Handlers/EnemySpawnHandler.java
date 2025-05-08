package Handlers;

import Entitys.Enemies.Enemy;
import Main.Panels.GamePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EnemySpawnHandler {
    private static final List<Enemy> enemies = new ArrayList<>();

    public static void setup() {
        Vector2 p = GamePanel.player.getPosition();
        int ts = GamePanel.tileMap.getScaledTileSize();

        double spawnX = p.x + 16 * ts;
        double spawnY = ((int)((p.y + ts + 120) / ts)) * ts - Enemy.HEIGHT;

        enemies.clear();
        enemies.add(new Enemy(new Vector2(spawnX, spawnY), 2.5, 8));
    }

    public static void updateAll() {
        for (Enemy e : enemies) e.update();
    }

    public static void drawAll(Graphics2D g2) {
        for (Enemy e : enemies) e.draw(g2);
    }
}