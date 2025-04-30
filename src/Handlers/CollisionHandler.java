package Handlers;

import Entitys.Entity;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class CollisionHandler {

    private static int[][] collidableTiles;

    public static void checkTileCollision(Entity entity) {
        double entityLeftWorldX = entity.getPosition().x + (entity.getSolidArea().x - entity.getPosition().x) / 2;
        double entityRightWorldX = entityLeftWorldX + entity.getSolidArea().width;
        double entityTopWorldY = entity.getPosition().y + (entity.getSolidArea().y - entity.getPosition().y) / 2;
        double entityBottomWorldY = entityTopWorldY + entity.getSolidArea().height;

        int entityLeftCol = (int) (entityLeftWorldX / TiledMap.getScaledTileSize());
        int entityRightCol = (int) (entityRightWorldX / TiledMap.getScaledTileSize());
        int entityTopRow = (int) (entityTopWorldY / TiledMap.getScaledTileSize());
        int entityBottomRow = (int) (entityBottomWorldY / TiledMap.getScaledTileSize());

        int tileNum1, tileNum2;

        try {
            switch (entity.getDirection()) {
                case "up":
                case "up-left":
                case "up-right":
                    entityTopRow = (int) ((entityTopWorldY - entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityTopRow][entityLeftCol];
                    tileNum2 = collidableTiles[entityTopRow][entityRightCol];

                    if (tileNum1 != 0 || tileNum2 != 0) {
                        entity.setColliding(true);
                        entity.getPosition().y = (entityTopRow + 1) * TiledMap.getScaledTileSize() - ((entity.getSolidArea().y - entity.getPosition().y) / 2) + 2;
                        entity.getVelocity().add(new Vector2(0, -entity.getVelocity().y));
                    }

                    break;
                case "down":
                case "down-left":
                case "down-right":
                    entityBottomRow = (int) ((entityBottomWorldY + entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityBottomRow][entityLeftCol];
                    tileNum2 = collidableTiles[entityBottomRow][entityRightCol];

                    if (tileNum1 != 0 || tileNum2 != 0) {
                        entity.setColliding(true);
                        entity.getPosition().y = entityBottomRow * TiledMap.getScaledTileSize() - entity.getSolidArea().height - ((entity.getSolidArea().y - entity.getPosition().y) / 2) - 9;
                        entity.getVelocity().add(new Vector2(0, entity.getVelocity().y));
                    }
                    break;
            }

            switch (entity.getDirection()) {
                case "left":
                    entityLeftCol = (int) ((entityLeftWorldX - entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityTopRow][entityLeftCol];
                    tileNum2 = collidableTiles[entityBottomRow][entityLeftCol];

                    if (tileNum1 != 0 || tileNum2 != 0) {
                        entity.setColliding(true);
                        entity.getPosition().x = (entityLeftCol + 1) * TiledMap.getScaledTileSize() - ((entity.getSolidArea().x - entity.getPosition().x) / 2) + 10;
                        entity.getVelocity().add(new Vector2(entity.getVelocity().x,0));
                    }
                    break;
                case "right":
                    entityRightCol = (int) ((entityRightWorldX + entity.getSpeed()) / TiledMap.getScaledTileSize());
                    tileNum1 = collidableTiles[entityTopRow][entityRightCol];
                    tileNum2 = collidableTiles[entityBottomRow][entityRightCol];

                    if (tileNum1 != 0 || tileNum2 != 0) {
                        entity.setColliding(true);
                        entity.getPosition().x = entityRightCol * TiledMap.getScaledTileSize() - entity.getSolidArea().width - ((entity.getSolidArea().x - entity.getPosition().x) / 2) - 3;
                        entity.getVelocity().add(new Vector2(-entity.getVelocity().x,0));
                    }
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Player out of bounds: " + e.getMessage());
            entity.getPosition().x = 100;
            entity.getPosition().y = 100;
            entity.setColliding(true);
        }
    }

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
        int entityLeftCol = (int) (x / tileSize);
        int entityRightCol = (int) ((x + width) / tileSize);
        int entityTopRow = (int) (y / tileSize);
        int entityBottomRow = (int) ((y + height) / tileSize);

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


    public static void setCollidableTiles(int[][] collidableTiles) {
        CollisionHandler.collidableTiles = collidableTiles;
    }
}

