package Main.UI;

import Main.Panels.GamePanel;

import java.awt.*;

public class UIManager {

    HealthBar healthBar;
    ManaBar manaBar;

    public UIManager() {
        healthBar = new HealthBar(GamePanel.player, 55, 160, 400, 100);
        manaBar = new ManaBar(GamePanel.player, 55, 110, 400, 100);
    }

    public void update() {
        healthBar.update();
        manaBar.update();
    }

    public void draw(Graphics2D g2) {
        healthBar.draw(g2);
        manaBar.draw(g2);
    }
}
