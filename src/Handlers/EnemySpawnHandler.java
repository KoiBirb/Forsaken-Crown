package Handlers;

import Entitys.Enemies.Enemy;
import Entitys.Enemies.Ghoul;
import Main.Panels.GamePanel;

import java.awt.*;

public class EnemySpawnHandler {

    /**
     * Spawns enemies at game start
     */
    public static void setup() {
        GamePanel.enemies.clear(); // Remove existing enemies
        GamePanel.enemies.add(new Ghoul(new Vector2(600, 100)));
    }

    /**
     * Updates all enemy objects in enemies list
     */
    public static void updateAll() {
        int playerRoom = Map.TiledMap.getRoomId(GamePanel.player.getPosition().x, GamePanel.player.getPosition().y);
        for (int i = 0; i < GamePanel.enemies.size(); i++) {
            Enemy enemy = GamePanel.enemies.get(i);
            int enemyRoom = Map.TiledMap.getRoomId(enemy.getPosition().x, enemy.getPosition().y);
            if (enemyRoom == playerRoom) {
                enemy.update();
            }
        }
    }

    /**
     * Draw each enemy object from enemies list
     *
     * @param g2 Graphics 2D object to draw enemies
     */
    public static void drawAll(Graphics2D g2) {
        int playerRoom = Map.TiledMap.getPlayerRoomId();
        for (int i = 0; i < GamePanel.enemies.size(); i++) {
            Enemy enemy = GamePanel.enemies.get(i);
            int enemyRoom = Map.TiledMap.getRoomId(enemy.getPosition().x, enemy.getPosition().y);
            if (enemyRoom == playerRoom) {
                enemy.draw(g2);
            }
        }
    }
}