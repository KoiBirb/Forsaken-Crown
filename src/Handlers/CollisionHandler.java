package Handlers;

import Entitys.Entity;
import Map.TiledMap;

public class CollisionHandler {

    private static int[][] collidableTiles;

    public static void checkTileCollision (Entity entity){
        double entityLeftWorldX = entity.getPosition().x;
        double entityRightWorldX = entity.getPosition().x + entity.getSolidArea().width;
        double entityTopWorldY = entity.getPosition().y;
        double entityBottomWorldY = entity.getPosition().y + entity.getSolidArea().height;

        int entityLeftCol = (int) (entityLeftWorldX / TiledMap.getScaledTileSize());
        int entityRightCol = (int) (entityRightWorldX / TiledMap.getScaledTileSize());
        int entityTopRow = (int) (entityTopWorldY / TiledMap.getScaledTileSize());
        int entityBottomRow = (int) (entityBottomWorldY / TiledMap.getScaledTileSize());

        int tileNum1, tileNum2;

        try {
            switch (entity.getDirection()) {
                case "up":
                    entityTopRow = (int) ((entityTopWorldY - entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityLeftCol][entityTopRow];
                    tileNum2 = collidableTiles[entityRightCol][entityTopRow];

                    if (tileNum1 == 1 || tileNum2 == 1)
                        entity.setColliding(true);

                    break;
                case "down":
                    entityBottomRow = (int) ((entityBottomWorldY + entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityLeftCol][entityBottomRow];
                    tileNum2 = collidableTiles[entityRightCol][entityBottomRow];

                    if (tileNum1 != 0 || tileNum2 != 0)
                        entity.setColliding(true);

                    break;
                case "left":
                    entityLeftCol = (int) ((entityLeftWorldX - entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityLeftCol][entityTopRow];
                    tileNum2 = collidableTiles[entityLeftCol][entityBottomRow];

                    if (tileNum1 != 0 || tileNum2 != 0)
                        entity.setColliding(true);

                    break;
                case "right":
                    entityRightCol = (int) ((entityRightWorldX + entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityRightCol][entityTopRow];
                    tileNum2 = collidableTiles[entityRightCol][entityBottomRow];

                    if (tileNum1 != 0 || tileNum2 != 0)
                        entity.setColliding(true);
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            entity.setColliding(true);
        }
    }

    public static void setCollidableTiles(int[][] collidableTiles) {
        CollisionHandler.collidableTiles = collidableTiles;
    }
}

