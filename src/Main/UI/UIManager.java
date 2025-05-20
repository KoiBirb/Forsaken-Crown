/*
 * UIManager.java
 * Leo Bogaert
 * May 7, 2025,
 * Manages UI elements
 */
package Main.UI;

import Main.GameState;
import Main.Panels.GamePanel;
import Main.UI.Buttons.ButtonManager;

import java.awt.*;

import static Main.Main.gameState;

public class UIManager {

    HealthBar healthBar;
    ManaBar manaBar;
    ButtonManager buttonManager;

    /**
     * Constructor for UIManager
     * Creates UI elements
     */
    public UIManager() {
        healthBar = new HealthBar(GamePanel.player, 55, 160, 400, 100);
        manaBar = new ManaBar(GamePanel.player, 55, 110, 400, 100);
        buttonManager = new ButtonManager();
    }

    /**
     * Update the UI elements
     */
    public void update() {
        if (gameState == GameState.GAME) {
            healthBar.update();
            manaBar.update();
        } else if (gameState == GameState.MENU) {
            buttonManager.update();
        }
    }

    /**
     * Draw the UI elements
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {
        if (gameState == GameState.GAME) {
            healthBar.draw(g2);
            manaBar.draw(g2);
        } else if (gameState == GameState.MENU) {
            buttonManager.draw(g2);
        }
    }
}
