/*
 * GamePanel.java
 * Leo Bogaert
 * May 7, 2025,
 * Main game loop
 */

package Main.Panels;

import Handlers.ImageHandler;
import Handlers.Vector2;
import Main.KeyInput;
import Main.UI.UIManager;
import Map.TiledMap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static Main.Main.keyI;
import static Main.Panels.GamePanel.player;

public class MenuPanel extends JPanel implements Runnable{

    // Screen settings
    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private float parallaxSelected = 1.0f;
    private static final float PARALLAX_LERP_SPEED = 0.15f;

    public static UIManager ui = new UIManager();

    public static Thread menuThread;

    private BufferedImage[] background;
    private BufferedImage redCircleBackground;
    private BufferedImage title;

    private int row, col, count;

    /**
     * Constructor for the GamePanel class.
     */
    public MenuPanel(){
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
    public void setupGame() {
        this.requestFocusInWindow();
        startThread();
    }

    private void loadBackground(){
        background = new BufferedImage[4];

        background[0] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 1.png");
        background[1] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 2.png");
        background[2] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 3.png");
        background[3] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 4.png");

        redCircleBackground = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/Red Circle/The Circle 35x37.png");
        title = ImageHandler.loadImage("Assets/Images/UI/UI - Words/Title.png");
    }

    /**
     * Starts game thread
     */
    public void startThread() {
        menuThread = new Thread(this);
        menuThread.start();
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

        while (menuThread != null) {

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

        g2.drawImage(
                redCircleBackground,
                (int) (screenWidth / 2 - (250 - 18.5) / 2), (int) (screenHeight * (1.76/3) - (250 - 18.5) / 2),
                (int) (screenWidth / 2 + (250 - 18.5) / 2), (int) (screenHeight * (1.76/3) + (250 - 18.5) / 2),
                col * 35, row * 37,
                (col + 1) * 35, (row + 1) * 37, null);

        g2.drawImage(title, (int)(screenWidth/2 - title.getWidth()*0.9),
                150, (int)(screenWidth/2 + title.getWidth()*0.9),
                150 + (int)(title.getHeight()*1.8), 0, 0,
                title.getWidth(), title.getHeight(), null);

        ui.draw(g2);

        g2.dispose();
    }

    private void drawParallaxBackground(Graphics2D g2) {
        int screenW = (int) screenWidth;
        int screenH = (int) screenHeight;
        float scale = 1.1f;

        int bgW = Math.round(screenW * scale);
        int bgH = Math.round(screenH * scale);

        int baseOffset = Math.round((parallaxSelected - 1) * 150);
        float[] parallaxFactors = {1.0f, 0.7f, 0.4f, 0.2f};

        for (int i = 0; i < background.length; i++) {
            BufferedImage bg = background[i];
            float factor = (i < parallaxFactors.length) ? parallaxFactors[i] : 1.0f;
            int offset = Math.round(baseOffset * factor);

            int x = (screenW - bgW) / 2 + offset;
            int y = (screenH - bgH) / 2;

            g2.drawImage(bg, x, y, bgW, bgH, null);
        }
    }
}
