package Handlers;

import Main.Panels.GamePanel;
import Map.TiledMap;

public class SpikeDetectionHandler {

    public static boolean isFacingSpike(double posX, double posY, double velocityX, int width, int height) {
        int ts = TiledMap.getScaledTileSize();
        int checkCol = (int) ((posX + (velocityX > 0 ? width : 0) + Math.signum(velocityX) * ts) / ts);
        int checkRow = (int) ((posY + height + 5) / ts);
        int tileValue = GamePanel.tileMap.getMapValue(checkCol, checkRow);
        return tileValue == 6;
    }

    public static boolean canLandAfterSpike(double posX, double posY, double velocityX, int width, int height) {
        int ts = TiledMap.getScaledTileSize();
        int forwardCol = (int) ((posX + (velocityX > 0 ? width : 0) + Math.signum(velocityX) * 2 * ts) / ts);
        int belowRow = (int) ((posY + height + 5) / ts);
        int landingTile = GamePanel.tileMap.getMapValue(forwardCol, belowRow);
        return landingTile == 1 || landingTile == 4;
    }
}





