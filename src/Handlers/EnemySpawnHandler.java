/*
 * EnemySpawnHandler.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Spawns and manages enemies in the game
 */

package Handlers;

import Entitys.Enemies.*;
import Entitys.Enemies.Summoner.Skeleton;
import Entitys.Enemies.Summoner.SkeletonSummoner;
import Entitys.Enemies.TheHive.Hive;
import Entitys.Enemies.TheHive.Wasp;
import Main.Panels.GamePanel;

import java.awt.*;

public class EnemySpawnHandler {

    /**
     * Spawns enemies at game start
     */
    public static void setup() {
        GamePanel.activeEnemies.clear();
        GamePanel.enemies.clear();
        GamePanel.enemyAttacks.clear();

        GamePanel.enemies.add(new HeavySlicer(new Vector2(500, 100)));

        GamePanel.enemies.add(new BloodKing(new Vector2(2650, 3102)));
    }

    /**
     * Updates all enemy objects in the enemies list
     */
    public static void updateAll() {
        int playerRoom = Map.TiledMap.getPlayerRoomId();

        for (int i = 0; i < GamePanel.enemies.size(); i++) {
            Enemy enemy = GamePanel.enemies.get(i);
            int enemyRoom = enemy.getRoomId();
            if (enemyRoom == playerRoom) {
                enemy.update();
            } else {
                GamePanel.activeEnemies.remove(enemy);
                enemy.stopSteps();
            }
        }
    }


    /**
     * Draw each enemy object from the enemies list
     * @param g2 Graphics 2D object to draw enemies
     */
    public static void drawAll(Graphics2D g2) {
        int playerRoom = Map.TiledMap.getPlayerRoomId();
        for (int i = 0; i < GamePanel.enemies.size(); i++) {
            Enemy enemy = GamePanel.enemies.get(i);
            int enemyRoom = enemy.getRoomId();
            if (enemyRoom == playerRoom) {
                enemy.draw(g2);
            }
        }
    }
}