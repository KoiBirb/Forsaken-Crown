/*
 * ButtonManager.java
 * Leo Bogaert
 * May 20, 2025,
 * Handles menu buttons
 */

package Main.UI.Buttons;

import Handlers.Sound.SoundHandlers.PlayerSoundHandler;
import Main.Main;
import Main.Panels.EndPanel;
import Main.Panels.GamePanel;
import Main.Panels.MenuPanel;

import java.awt.*;

import static Main.Main.keyI;

public class ButtonManager {

    private static final float SELECTED_SCALE = 1.15f;
    private static final float NORMAL_SCALE = 1.0f;
    private static final int SHIFT_PIXELS = 20;
    private static final float LERP_SPEED = 0.15f;

    private final Button[] menuButtons, deathButtons;
    private final float[] scales;
    private final float[] shifts;
    private final int[] initialX;
    private int selectedIndex = 1;

    public ButtonManager() {
        menuButtons = new Button[]{
                new Button((int) (GamePanel.screenWidth/5) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words3.png"),
                new Button((int) (GamePanel.screenWidth/2) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words5.png"),
                new Button((int) (GamePanel.screenWidth * (4.0/5) - 169), (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words16.png")
        };

        deathButtons = new Button[]{
                new Button((int) (GamePanel.screenWidth/5) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words3.png"),
                new Button((int) (GamePanel.screenWidth/2) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words10.png"),
                new Button((int) (GamePanel.screenWidth * (4.0/5) - 169), (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words17.png")
        };

        int n = menuButtons.length;
        scales = new float[n];
        shifts = new float[n];
        initialX = new int[n];
        for (int i = 0; i < n; i++) {
            scales[i] = NORMAL_SCALE;
            shifts[i] = 0;
            initialX[i] = menuButtons[i].getX();
        }
    }

    public void update() {

        switch (Main.gameState) {

            case MENU:
                updateMenu();
                break;

            case DEATH, VICTORY:
                updateDeath();
                break;
        }
    }

    private void updateMenu() {
        if (keyI.aPressed) {
            selectLeft();
            PlayerSoundHandler.UIHover();
            keyI.aPressed = false;
            MenuPanel.help = false;
        }
        if (keyI.dPressed) {
            selectRight();
            PlayerSoundHandler.UIHover();
            keyI.dPressed = false;
            MenuPanel.help = false;
        }

        for (int i = 0; i < menuButtons.length; i++) {
            float targetScale = (i == selectedIndex) ? SELECTED_SCALE : NORMAL_SCALE;
            float targetShift = 0;
            if (i != selectedIndex) {
                targetShift = (initialX[i] < initialX[selectedIndex]) ? -SHIFT_PIXELS : SHIFT_PIXELS;
            }
            scales[i] += (targetScale - scales[i]) * LERP_SPEED;
            shifts[i] += (targetShift - shifts[i]) * LERP_SPEED;
        }

        if (keyI.uPressed) {
            switch (selectedIndex) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    Main.switchToGame();
                    break;
                case 2:
                    MenuPanel.help = !MenuPanel.help;
                    break;
            }
            PlayerSoundHandler.UIConfirm();
            keyI.uPressed = false;
        }
    }

    private void updateDeath() {
        if (keyI.aPressed) {
            selectLeft();
            PlayerSoundHandler.UIHover();
            keyI.aPressed = false;
            EndPanel.leader = false;
        }
        if (keyI.dPressed) {
            selectRight();
            PlayerSoundHandler.UIHover();
            keyI.dPressed = false;
            EndPanel.leader = false;
        }

        for (int i = 0; i < deathButtons.length; i++) {
            float targetScale = (i == selectedIndex) ? SELECTED_SCALE : NORMAL_SCALE;
            float targetShift = 0;
            if (i != selectedIndex) {
                targetShift = (initialX[i] < initialX[selectedIndex]) ? -SHIFT_PIXELS : SHIFT_PIXELS;
            }
            scales[i] += (targetScale - scales[i]) * LERP_SPEED;
            shifts[i] += (targetShift - shifts[i]) * LERP_SPEED;
        }

        if (keyI.uPressed) {
            switch (selectedIndex) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    Main.switchToMenu();
                    break;
                case 2:
                    EndPanel.leader = !EndPanel.leader;
                    break;
            }
            PlayerSoundHandler.UIConfirm();
            keyI.uPressed = false;
        }
    }



    private void selectLeft() {
        selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
    }

    private void selectRight() {
        selectedIndex = (selectedIndex + 1) % menuButtons.length;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void draw(Graphics2D g2) {

        Button[] buttons = (Main.gameState == Main.GameState.MENU) ? menuButtons : deathButtons;

        for (int i = 0; i < buttons.length; i++) {
            Button btn = buttons[i];
            boolean selected = (i == selectedIndex);

            int btnWidth = Math.toIntExact(Math.round(btn.getWidth() * scales[i]));
            int btnHeight = Math.toIntExact(Math.round(btn.getHeight() * scales[i]));
            int centerX = (int) (initialX[i] + btn.getWidth() / 2 + Math.round(shifts[i]));
            int centerY = (int) (btn.getY() + btn.getHeight() / 2);

            int x = centerX - btnWidth / 2;
            int y = centerY - btnHeight / 2;

            btn.setHovered(selected);
            g2.drawImage(btn.getImage(), x, y, btnWidth, btnHeight, null);
        }
    }
}