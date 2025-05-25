package Entitys.Enemies;

import Handlers.ImageHandler;
import Handlers.Vector2;
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

    public Enemy(Vector2 pos, double speed, int detectionRadiusTiles, int width, int height) {
        super(pos, new Vector2(0, 0), width, height, speed,
                new Rectangle(0, 0, width, height), null, 3, 0);

        WIDTH = width;
        HEIGHT = height;

        this.spawnPos = new Vector2(pos.x, pos.y);
        this.detectionRadiusTiles = detectionRadiusTiles;

        ts = TiledMap.getScaledTileSize();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public abstract void draw(Graphics2D g2);

    @Override
    public abstract void hit(int damage, int knockbackX, int knockbackY);
}
