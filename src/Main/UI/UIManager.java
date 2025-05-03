package Main.UI;

import Main.Panels.GamePanel;

import java.awt.*;

public class UIManager {

    HealthBar healthBar;

    public UIManager() {
        healthBar = new HealthBar(GamePanel.player, 55, 150, 400, 100);
    }

    public void update() {
        healthBar.update();
    }

    public void draw(Graphics2D g2) {
        healthBar.draw(g2);
    }
}
