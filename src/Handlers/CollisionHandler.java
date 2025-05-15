/*
 * CollisionHandler.java
 * Leo Bogaert
 * May 6, 2025,
 * Handles all collisions
 */
package Handlers;

import Entitys.Entity;
import Main.Panels.GamePanel;
import Main.UI.VFX.Hit;
import Map.TiledMap;

import java.awt.*;

import static Main.Panels.GamePanel.keyI;

public class CollisionHandler {

    private static int[][] collidableTiles;

    /**
     * Checks for a tile collision with the given entity.
     * @param entity The entity to check for collision.
     */
    public static void checkTileCollision(Entity entity) {
        double entityTopWorldY = entity.getPosition().y + (entity.getSolidArea().y - entity.getPosition().y) / 2;
        double entityBottomWorldY = entityTopWorldY + entity.getSolidArea().height;
        double entityLeftWorldX = entity.getPosition().x + (entity.getSolidArea().x - entity.getPosition().x) / 2;
        double entityRightWorldX = entityLeftWorldX + entity.getSolidArea().width;

        int entityBottomRow;
        int entityTopRow = (int) (entityTopWorldY / TiledMap.getScaledTileSize());;
        int entityLeftCol = (int) (entityLeftWorldX / TiledMap.getScaledTileSize());
        int entityRightCol = (int) (entityRightWorldX / TiledMap.getScaledTileSize());

        int tileNum1, tileNum2;

        try {
            if (entity.getDirection().contains("up")) {
                // Check top collision
                entityTopRow = (int) ((entityTopWorldY - entity.getSpeed()) / TiledMap.getScaledTileSize());
                tileNum1 = collidableTiles[entityTopRow][entityLeftCol];
                tileNum2 = collidableTiles[entityTopRow][entityRightCol];

                if (tileNum1 == 1 || tileNum2 == 1) {
                    entity.setColliding(true);
                    entity.getPosition().y = (entityTopRow + 1) * TiledMap.getScaledTileSize() - (((entity.getSolidArea().y - entity.getPosition().y) / 2) - 2);
                    entity.getSolidArea().setLocation(entity.getSolidArea().x, (int) (entity.getPosition().y + entity.getSolidAreaOffsetY()));

                } else if ((tileNum1 == 6 || tileNum2 == 6)) {
                    Point trapLocation = new Point(entityLeftCol, entityTopRow);

                    if (!trapLocation.equals(entity.getCurrentTrap())) {
                        entity.hit(1, 0, 0);
                        entity.setCurrentTrap(trapLocation);
                    }
                }
            }

            // Check bottom collision
            entityBottomRow = (int) ((entityBottomWorldY + entity.getSpeed()) / TiledMap.getScaledTileSize());
            tileNum1 = collidableTiles[entityBottomRow][entityLeftCol];
            tileNum2 = collidableTiles[entityBottomRow][entityRightCol];


            if ((tileNum1 == 1 || tileNum2 == 1) ||
                ((tileNum1 == 4 || tileNum2 == 4) &&
                 !keyI.sPressed && entity.getVelocity().y >= 0)) {
                    entity.setOnGround(true);
                    entity.setColliding(true);
                    entity.getPosition().y = entityBottomRow * TiledMap.getScaledTileSize() - entity.getSolidArea().height - ((entity.getSolidArea().y - entity.getPosition().y) / 2)-1;
                    entity.getSolidArea().setLocation(entity.getSolidArea().x, (int) (entity.getPosition().y + entity.getSolidAreaOffsetY()));
           } else if ((tileNum1 == 6 || tileNum2 == 6)) {
                Point trapLocation = new Point(entityLeftCol, entityTopRow);
                if (!trapLocation.equals(entity.getCurrentTrap()) && entity.getFalling()) {
                    entity.hit(1, 0, 10);
                    entity.setCurrentTrap(trapLocation);
                }
                entity.setOnGround(false);
            } else {
                entity.setOnGround(false);
            }

            entityTopWorldY = entity.getPosition().y + (entity.getSolidArea().y - entity.getPosition().y) / 2;
            entityBottomWorldY = entityTopWorldY + entity.getSolidArea().height;
            entityLeftWorldX = entity.getPosition().x + (entity.getSolidArea().x - entity.getPosition().x) / 2;
            entityRightWorldX = entityLeftWorldX + entity.getSolidArea().width;

            entityTopRow = (int) (entityTopWorldY / TiledMap.getScaledTileSize());
            entityBottomRow = (int) (entityBottomWorldY / TiledMap.getScaledTileSize());

            // Check left collision
            if (entity.getDirection().contains("left")) {
                entityLeftCol = (int) ((entityLeftWorldX - entity.getSpeed()) / TiledMap.getScaledTileSize());
                tileNum1 = collidableTiles[entityTopRow][entityLeftCol];
                tileNum2 = collidableTiles[entityBottomRow][entityLeftCol];

                if (tileNum1 == 1 || tileNum2 == 1) {
                    entity.getPosition().x = (entityLeftCol + 1) * TiledMap.getScaledTileSize() - ((entity.getSolidArea().x - entity.getPosition().x) / 2) + 5;

                } else if ((tileNum1 == 6 || tileNum2 == 6)) {
                    Point trapLocation = new Point(entityLeftCol, entityTopRow);

                    if (!trapLocation.equals(entity.getCurrentTrap()) && !entity.getContinuousJump()) {
                        entity.hit(1, 0, 0);
                        entity.setCurrentTrap(trapLocation);
                    }
                }
            }
            // Check right collision
            else if (entity.getDirection().contains("right")) {
                entityRightCol = (int) ((entityRightWorldX + entity.getSpeed()) / TiledMap.getScaledTileSize());
                tileNum1 = collidableTiles[entityTopRow][entityRightCol];
                tileNum2 = collidableTiles[entityBottomRow][entityRightCol];
                if (tileNum1 == 1 || tileNum2 == 1) {
                    entity.getPosition().x = entityRightCol * TiledMap.getScaledTileSize() - entity.getSolidArea().width - ((entity.getSolidArea().x - entity.getPosition().x) / 2) - 5;

                } else if ((tileNum1 == 6 || tileNum2 == 6)) {
                    Point trapLocation = new Point(entityLeftCol, entityTopRow);

                    if (!trapLocation.equals(entity.getCurrentTrap()) && !entity.getContinuousJump()) {
                        entity.hit(1, 0, 0);
                        entity.setCurrentTrap(trapLocation);
                    }
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Player out of bounds: " + e.getMessage());

            // reset the player back to spawn
            entity.getPosition().x = 100;
            entity.getPosition().y = 100;
            entity.setColliding(true);
        }
    }

    /**
     * Checks for an attack collision with a tile
     * @param hitbox The hitbox of the attack
     * @param player The player object that is performing the attack
     * @return True if a collision is detected, false otherwise
     */
    public static boolean checkAttackTileCollision(Rectangle hitbox, Entity player) {
        int tileSize = TiledMap.getScaledTileSize();

        int leftCol = hitbox.x / tileSize;
        int rightCol = (hitbox.x + hitbox.width) / tileSize;
        int topRow = hitbox.y / tileSize;
        int bottomRow = (hitbox.y + hitbox.height) / tileSize;

        double closestDistance = Double.MAX_VALUE;
        int closestTileX = -1;
        int closestTileY = -1;
        boolean flip = false;

        for (int row = topRow; row <= bottomRow; row++) {
            if (row < 0 || row >= collidableTiles.length) continue;

            for (int col = leftCol; col <= rightCol; col++) {
                if (col < 0 || col >= collidableTiles[row].length) continue;

                if (collidableTiles[row][col] != 0 && collidableTiles[row][col] != 6) {
                    int tileX = col * tileSize;
                    int tileY = row * tileSize;

                    double distance = Math.hypot(player.getPosition().x - tileX, player.getPosition().y - tileY);

                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestTileX = tileX;
                        closestTileY = tileY;

                        flip = player.getPosition().x > tileX;
                    }
                }
            }
        }

        if (closestTileX != -1 && closestTileY != -1) {
            new Hit(closestTileX + (flip ? tileSize : 0), closestTileY + tileSize / 2, flip);
            return true;
        }

        return false;
    }

    /**
     * Draws the collision boxes for debugging purposes.
     * @param g2 The Graphics2D object used for drawing.
     * @param entity The entity whose collision box is being drawn.
     */
    public static void draw(Graphics2D g2, Entity entity) {
        g2.setColor(Color.GREEN);

        int x = (int) (entity.getPosition().x + (entity.getSolidArea().x - entity.getPosition().x) / 2);
        int y = (int) (entity.getPosition().y + (entity.getSolidArea().y - entity.getPosition().y) / 2);
        int width = entity.getSolidArea().width;
        int height = entity.getSolidArea().height;

        Vector2 cameraPos = GamePanel.tileMap.getCameraPos();

        int screenX = (int) (x - cameraPos.x);
        int screenY = (int) (y - cameraPos.y);

        g2.drawRect(screenX, screenY, width, height);

        if (collidableTiles == null) return;

        int tileSize = TiledMap.getScaledTileSize();

        g2.setColor(Color.RED);
        int entityLeftCol = x / tileSize;
        int entityRightCol = (x + width) / tileSize;
        int entityTopRow = (y / tileSize);
        int entityBottomRow = (y + height) / tileSize;

        for (int row = entityTopRow; row <= entityBottomRow; row++) {
            for (int col = entityLeftCol; col <= entityRightCol; col++) {
                int tileScreenX = col * tileSize - (int) cameraPos.x;
                int tileScreenY = row * tileSize - (int) cameraPos.y;
                g2.drawRect(tileScreenX, tileScreenY, tileSize, tileSize);
            }
        }

        g2.setColor(Color.BLUE);
        for (int row = 0; row < collidableTiles.length; row++) {
            for (int col = 0; col < collidableTiles[row].length; col++) {
                int tileNum = collidableTiles[row][col];

                if (tileNum == 0) continue;

                screenX = col * tileSize - (int) cameraPos.x;
                screenY = row * tileSize - (int) cameraPos.y;

                g2.drawString(String.valueOf(tileNum), screenX + tileSize / 4, screenY + tileSize / 2);
            }
        }
    }

    /**
     * Checks if the entity is on the ground.
     * @param entity The entity to check.
     * @return True if the entity is on the ground, false otherwise.
     */
    public static boolean onGround(Entity entity) {
        try {
            double entityTopWorldY = entity.getPosition().y + (entity.getSolidArea().y - entity.getPosition().y) / 2;
            double entityBottomWorldY = entityTopWorldY + entity.getSolidArea().height;
            double entityLeftWorldX = entity.getPosition().x + (entity.getSolidArea().x - entity.getPosition().x) / 2;
            double entityRightWorldX = entityLeftWorldX + entity.getSolidArea().width;

            int entityLeftCol = (int) (entityLeftWorldX / TiledMap.getScaledTileSize());
            int entityRightCol = (int) (entityRightWorldX / TiledMap.getScaledTileSize());

            int entityBottomRow = (int) ((entityBottomWorldY + entity.getSpeed()) / TiledMap.getScaledTileSize());
            int tileNum1 = collidableTiles[entityBottomRow][entityLeftCol];
            int tileNum2 = collidableTiles[entityBottomRow][entityRightCol];

            return ((tileNum1 == 1 || tileNum1 == 4) || (tileNum2 == 1 || tileNum2 == 4))
                    && tileNum1 != 6 && tileNum2 != 6;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Player out of bounds: " + e.getMessage());
            entity.getPosition().x = 100;
            entity.getPosition().y = 100;
            return true;
        }
    }

    /**
     * Sets the collidable tiles for the collision handler.
     * @param collidableTiles The 2D array of collidable tiles.
     */
    public static void setCollidableTiles(int[][] collidableTiles) {
        CollisionHandler.collidableTiles = collidableTiles;
    }
}

