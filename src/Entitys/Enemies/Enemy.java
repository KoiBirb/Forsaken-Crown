package Entitys.Enemies;

import Handlers.CollisionHandler;
import Handlers.Pathfinding;
import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;
import java.util.List;

public class Enemy extends Entitys.Entity {

    private final Vector2 spawnPos;         // original position
    private final int detectionRadiusTiles; // how far (horizontally) to spot you

    public Enemy(Vector2 position, int speed, int detectionRadiusTiles) {
        super(position, new Vector2(0,0), 32, 32, speed, new Rectangle(0,0,32,32), null, 3);
        this.spawnPos = position;
        this.detectionRadiusTiles = detectionRadiusTiles;
    }

    @Override
    public void update() {
        // Check collisions first
        setColliding(false);
        CollisionHandler.checkTileCollision(this);
        if (isColliding) {
            // If collision occurs, stop
            velocity.x = 0;
        } else {
            Vector2 currentPos = position;
            Vector2 player = GamePanel.player.getPosition();
            int t = TiledMap.getScaledTileSize();

            double dxFromSpawn = player.x - spawnPos.x; // Horizontal distance from spawn to the player
            // Are we within detection range horizontally AND on roughly the same platform?
            boolean inRange = Math.abs(dxFromSpawn) <= detectionRadiusTiles * t && Math.abs(player.y - spawnPos.y) < t;

            // choose goal: player when in range, go after otherwise back to spawn
            Vector2 goal = inRange ? player : spawnPos;

            // Compute A* path from our current location to the goal
            List<Vector2> path = Pathfinding.findPath(currentPos, goal);
            if (path != null && path.size() > 1) {
                // The next waypoint on the grid
                Vector2 next = path.get(1);
                // direction vector
                Vector2 dir = new Vector2(next.x - currentPos.x, next.y - currentPos.y);
                dir.normalize();
                // only move horizontally
                velocity.x = dir.x * getSpeed();
            } else {
                // nowhere to go
                velocity.x = 0;
            }
        }

        // prevent any vertical drift
        velocity.y = 0;
        super.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Vector2 cam = GamePanel.tileMap.getCameraPos();
        int sx = (int)(position.x - cam.x);
        int sy = (int)(position.y - cam.y);

        g2.setColor(Color.RED);
        Rectangle r = getSolidArea();
        g2.fillRect(sx + r.x, sy + r.y, r.width, r.height);
    }
}



