package Entitys;

import Handlers.CollisionHandler;
import Handlers.Pathfinding;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.util.List;

public class Enemy extends Entity{

    private final int detectionRadius; // How many tiles away the enemy will start chasing the player

    public Enemy(Vector2 position, int speed, int detectionRadius) {
        super(position, new Vector2(0, 0), 32, 32, speed, new Rectangle(0, 0, 32, 32), null, 3);
        this.detectionRadius = detectionRadius;
    }

    @Override
    public void update() {
        // Reset collision state, then check tile collisions
        setColliding(false);
        CollisionHandler.checkTileCollision(this);

        if (isColliding) {
            // Stop on collision
            velocity = new Vector2(0, 0);
        } else {
            // Calculate xy component distance to player
            Vector2 playerPos = GamePanel.player.getPosition();
            double dx = Math.abs(playerPos.x - position.x);
            double dy = Math.abs(playerPos.y - position.y);
            int tileSize = TiledMap.getScaledTileSize();

            // If enemy is within the detection radius, pathfind toward the player
            if (dx + dy <= detectionRadius * tileSize) {
                List<Vector2> path = Pathfinding.findPath(position, playerPos);
                if (path != null && path.size() > 1) {
                    Vector2 next = path.get(1);
                    Vector2 dir = new Vector2(next.x - position.x, next.y - position.y);
                    dir.normalize();
                    dir.multiplyScalar(getSpeed());
                    velocity = dir;
                } else {
                    // No path or already at target
                    velocity = new Vector2(0, 0);
                }
            } else {
                // Player out of range
                velocity = new Vector2(0, 0);
            }
        }

        super.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        // Figure out where the enemy should appear on-screen
        Vector2 camera = GamePanel.tileMap.getCameraPos();
        int screenX = (int)(position.x - camera.x);
        int screenY = (int)(position.y - camera.y);

        // Draw the enemy as a red rectangle
        g2.setColor(Color.RED);
        Rectangle r = getSolidArea();
        g2.fillRect(screenX + r.x, screenY + r.y, r.width, r.height);
    }
}
