/*
 * ManaBar.java
 * Leo Bogaert
 * May 7, 2025,
 * Manages and renders the mana bar for an entity
 */

package Main.UI;

import Entitys.Entity;

import java.awt.*;
import java.util.Random;

public class ManaBar extends HealthBar {

    /**
     * Constructor for ManaBar
     * @param entity Entity to which the mana bar belongs to
     * @param x int x position of the mana bar
     * @param y int y position of the mana bar
     * @param width int width of the mana bar
     * @param height int height of the mana bar
     */
    public ManaBar(Entity entity, int x, int y, int width, int height) {
        super(entity, x, y, width, height);
        this.entity = entity;
        this.scale = 4;
        this.shakeOffsetX = 0;
        this.shakeOffsetY = 0;
        this.shakeTimer = 0;

        segments = calculateManaSegments(entity.getCurrentMana());
    }

    /**
     * Update the mana bar
     */
    public void update() {
        int previousMana = segments != null ? calculateTotalMana(segments) : 0;
        segments = calculateManaSegments(entity.getCurrentMana());

        if (entity.getCurrentMana() < previousMana) {
            triggerShake();
        }

        if (shakeTimer > 0) {
            shakeTimer -= 16;
            Random random = new Random();
            shakeOffsetX = random.nextInt(6) - 3;
            shakeOffsetY = random.nextInt(6) - 3;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
    }

    /**
     * Calculate the total mana based on the segments
     * @param segments int[][] segments of mana
     * @return int total mana
     */
    private int calculateTotalMana (int[][] segments) {
        int totalMana = 0;
        if (segments[0][0] == 10) totalMana += 1;
        if (segments[3][0] == 10) totalMana += 1;
        if (segments[1][0] == 11) totalMana += 4 - (segments[1][1] - 8);
        if (segments[2][0] == 11) totalMana += 4 - (segments[2][1] - 8);
        return totalMana;
    }

    /**
     * Calculate the segments of the mana bar based on the current mana
     * @param mana int mana of the entity
     * @return int[][] segments of the mana bar
     */
    public static int[][] calculateManaSegments(int mana) {
        int[][] segments = new int[4][2]; // [leftEdge, body1, body2, rightEdge]

        if (mana > 0) {
            segments[0][0] = 10;
            segments[0][1] = 8;
            mana -= 1;
        } else {
            segments[0][0] = 13;
            segments[0][1] = 8;
        }

        if (mana > 0) {
            int remainingMana = Math.min(4, mana);

            switch (remainingMana) {
                case 4 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 8;
                }
                case 3 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 9;
                }
                case 2 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 10;
                }
                case 1 -> {
                    segments[1][0] = 11;
                    segments[1][1] = 11;
                }
            }

            mana -= remainingMana;
        } else {
            segments[1][0] = 12;
            segments[1][1] = 8;
        }

        if (mana > 0) {
            int remainingMana = Math.min(4, mana);

            switch (remainingMana) {
                case 4 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 8;
                }
                case 3 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 9;
                }
                case 2 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 10;
                }
                case 1 -> {
                    segments[2][0] = 11;
                    segments[2][1] = 11;
                }
            }
            mana -= remainingMana;
        } else {
            segments[2][0] = 12;
            segments[2][1] = 8;
        }

        if (mana > 0) {
            segments[3][0] = 10;
        } else {
            segments[3][0] = 13;
        }
        segments[3][1] = 8;

        return segments;
    }

    /**
     * Draws the mana bar on the screen
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {
        int col, row;

        for (int i = 0; i < 4; i++) {
            col = segments[i][0];
            row = segments[i][1];

            if ((i == 3 && segments[i][0] == 10) || (i == 0 && segments[i][0] == 13)) {
                g2.drawImage(
                        imageGlow,
                        x + shakeOffsetX + i * 16 * scale, y + shakeOffsetY, x + shakeOffsetX + (16 * scale) + (i * 16 * scale), y + shakeOffsetY + 16 * scale,
                        (col + 1) * 16, row * 16, col * 16, (row + 1) * 16,
                        null
                );
            } else {
                g2.drawImage(
                        imageGlow,
                        x + shakeOffsetX + i * 16 * scale, y + shakeOffsetY, x + shakeOffsetX + (16 * scale) + (i * 16 * scale), y + shakeOffsetY + 16 * scale,
                        col * 16, row * 16, (col + 1) * 16, (row + 1) * 16,
                        null
                );
            }
        }
    }
}