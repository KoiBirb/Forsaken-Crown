package Handlers;

import Entitys.Enemies.BloodKing;
import Entitys.Enemies.Enemy;
import Entitys.Enemies.Ghoul;
import Entitys.Enemies.Summoner.Skeleton;
import Entitys.Enemies.Summoner.SkeletonSummoner;
import Main.Panels.GamePanel;

import java.awt.*;

public class EnemySpawnHandler {

    /**
     * Spawns enemies at game start
     */
    public static void setup() {
        GamePanel.activeEnemies.clear();
        GamePanel.enemies.clear();

        GamePanel.enemies.add(new Ghoul(new Vector2(500, 100)));

        GamePanel.enemies.add(new BloodKing(new Vector2(2650, 3102)));
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