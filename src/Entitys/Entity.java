package Entitys;

import Handlers.Vector2;

import java.awt.*;

public abstract class Entity {

    Vector2 position;
    private Vector2 velocity;
    private int width;
    private int height;
    private Rectangle Hitbox;

    public Entity(Vector2 position, Vector2 velocity, int width, int height) {
        this.position = position;
        this.velocity = velocity;
        this.width = width;
        this.height = height;

        Hitbox = new Rectangle((int)position.x, (int)position.y, width, height);
    }
    public void update() {
        position.add(velocity);
        Hitbox.setLocation((int)position.x, (int)position.y);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.drawRect((int)position.x, (int)position.y, width, height);
    }
}
