/*
 * Entity.java
 * Leo Bogaert
 * May 1, 2025,
 * Abstract entity class, defines basic functionality of entities
 */

package Entitys;

import Handlers.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class Entity {

    Vector2 position;
    boolean isColliding;
    Vector2 velocity;
    double speed;
    final int width,height;
    int solidAreaOffsetX, solidAreaOffsetY;
    Rectangle solidArea;
    String direction;
    HashMap<String, Double> directionToRad;
    BufferedImage image;

    public Entity(Vector2 position, Vector2 velocity, int width, int height, int speed, Rectangle solidArea, BufferedImage image) {
        this.position = position;
        this.velocity = velocity;
        this.width = width;
        this.height = height;
        this.solidAreaOffsetX = solidArea.x;
        this.solidAreaOffsetY = solidArea.y;
        this.speed = speed;
        this.image = image;

        this.isColliding = false;
        this.direction = "right";

        directionToRad = new HashMap<>();

        directionToRad.put("up", 0.0);
        directionToRad.put("up-right", 45.0);
        directionToRad.put("right", 90.0);
        directionToRad.put("down-right", 135.0);
        directionToRad.put("down", 180.0);
        directionToRad.put("down-left", 225.0);
        directionToRad.put("left", 270.0);
        directionToRad.put("up-left", 315.0);

        this.solidArea = solidArea;
    }
    public void update() {
        position.add(velocity);
        solidArea.setLocation((int) position.x + solidAreaOffsetX, (int)position.y + solidAreaOffsetY);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.drawRect((int)position.x, (int)position.y, width, height);
    }

    public Vector2 getPosition(){
        return position;
    }

    public Vector2 getVelocity(){
        return velocity;
    }

    public Rectangle getSolidArea(){
        return solidArea;
    }

    public String getDirection() {
        return direction;
    }

    public void setColliding(boolean colliding) {
        isColliding = colliding;
    }

    public double getSpeed(){
        return speed;
    }

    public double getSolidAreaOffsetX(){
        return solidAreaOffsetX;
    }

    public double getSolidAreaOffsetY(){
        return solidAreaOffsetY;
    }

}
