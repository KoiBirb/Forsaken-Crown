/*
 * MenuPanel.java
 * Leo Bogaert
 * May 20, 2025,
 * Main menu
 */

package Main.Panels;

import Handlers.ImageHandler;
import Handlers.ScoreHandler;
import Main.UI.UIManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;

import static Main.Main.keyI;

public class EndPanel extends JPanel implements Runnable{

    // Screen settings
    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    public static boolean leader = false;
    public static boolean victory;
    private float parallaxSelected = 1.0f;
    private static final float PARALLAX_LERP_SPEED = 0.15f;

    public UIManager ui;

    public static Thread endThread;

    private VolatileImage[] background;
    private VolatileImage deathTitle, circleBackground, victoryTitle;

    private int row, col, count;

    /**
     * Constructor for the DeathPanel class.
     */
    public EndPanel() {
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setBackground(Color.BLACK);
        this.addKeyListener(keyI);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        loadBackground();
    }

    /**
     * Set up the game.
     */
    public void setup() {
        this.requestFocusInWindow();
        ui = new UIManager(null, false);
        ScoreHandler.addScore(ScoreHandler.generateRandomName(), GamePanel.points);
        ScoreHandler.writeScoresToFile("src/Assets/Map/Leaderboard.txt");
        startThread();
    }

    /**
     * Load background images
     */
    private void loadBackground(){
        background = new VolatileImage[7];

        background[0] = ImageHandler.loadImage("Assets/Images/Backgrounds/Sword Parallax/Color 1/BG1.png");
        background[1] = ImageHandler.loadImage("Assets/Images/Backgrounds/Sword Parallax/Color 1/Close1.png");
        background[2] = ImageHandler.loadImage("Assets/Images/Backgrounds/Sword Parallax/Color 1/Dense Atmostphere1.png");
        background[3] = ImageHandler.loadImage("Assets/Images/Backgrounds/Sword Parallax/Color 1/Far1.png");
        background[4] = ImageHandler.loadImage("Assets/Images/Backgrounds/Sword Parallax/Color 1/Fog1.png");
        background[5] = ImageHandler.loadImage("Assets/Images/Backgrounds/Sword Parallax/Color 1/Mid1.png");


        circleBackground = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/Red Circle/The Circle 35x37 RED.png");
        deathTitle = ImageHandler.loadImage("Assets/Images/UI/UI - Words/DeathTitle.png");
        victoryTitle = ImageHandler.loadImage("Assets/Images/UI/UI - Words/VictoryTitle.png");
    }

    /**
     * Starts game thread
     */
    public void startThread() {
        endThread = new Thread(this);
        endThread.start();
    }

    /**
     * Delta FPS clock to call update and repaint
     */
    @Override
    public void run() {
        double drawInterval = 1000000000.0/60; // 60 FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (endThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if(timer>= 1000000000) {
                // FPS counter
//                System.out.println("FPS:" + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    /**
     * Update game objects
     */
    public void update() {
        ui.update();
        int target = ui.getSelectedButton();

        parallaxSelected += (target - parallaxSelected) * PARALLAX_LERP_SPEED;

        count++;
        if (count > 3) {
            count = 0;
            col++;
            if (col > 5) {
                row++;
                col = 0;
            } else if (row == 5 && col == 1) {
                row = 0;
                col = 0;
            }
        }
    }


    /**
     * Draw game objects
     * @param g Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawParallaxBackground(g2);

        if (victory) {
            g2.drawImage(
                    victoryTitle,
                    (int) (screenWidth / 2 - victoryTitle.getWidth() * 3.5),
                    50,
                    (int) (screenWidth / 2 + victoryTitle.getWidth() * 3.5),
                    50 + victoryTitle.getHeight() * 7,
                    0, 0,
                    victoryTitle.getWidth(), victoryTitle.getHeight(),
                    null
            );
        } else {
            g2.drawImage(deathTitle, (int) (screenWidth / 2 - deathTitle.getWidth() * 0.9),
                    150, (int) (screenWidth / 2 + deathTitle.getWidth() * 0.9),
                    150 + (int) (deathTitle.getHeight() * 1.8), 0, 0,
                    deathTitle.getWidth(), deathTitle.getHeight(), null);
        }
        if (ui != null)
            ui.draw(g2);

        g2.dispose();
    }

    /**
     * Draws the parallax background
     * @param g2 Graphics2D object to draw on
     */
    private void drawParallaxBackground(Graphics2D g2) {
        int screenW = (int) screenWidth;
        int screenH = (int) screenHeight;
        float scale = 1.2f;

        int bgW = Math.round(screenW * scale);
        int bgH = Math.round(screenH * scale);

        int baseOffset = Math.round((parallaxSelected - 1) * 150);
        float[] parallaxFactors = {0.5f, 0.4f, 0.35f, 0.3f, 0.25f, 0.2f, 0.15f};

        for (int i = 0; i < background.length; i++) {

            VolatileImage bg = background[i];
            float factor = (i < parallaxFactors.length) ? parallaxFactors[i] : 1.0f;
            int offset = Math.round(baseOffset * factor);

            if (i == 5) {
                g2.drawImage(
                        circleBackground,
                        (int) (screenWidth / 2 - (250 - 18.5) + offset/2), (int) (screenHeight * (1.76/3) - (250 - 18.5) - 20),
                        (int) (screenWidth / 2 + (250 - 18.5) + offset/2), (int) (screenHeight * (1.76/3) + (250 - 18.5) - 20),
                        col * 35, row * 37,
                        (col + 1) * 35, (row + 1) * 37, null);
            }

            int x = (screenW - bgW) / 2 + offset;
            int y = (screenH - bgH) / 2;

            if (i == 0)
                g2.drawImage(bg, x, y, bgW, bgH, null);
            else
                g2.drawImage(bg, x, y + 40, bgW, bgH + 40, null);
        }
    }
}