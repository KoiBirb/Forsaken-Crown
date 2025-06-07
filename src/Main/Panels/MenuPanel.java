/*
 * MenuPanel.java
 * Leo Bogaert
 * May 20, 2025,
 * Main menu panel
 */

package Main.Panels;

import Handlers.ImageHandler;
import Main.UI.UIManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;

import static Main.Main.keyI;

public class MenuPanel extends JPanel implements Runnable{

    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private float helpAlpha = 0f, titleAlpha = 1f;
    private static final float movementFactor = 0.15f;
    private float parallaxSelected = 1.0f;
    public static boolean help = false;

    public UIManager ui;

    public static Thread menuThread;

    private VolatileImage[] background;
    private VolatileImage redCircleBackground, title, helpImage;

    private int row, col, count;

    /**
     * Constructor for the MenuPanel class.
     */
    public MenuPanel(){
        this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setBackground(Color.BLACK);
        this.addKeyListener(keyI);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        loadImages();
    }

    /**
     * Set up the panel
     */
    public void setup() {
        ui = new UIManager(null, false);
        this.requestFocusInWindow();
        keyI.uPressed = false;
        startThread();
    }

    /**
     * Loads images
     */
    private void loadImages(){
        background = new VolatileImage[4];

        background[0] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 1.png");
        background[1] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 2.png");
        background[2] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 3.png");
        background[3] = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/layer 4.png");

        redCircleBackground = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/Red Circle/The Circle 35x37.png");
        title = ImageHandler.loadImage("Assets/Images/UI/Words/Title.png");
        helpImage = ImageHandler.loadImage("Assets/dImages/UI/Words/help.png");
    }

    /**
     * Starts thread
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
        final double drawInterval = 1_000_000_000.0 / 60.0;
        long lastTime = System.nanoTime();
        double delta = 0;

        while (menuThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / drawInterval;
            lastTime = now;

            while (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Updates the menu panel
     */
    public void update() {
        ui.update();
        int target = ui.getSelectedButton();

        parallaxSelected += (target - parallaxSelected) * movementFactor;

        float targetHelpAlpha = help ? 1f : 0f;
        helpAlpha += (targetHelpAlpha - helpAlpha) * movementFactor;

        float targetTitleAlpha = 1f - helpAlpha;
        titleAlpha += (targetTitleAlpha - titleAlpha) * movementFactor;

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
     * Draw menu components
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

        if (helpAlpha > 0.01f) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, helpAlpha));
            g2.drawImage(helpImage, (int) (screenWidth / 2 - helpImage.getWidth() * 1.5),
                    150, (int) (screenWidth / 2 + helpImage.getWidth() * 1.5),
                    150 + (helpImage.getHeight() * 3), 0, 0,
                    helpImage.getWidth(), helpImage.getHeight(), null);
            g2.setComposite(old);
        }
        if (titleAlpha > 0.01f) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
            g2.drawImage(title, (int) (screenWidth / 2 - title.getWidth() * 0.9),
                    150, (int) (screenWidth / 2 + title.getWidth() * 0.9),
                    150 + (int) (title.getHeight() * 1.8), 0, 0,
                    title.getWidth(), title.getHeight(), null);
            g2.setComposite(old);
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
        float scale = 1.1f;

        int bgW = Math.round(screenW * scale);
        int bgH = Math.round(screenH * scale);

        int baseOffset = Math.round((parallaxSelected - 1) * 150);
        float[] parallaxFactors = {1.0f, 0.7f, 0.4f, 0.2f};

        for (int i = 0; i < background.length; i++) {
            VolatileImage bg = background[i];
            float factor = (i < parallaxFactors.length) ? parallaxFactors[i] : 1.0f;
            int offset = Math.round(baseOffset * factor);

            int x = (screenW - bgW) / 2 + offset;
            int y = (screenH - bgH) / 2;

            g2.drawImage(bg, x, y, bgW, bgH, null);
        }
    }
}