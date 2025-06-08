/*
 * EnemySpawnHandler.java
 * Leo Bogaert
 * Jun 7, 2025,
 * Spawns and manages enemies in the game
 */

package Handlers;

import Entitys.Enemies.*;
import Entitys.Enemies.Summoner.SkeletonSummoner;
import Entitys.Enemies.TheHive.Hive;
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

        GamePanel.enemies.add(new Ghoul(new Vector2(500, 240)));
        GamePanel.enemies.add(new Ghoul(new Vector2(660, 240)));

        GamePanel.enemies.add(new CagedShocker(new Vector2(1477, 200)));

        GamePanel.enemies.add(new Ghoul(new Vector2(2456, 208)));
        GamePanel.enemies.add(new Ghoul(new Vector2(2500, 208)));
        GamePanel.enemies.add(new Hive(new Vector2(2600, 98)));

        GamePanel.enemies.add(new Ghoul(new Vector2(2192, 688)));
        GamePanel.enemies.add(new CagedShocker(new Vector2(2742, 520)));
        GamePanel.enemies.add(new Ghoul(new Vector2(1898, 496)));
        GamePanel.enemies.add(new Ghoul(new Vector2(2036, 496)));
        GamePanel.enemies.add(new CagedShocker(new Vector2(2118, 870)));
        GamePanel.enemies.add(new Hive(new Vector2(1895, 687)));

        GamePanel.enemies.add(new Ghoul(new Vector2(851, 720)));
        GamePanel.enemies.add(new CagedShocker(new Vector2(626, 620)));
        GamePanel.enemies.add(new Hive(new Vector2(620, 700)));
        GamePanel.enemies.add(new Ghoul(new Vector2(110, 752)));
        GamePanel.enemies.add(new Ghoul(new Vector2(190, 752)));

        GamePanel.enemies.add(new SkeletonKnight(new Vector2(765, 1195)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(455, 1328)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(964, 1488)));
        GamePanel.enemies.add(new Hive(new Vector2(750, 1490)));
        GamePanel.enemies.add(new Ghoul(new Vector2(100, 1680)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(766, 2000)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(502, 2480)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(970, 2608)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(293, 2192)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(293, 2736)));

        GamePanel.enemies.add(new SkeletonSummoner(new Vector2(158, 3115)));
        GamePanel.enemies.add(new SlicerBot(new Vector2(970, 3024)));

        GamePanel.enemies.add(new Ghoul(new Vector2(1385, 2832)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(1769, 3024)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(1443, 2928)));

        GamePanel.enemies.add(new HeavySlicer(new Vector2(2180, 2670)));
        GamePanel.enemies.add(new SlicerBot(new Vector2(2634, 2640)));
        GamePanel.enemies.add(new HeavySlicer(new Vector2(2997, 2768)));

        GamePanel.enemies.add(new Ghoul(new Vector2(2870, 2224)));
        GamePanel.enemies.add(new Hive(new Vector2(2985, 1560)));
        GamePanel.enemies.add(new SlicerBot(new Vector2(2870, 1264)));

        GamePanel.enemies.add(new SkeletonKnight(new Vector2(1667, 2448)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(1379, 2160)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(1767, 2192)));

        GamePanel.enemies.add(new SlicerBot(new Vector2(2444, 2128)));
        GamePanel.enemies.add(new SkeletonKnight(new Vector2(2235, 1936)));

        GamePanel.enemies.add(new SkeletonSummoner(new Vector2(1418, 1934)));

        GamePanel.enemies.add(new SkeletonSummoner(new Vector2(2234, 1744)));
        GamePanel.enemies.add(new SkeletonSummoner(new Vector2(1793, 1744)));

        GamePanel.enemies.add(new CagedShocker(new Vector2(1474, 1510)));
        GamePanel.enemies.add(new SkeletonSummoner(new Vector2(1931, 1200)));
        GamePanel.enemies.add(new Ghoul(new Vector2(2440, 1424)));
        GamePanel.enemies.add(new Hive(new Vector2(1430, 1995)));


        GamePanel.enemies.add(new BloodKing(new Vector2(2550, 3102)));
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