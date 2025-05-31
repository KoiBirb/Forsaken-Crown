/*
 * PlayerDashHeavyAttack.java
 * Leo Bogaert, Heeyoung Shin
 * May 28, 2025,
 * Extends Entity, used as a parent class for all enemies
 */

package Entitys.Enemies;

import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public abstract class Enemy extends Entitys.Entity {

    protected static final double GRAVITY = 0.8;
    protected static final double TERMINAL_VELOCITY = 12;
    protected static final double JUMP_FORCE = -8;

    protected long lastHitTime = 0;
    protected static final long INVINCIBILITY_DURATION_MS = 200;

    protected final Vector2 spawnPos;
    protected final int detectionRadiusTiles, ts;
    protected boolean jumpedOut = false, hasStartedChasing = false;
    protected long lastJumpTime = 0;
    protected static final long JUMP_COOLDOWN_MS = 1000;

    protected int spriteCounter = 0, spriteCol = 0, spriteRow = 0, maxSpriteCol = 0;

    public Enemy(Vector2 pos, double speed, int detectionRadiusTiles, int width, int height, int health, Rectangle solidArea) {
        super(pos, new Vector2(0, 0), width, height, speed,
                solidArea, null, health, 0);

        this.spawnPos = new Vector2(pos.x, pos.y);
        this.detectionRadiusTiles = detectionRadiusTiles;

        ts = TiledMap.getScaledTileSize();
    }

    public boolean canBeHit() {
        return System.currentTimeMillis() - lastHitTime > INVINCIBILITY_DURATION_MS;
    }

    protected boolean hasLineOfSight(Vector2 from, Vector2 to) {
        int tileSize = TiledMap.getScaledTileSize();
        int[][] collidableTiles = Handlers.CollisionHandler.collidableTiles;
        if (collidableTiles == null) return false;

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.hypot(dx, dy);
        int steps = (int) (distance / ((double) tileSize / 4));

        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            double x = from.x + dx * t;
            double y = from.y + dy * t;
            int col = (int) (x / tileSize);
            int row = (int) (y / tileSize);

            if (row < 0 || row >= collidableTiles.length || col < 0 || col >= collidableTiles[0].length)
                return false;

            int tile = collidableTiles[row][col];
            if (tile == 1) return false; // 1 = wall
        }
        return true;
    }

    @Override
    public void update() {

        for (Enemy other : GamePanel.enemies) {
            if (other.getClass() == this.getClass() && other != this && other.getSolidArea().intersects(this.getSolidArea())) {
                Rectangle myArea = this.getSolidArea();
                Rectangle otherArea = other.getSolidArea();
                int dx = (myArea.x + myArea.width / 2) - (otherArea.x + otherArea.width / 2);

                // Push apart horizontally
                int push = 2;
                if (dx > 0) {
                    this.position.x += push;
                    other.position.x -= push;
                } else {
                    this.position.x -= push;
                    other.position.x += push;
                }
            }
        }

        super.update();
    }

    @Override
    public abstract void draw(Graphics2D g2);

    public void death(){
        GamePanel.player.increaseMana(2);
    }
}
