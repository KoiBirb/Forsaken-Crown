package Entitys.Enemies;

import Handlers.Vector2;
import Main.Panels.GamePanel;
import Map.TiledMap;

import java.awt.*;

public abstract class Enemy extends Entitys.Entity {
    protected static int WIDTH;
    protected static int HEIGHT;

    protected static final double GRAVITY = 0.8;
    protected static final double TERMINAL_VELOCITY = 12;
    protected static final double JUMP_FORCE = -8;
    protected final int ts;

    protected final Vector2 spawnPos;
    protected final int detectionRadiusTiles;
    protected boolean jumpedOut = false;
    protected boolean hasStartedChasing = false;
    protected long lastJumpTime = 0;
    protected static final long JUMP_COOLDOWN_MS = 1000;

    protected int spriteCounter = 0;
    protected int spriteCol = 0;
    protected int spriteRow = 0;
    protected int maxSpriteCol = 7;

    protected enum State { IDLE, WALK }
    protected State currentState = State.IDLE;

    public Enemy(Vector2 pos, double speed, int detectionRadiusTiles, int width, int height, int health, Rectangle solidArea) {
        super(pos, new Vector2(0, 0), width, height, speed,
                solidArea, null, health, 0);

        WIDTH = width;
        HEIGHT = height;

        this.spawnPos = new Vector2(pos.x, pos.y);
        this.detectionRadiusTiles = detectionRadiusTiles;

        ts = TiledMap.getScaledTileSize();
    }

    protected boolean hasLineOfSight(Vector2 from, Vector2 to) {
        int tileSize = TiledMap.getScaledTileSize();
        int[][] collidableTiles = Handlers.CollisionHandler.collidableTiles;
        if (collidableTiles == null) return false;

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.hypot(dx, dy);
        int steps = (int) (distance / ((double) tileSize / 4)); // step in small increments

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
        super.update();
    }

    @Override
    public abstract void draw(Graphics2D g2);

    @Override
    public abstract void hit(int damage, int knockbackX, int knockbackY);

    public void death(){
        GamePanel.enemies.remove(this);
    }
}
