package Entitys.Enemies;

import Handlers.CollisionHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class Enemy extends Entitys.Entity {
    // Size of the enemy hitbox
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;

    // Physics constants for jumping and gravity
    private static final double JUMP_FORCE = -8;
    private static final double GRAVITY = 0.8;
    private static final double TERMINAL_VELOCITY = 12;

    private final Vector2 spawnPos; // Original spawn position
    private final int detectionRadiusTiles; // Range for detecting the player
    private boolean jumpedOut = false; // Prevent double jumping from same wall
    private boolean hasStartedChasing = false; // Remember if enemy has ever detected the player

    // Constructor
    public Enemy(Vector2 pos, double speed, int detectionRadiusTiles) {
        super(pos, new Vector2(0, 0), WIDTH, HEIGHT, speed,
                new Rectangle(0, 0, WIDTH, HEIGHT), null, 3, 0);
        this.spawnPos = new Vector2(pos.x, pos.y);
        this.detectionRadiusTiles = detectionRadiusTiles;
    }

    @Override
    public void update() {
        int ts = TiledMap.getScaledTileSize();

        // Determine player's position and distance
        Vector2 playerPos = GamePanel.player.getPosition();
        double distanceX = Math.abs(playerPos.x - spawnPos.x);
        boolean inRadius = distanceX <= detectionRadiusTiles * ts;

        // Begin chasing if the player enters detection range
        if (inRadius) hasStartedChasing = true;

        // Choose target: player if still in range, otherwise return home
        Vector2 target = hasStartedChasing && inRadius ? playerPos : spawnPos;
        double dx = target.x - position.x;
        boolean closeX = Math.abs(dx) <= ts;

        // Check if standing on solid ground
        boolean onGround = CollisionHandler.onGround(this);

        // Horizontal movement toward target if not close
        if (!closeX) {
            velocity.x = Math.signum(dx) * getSpeed();
            direction = velocity.x < 0 ? "left" : "right";

            // Check for horizontal obstacles (wall)
            int checkCol = (int) ((position.x + (velocity.x > 0 ? WIDTH : 0) + velocity.x) / ts);
            int checkRow = (int) (position.y / ts);
            boolean blocked = !GamePanel.tileMap.isWalkable(checkCol, checkRow);

            if (blocked) {
                velocity.x = 0;

                // Jump only if grounded, not already jumped, and space above is clear
                int aboveRow = (int)((position.y - ts) / ts);
                if (onGround && !jumpedOut && aboveRow >= 0 && GamePanel.tileMap.isWalkable(checkCol, aboveRow)) {
                    velocity.y = JUMP_FORCE;
                    jumpedOut = true;
                }
            } else {
                jumpedOut = false; // Reset jump state once clear
            }
        } else {
            velocity.x = 0; // Stop if close enough to the target
        }

        // Apply gravity when in air
        if (!onGround) {
            velocity.y = Math.min(velocity.y + GRAVITY, TERMINAL_VELOCITY);
        } else {
            if (velocity.y > 0) velocity.y = 0;
        }
        super.update();

        // Apply movement to position
        position.y += velocity.y;
        position.x += velocity.x;
    }

    public void hit(int damage, int knockbackX, int knockbackY){}

    @Override
    public void draw(Graphics2D g2) {
        // Get camera position and calculate screen coordinates
        Vector2 cam = GamePanel.tileMap.returnCameraPos();
        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y);

        // Draw red rectangle to represent the enemy
        g2.setColor(Color.RED);
        g2.fillRect(sx, sy, WIDTH, HEIGHT);
    }
}