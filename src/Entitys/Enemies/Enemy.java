package Entitys.Enemies;

import Handlers.CollisionHandler;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public class Enemy extends Entitys.Entity {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    private static final double JUMP_FORCE = -8;
    private static final double GRAVITY = 0.8;
    private static final double TERMINAL_VELOCITY = 12;

    private final Vector2 spawnPos;
    private final int detectionRadiusTiles;
    private boolean jumpedOut = false;
    private boolean hasStartedChasing = false;

    public Enemy(Vector2 pos, double speed, int detectionRadiusTiles) {
        super(pos, new Vector2(0, 0), WIDTH, HEIGHT, speed,
                new Rectangle(0, 0, WIDTH, HEIGHT), null, 3, 0);
        this.spawnPos = new Vector2(pos.x, pos.y);
        this.detectionRadiusTiles = detectionRadiusTiles;
    }

    @Override
    public void update() {
        int ts = TiledMap.getScaledTileSize();

        Vector2 playerPos = GamePanel.player.getPosition();
        double distanceX = Math.abs(playerPos.x - spawnPos.x);
        boolean inRadius = distanceX <= detectionRadiusTiles * ts;

        if (inRadius) hasStartedChasing = true;

        Vector2 target = hasStartedChasing && inRadius ? playerPos : spawnPos;
        double dx = target.x - position.x;
        boolean closeX = Math.abs(dx) <= ts;

        boolean onGround = CollisionHandler.onGround(this);

        if (!closeX) {
            velocity.x = Math.signum(dx) * getSpeed();
            direction = velocity.x < 0 ? "left" : "right";

            int checkCol = (int) ((position.x + (velocity.x > 0 ? WIDTH : 0) + velocity.x) / ts);
            int checkRow = (int) (position.y / ts);
            boolean blocked = !GamePanel.tileMap.isWalkable(checkCol, checkRow);

            if (blocked) {
                velocity.x = 0;

                int aboveRow = (int)((position.y - ts) / ts);
                if (onGround && !jumpedOut && aboveRow >= 0 && GamePanel.tileMap.isWalkable(checkCol, aboveRow)) {
                    velocity.y = JUMP_FORCE;
                    jumpedOut = true;
                }
            } else {
                jumpedOut = false;
            }
        } else {
            velocity.x = 0;
        }

        // Apply vertical physics BEFORE collision
        if (!onGround) {
            velocity.y = Math.min(velocity.y + GRAVITY, TERMINAL_VELOCITY);
        } else {
            if (velocity.y > 0) velocity.y = 0;
        }
        super.update();
        position.y += velocity.y;


        // Then handle horizontal movement
        position.x += velocity.x;
    }

    @Override
    public void draw(Graphics2D g2) {
        Vector2 cam = GamePanel.tileMap.returnCameraPos();
        int sx = (int) (position.x - cam.x);
        int sy = (int) (position.y - cam.y);

        g2.setColor(Color.RED);
        g2.fillRect(sx, sy, WIDTH, HEIGHT);
    }
}