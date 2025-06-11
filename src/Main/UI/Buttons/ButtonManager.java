/*
 * ButtonManager.java
 * Leo Bogaert, Benjamin Weir
 * May 20, 2025,
 * Handles menu buttons
 */

package Main.UI.Buttons;

import Handlers.Sound.SoundHandlers.EnemySoundHandler;
import Handlers.Sound.SoundHandlers.PlayerSoundHandler;
import Main.Main;
import Main.Panels.EndPanel;
import Main.Panels.GamePanel;
import Main.Panels.MenuPanel;

import java.awt.*;

import static Main.Main.keyI;

public class ButtonManager {
    private enum ButtonState {MENU,PAUSED,END};
    private ButtonState currentState = ButtonState.MENU;
    private static final float SELECTED_SCALE = 1.15f;
    private static final float NORMAL_SCALE = 1.0f;
    private static final int SHIFT_PIXELS = 20;
    private static final float MOVEMENT_SPEED = 0.15f;

    private final Button[] menuButtons, endButtons, pauseButtons;
    private final float[] scales, shifts;
    private final int[] initialX;

    private int selectedIndex = 1;

    /**
     * Constructor for ButtonManager
     * Initializes buttons with images and positions
     */
    public ButtonManager() {
        menuButtons = new Button[]{
                new Button((int) (GamePanel.screenWidth/5) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words3.png"),
                new Button((int) (GamePanel.screenWidth/2) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words5.png"),
                new Button((int) (GamePanel.screenWidth * (4.0/5) - 169), (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words16.png")
        };

        endButtons = new Button[]{
                new Button((int) (GamePanel.screenWidth/5) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words3.png"),
                new Button((int) (GamePanel.screenWidth/2) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words10.png"),
                new Button((int) (GamePanel.screenWidth * (4.0/5) - 169), (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words17.png")
        };
        pauseButtons = new Button[]{
                new Button((int) (GamePanel.screenWidth/5) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words10.png"),
                new Button((int) (GamePanel.screenWidth/2) - 169, (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words11.png"),
                new Button((int) (GamePanel.screenWidth * (4.0/5) - 169), (int) (GamePanel.screenHeight * (4.6/6.2)), 120,
                        "Images/UI/Words/Words With BG/UI - Words16.png")
        };

        // initial positions and scales
        scales = new float[3];
        shifts = new float[3];
        initialX = new int[3];
        for (int i = 0; i < 3; i++) {
            scales[i] = NORMAL_SCALE;
            shifts[i] = 0;
            initialX[i] = menuButtons[i].getX();
        }
    }

    /**
     * Updates buttons
     */
    public void update() {

        switch (Main.gameState) {

            case MENU:
                currentState = ButtonState.MENU;
                break;

            case DEATH, VICTORY:
                currentState = ButtonState.END;
                break;
        }
        if(GamePanel.isPaused){
            currentState = ButtonState.PAUSED;
        }
        switch (currentState){

            case ButtonState.MENU:
                updateMenu();
                break;
            case ButtonState.END:
                updateEnd();
                break;
            case ButtonState.PAUSED:
                updatePaused();
                break;
        }
    }

    /**
     * Updates the menu buttons
     */
    private void updateMenu() {

        // Selection logic
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
            scales[i] += (targetScale - scales[i]) * MOVEMENT_SPEED;
            shifts[i] += (targetShift - shifts[i]) * MOVEMENT_SPEED;
        }

        // Action Logic
        if (keyI.uPressed) {
            switch (selectedIndex) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    GamePanel.backgroundMusic.fadeIn(GamePanel.backgroundMusic.getMusicMusicDarkMain(),1000);
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

    /**
     * Updates the end buttons
     */
    private void updateEnd() {
        // Selection logic
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

        for (int i = 0; i < endButtons.length; i++) {
            float targetScale = (i == selectedIndex) ? SELECTED_SCALE : NORMAL_SCALE;
            float targetShift = 0;
            if (i != selectedIndex) {
                targetShift = (initialX[i] < initialX[selectedIndex]) ? -SHIFT_PIXELS : SHIFT_PIXELS;
            }
            scales[i] += (targetScale - scales[i]) * MOVEMENT_SPEED;
            shifts[i] += (targetShift - shifts[i]) * MOVEMENT_SPEED;
        }

        // Action Logic
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

    /**
     * Updates the paused buttons
     */
    private void updatePaused() {

        // Selection logic
        if (keyI.aPressed) {
            selectLeft();
            PlayerSoundHandler.UIHover();
            keyI.aPressed = false;
        }
        if (keyI.dPressed) {
            selectRight();
            PlayerSoundHandler.UIHover();
            keyI.dPressed = false;
        }

        for (int i = 0; i < pauseButtons.length; i++) {
            float targetScale = (i == selectedIndex) ? SELECTED_SCALE : NORMAL_SCALE;
            float targetShift = 0;
            if (i != selectedIndex) {
                targetShift = (initialX[i] < initialX[selectedIndex]) ? -SHIFT_PIXELS : SHIFT_PIXELS;
            }
            scales[i] += (targetScale - scales[i]) * MOVEMENT_SPEED;
            shifts[i] += (targetShift - shifts[i]) * MOVEMENT_SPEED;
        }

        // Action Logic
        if (keyI.uPressed) {
            switch (selectedIndex) {
                case 0:
                    GamePanel.isPaused = false;
                    Main.switchToMenu();
                    break;
                case 1:
                    GamePanel.isPaused = false;
                    GamePanel.backgroundMusic.unmuteCurrent();
                    EnemySoundHandler.unmuteAll();
                    PlayerSoundHandler.unmuteAll();
                    break;
                case 2:
                    GamePanel.help = !GamePanel.help;
                    break;
            }
            PlayerSoundHandler.UIConfirm();
            keyI.uPressed = false;
        }
    }

    /**
     * Selects the next left button
     */
    private void selectLeft() {
        selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
    }

    /**
     * Selects the next right button
     */
    private void selectRight() {
        selectedIndex = (selectedIndex + 1) % menuButtons.length;
    }

    /**
     * Gets the index of the currently selected button
     * @return  int index of the selected button
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Draws the buttons on the screen
     * @param g2 Graphics2D object to draw on
     */
    public void draw(Graphics2D g2) {

        Button[] buttons = new Button[3];

        switch (currentState){

            case ButtonState.MENU:
                buttons = menuButtons;
                break;
            case ButtonState.END:
                buttons = endButtons;
                break;
            case ButtonState.PAUSED:
                buttons = pauseButtons;
                break;
        }

        for (int i = 0; i < buttons.length; i++) {
            Button btn = buttons[i];

            int btnWidth = Math.toIntExact(Math.round(btn.getWidth() * scales[i]));
            int btnHeight = Math.toIntExact(Math.round(btn.getHeight() * scales[i]));
            int centerX = (int) (initialX[i] + btn.getWidth() / 2 + Math.round(shifts[i]));
            int centerY = (int) (btn.getY() + btn.getHeight() / 2);

            int x = centerX - btnWidth / 2;
            int y = centerY - btnHeight / 2;

            g2.drawImage(btn.getImage(), x, y, btnWidth, btnHeight, null);
        }
    }
}