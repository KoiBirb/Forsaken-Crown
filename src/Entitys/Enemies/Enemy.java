/*
 * Enemy.java
 * Leo Bogaert, Heeyoung Shin
 * May 28, 2025,
 * Extends Entity, used as a parent class for all enemies
 */

package Entitys.Enemies;

import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

import static Main.Panels.GamePanel.activeEnemies;

public abstract class Enemy extends Entitys.Entity {

    protected static final double GRAVITY = 0.8;
    protected static final double TERMINAL_VELOCITY = 12;
    protected static final long INVINCIBILITY_DURATION_MS = 500;

    protected final Vector2 spawnPos;

    protected long lastHitTime = 0;
    protected final int detectionRadiusTiles, ts, roomNumber;
    protected boolean jumpedOut = false, hasStartedChasing = false, canSeePlayer;

    protected int spriteCounter = 0, spriteCol = 0, spriteRow = 0, maxSpriteCol = 0;

    public Enemy(Vector2 pos, double speed, int detectionRadiusTiles, int width, int height, int health, Rectangle solidArea) {
        super(pos, new Vector2(0, 0), width, height, speed,
                solidArea, null, health, 0);

        this.spawnPos = new Vector2(pos.x, pos.y);
        this.detectionRadiusTiles = detectionRadiusTiles;

        ts = TiledMap.getScaledTileSize();
        roomNumber = GamePanel.tileMap.getRoomId(pos.x, pos.y);
    }

    /**
     * Checks if the enemy can be hit based on the last hit time.
     * @return true if the enemy can be hit, false if it is still invincible
     */
    public boolean canBeHit() {
        return System.currentTimeMillis() - lastHitTime > INVINCIBILITY_DURATION_MS;
    }

    /**
     * Gets the room ID of the enemy.
     * @return int room ID of the enemy
     */
    public int getRoomId() {
        return roomNumber;
    }

    /**
     * Checks if the enemy can see the player based on the distance and line of sight.
     * @return true if the enemy can see the player, false otherwise
     */
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
            if (tile == 1) return false;
        }
        return true;
    }

    /**
     * Stops the footsteps sound.
     */
    public void stopSteps(){}

    /**
     * Checks if the enemy is currently playing footsteps sound.
     * @return false, this method is not implemented in the base class
     */
    public boolean getFootstepsPlaying(){
        return false;
    }

    /**
     * Increases the player's mana when the enemy is hit.
     */
    public void hit() {
        GamePanel.player.increaseMana(1);
    }

    /**
     * updates the enemy's state and checks for collisions with other enemies.
     */
    @Override
    public void update() {
        if (canSeePlayer) {
            if (!activeEnemies.contains(this)) {
                activeEnemies.add(this);
            }
        } else {
            activeEnemies.remove(this);
        }

        for (Enemy other : GamePanel.enemies) {
                if (other.getClass() == this.getClass() && other != this && other.getSolidArea().intersects(this.getSolidArea())) {
                    Rectangle myArea = this.getSolidArea();
                    Rectangle otherArea = other.getSolidArea();
                    int dx = (myArea.x + myArea.width / 2) - (otherArea.x + otherArea.width / 2);

                    int push = 2;
                    double newThisX = this.position.x + (dx > 0 ? push : -push);
                    double newOtherX = other.position.x + (dx > 0 ? -push : push);

                    boolean thisOnGround = this.isGroundAhead(newThisX, this.position.y + this.height, dx > 0 ? 1 : -1);
                    boolean otherOnGround = other.isGroundAhead(newOtherX, other.position.y + other.height, dx > 0 ? -1 : 1);

                    if (thisOnGround) {
                        this.position.x = newThisX;
                    }
                    if (otherOnGround) {
                        other.position.x = newOtherX;
                    }
                }
            }

        super.update();
    }

    /**
     * Checks if there is ground ahead of the enemy.
     * @param x double x position of the enemy
     * @param y double y position of the enemy
     * @param direction double direction of the enemy (1 for right, -1 for left)
     * @return true if there is ground ahead, false otherwise
     */
    public abstract boolean isGroundAhead(double x, double y, double direction);

    /**
     * Draws the enemy on the screen.
     * @param g2 Graphics2D object to draw on
     */
    @Override
    public abstract void draw(Graphics2D g2);

    /**
     * Increases player mana on death
     */
    public void death(){
        GamePanel.player.increaseMana(2);
    }
}
