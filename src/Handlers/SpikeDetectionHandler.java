package Handlers;

import Entitys.Entity;
import Main.Panels.GamePanel;
import Map.TiledMap;

public class SpikeDetectionHandler {

    public static boolean isFacingSpike(Entity e) {
        int ts = TiledMap.getScaledTileSize();
        int checkCol = (int) ((e.getPosition().x + (e.getVelocity().x > 0 ? e.getSolidArea().width : 0) + Math.signum(e.getVelocity().x) * ts) / ts);
        int checkRow = (int) ((e.getPosition().y + e.getSolidArea().height + 5) / ts);
        int tileValue = GamePanel.tileMap.getMapValue(checkCol, checkRow);
        return tileValue == 6;
    }

    public static boolean canLandAfterSpike(Entity e) {
        int ts = TiledMap.getScaledTileSize();
        int forwardCol = (int) ((e.getPosition().x + (e.getVelocity().x > 0 ? e.getSolidArea().width : 0) + Math.signum(e.getVelocity().x) * 2 * ts) / ts);
        int belowRow = (int) ((e.getPosition().y + e.getSolidArea().height + 5) / ts);
        int landingTile = GamePanel.tileMap.getMapValue(forwardCol, belowRow);
        return landingTile == 1 || landingTile == 4;
    }

    public static boolean shouldJumpOverSpike(Entity e) {
        return isFacingSpike(e) && canLandAfterSpike(e);
    }
}



