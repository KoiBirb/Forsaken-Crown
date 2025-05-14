/*
 * Entity.java
 * Leo Bogaert
 * May 6, 2025,
 * Abstract entity class, defines the basic functionality of entities
 */

package Entitys;

import Handlers.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Entity {

    protected boolean isColliding, jump, onGround, continuousJump;

    protected double speed;
    protected final int width, height;
    protected final int maxHealth, maxMana;
    protected int currentHealth, currentMana;
    protected int solidAreaOffsetX, solidAreaOffsetY;
    protected boolean knockedBack;

    private Set<Point> activeTraps = new HashSet<>();
    private Point currentTrap;
    protected Rectangle solidArea;
    protected String direction;
    protected HashMap<String, Double> directionToRad;
    protected BufferedImage image;
    protected Vector2 position, velocity;

    /**
     * Constructor for Entity
     * @param position Position of the entity
     * @param velocity Velocity of the entity
     * @param width Width of the entity
     * @param height Height of the entity
     * @param speed Speed of the entity
     * @param solidArea Solid area of the entity
     * @param image Image of the entity
     * @param maxHealth Maximum health of the entity
     * @param maxMana Maximum mana of the entity
     */
    public Entity(Vector2 position, Vector2 velocity, int width, int height, double speed, Rectangle solidArea, BufferedImage image, int maxHealth, int maxMana) {
        this.position = position;
        this.velocity = velocity;
        this.width = width;
        this.height = height;
        this.solidAreaOffsetX = solidArea.x;
        this.solidAreaOffsetY = solidArea.y;
        this.speed = speed;
        this.image = image;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.maxMana = maxHealth;
        this.currentMana = maxMana;

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


    /**
     * Update the entity
     */
    public void update() {
        position.add(velocity);
        solidArea.setLocation((int) position.x + solidAreaOffsetX, (int)position.y + solidAreaOffsetY);
    }

    public boolean getFalling(){
        return velocity.y > 2;
    }

    public Point getCurrentTrap() {
        return currentTrap;
    }

    public void setCurrentTrap(Point currentTrap) {
        this.currentTrap = currentTrap;
    }

    /**
     * Draw the entity
     * @param g2 Graphics object to draw on
     */
    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.drawRect((int)position.x, (int)position.y, width, height);
    }

    /**
     * Sets the players on ground status
     * @param onGround true if on ground, false otherwise
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }


    /**
     * Abstract method to handle damage taken by the entity
     */
    public abstract void hit(int damage, int knockbackX, int knockbackY);

    /**
     * Gets the entity's position vector
     * @return Vector2 position
     */
    public Vector2 getPosition(){
        return position;
    }

    /**
     * Sets the players knockback status
     * @param knockback true if knockback, false otherwise
     */
    public void setKnockback(boolean knockback) {
        this.knockedBack = knockback;
    }

    /**
     * Gets the entity's velocity vector
     * @return Vector2 velocity
     */
    public Vector2 getVelocity(){
        return velocity;
    }

    /**
     * Gets the entity's velocity vector
     * @return Vector2 velocity
     */
    public Rectangle getSolidArea(){
        return solidArea;
    }

    /**
     * Gets the entity's direction vector
     * @return Vector2 direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Sets entity's collision state
     */
    public void setColliding(boolean colliding) {
        isColliding = colliding;
    }

    /**
     * Gets the entity's speed
     * @return double speed
     */
    public double getSpeed(){
        return speed;
    }

    /**
     * gets the state of player jumping
     * @return true if jumping, false otherwise
     */
    public boolean getContinuousJump() {
        return continuousJump;
    }

    /**
     * Gets the x component of the entity's solid area
     * @return double x component
     */
    public double getSolidAreaOffsetX(){
        return solidAreaOffsetX;
    }

    /**
     * Gets the y component of the entity's solid area
     * @return double y component
     */
    public double getSolidAreaOffsetY(){
        return solidAreaOffsetY;
    }

    /**
     * returns the entity's max health
     * @return int max health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Gets entity's jumping state
     * @return true if jumping, false otherwise
     */
    public boolean isjumping() {
        return jump;
    }

    /**
     * Gets the entity's current health
     * @return int currentHealth
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Gets the current mana of the entity
     * @return int currentMana
     */
    public int getCurrentMana() {
        return currentMana;
    }

    /**
     * Gets the entity's ground state
     * @return true if on ground, false otherwise
     */
    public boolean isOnGround() {
        return onGround;
    }
}
