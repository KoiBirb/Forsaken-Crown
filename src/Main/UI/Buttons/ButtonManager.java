
package Main.UI.Buttons;

import Main.Panels.GamePanel;
import Main.Panels.MenuPanel;

import java.awt.*;

import static Main.Main.keyI;

public class ButtonManager {

    private static final float SELECTED_SCALE = 1.15f;
    private static final float NORMAL_SCALE = 1.0f;
    private static final int SHIFT_PIXELS = 20;
    private static final float LERP_SPEED = 0.15f;

    private Button[] buttons;
    private float[] scales;
    private float[] shifts;
    private int[] initialX;
    private int selectedIndex = 0; // Start with the middle button

    public ButtonManager() {
        buttons = new Button[]{
                new Button((int) (GamePanel.screenWidth/2) - 169, (int) (GamePanel.screenHeight * (4.0/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words5.png"),
                new Button((int) (GamePanel.screenWidth/5) - 169, (int) (GamePanel.screenHeight * (4.0/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words3.png"),
                new Button((int) (GamePanel.screenWidth * (4.0/5) - 169), (int) (GamePanel.screenHeight * (4.0/6.2)), 120,
                        "Assets/Images/UI/UI - Words/Words With BG/UI - Words16.png")
        };
        int n = buttons.length;
        scales = new float[n];
        shifts = new float[n];
        initialX = new int[n];
        for (int i = 0; i < n; i++) {
            scales[i] = NORMAL_SCALE;
            shifts[i] = 0;
            initialX[i] = buttons[i].getX();
        }
    }

    public void update() {
        if (keyI.aPressed) {
            selectRight();
            keyI.aPressed = false;
        }
        if (keyI.dPressed) {
            selectLeft();
            keyI.dPressed = false;
        }

        for (int i = 0; i < buttons.length; i++) {
            float targetScale = (i == selectedIndex) ? SELECTED_SCALE : NORMAL_SCALE;
            float targetShift = 0;
            if (i != selectedIndex) {
                targetShift = (initialX[i] < initialX[selectedIndex]) ? -SHIFT_PIXELS : SHIFT_PIXELS;
            }
            scales[i] += (targetScale - scales[i]) * LERP_SPEED;
            shifts[i] += (targetShift - shifts[i]) * LERP_SPEED;
        }

        if (keyI.uPressed) {
            switch(selectedIndex) {
                case 0:
                    Main.Main.switchToGame();
                    break;
                case 1:
                    System.exit(0);
                    break;
                case 2:
                    System.out.println("Controls");
                    break;
            }
        };
    }

    private void selectLeft() {
        selectedIndex = (selectedIndex - 1 + buttons.length) % buttons.length;
    }

    private void selectRight() {
        selectedIndex = (selectedIndex + 1) % buttons.length;
    }

    public void draw(Graphics2D g2) {
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