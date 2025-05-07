/*
 * UIManager.java
 * Leo Bogaert
 * May 7, 2025,
 * Manages UI elements
 */
package Main.UI;

import Main.Panels.GamePanel;

import java.awt.*;

public class UIManager {

    HealthBar healthBar;
    ManaBar manaBar;

    /**
     * Constructor for UIManager
     * Creates UI elements
     */
    public UIManager() {
        healthBar = new HealthBar(GamePanel.player, 55, 160, 400, 100);
        manaBar = new ManaBar(GamePanel.player, 55, 110, 400, 100);
    }

    /**
     * Update the UI elements
     */
    public void update() {
        healthBar.update();
        manaBar.update();
    }

    /**
     * Draw the UI elements
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {
        healthBar.draw(g2);
        manaBar.draw(g2);
    }
}
