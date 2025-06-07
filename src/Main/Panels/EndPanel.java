/*
 * EndPanel.java
 * Leo Bogaert
 * May 20, 2025,
 * End menu panel
 */

package Main.Panels;

import Handlers.ImageHandler;
import Handlers.ScoreHandler;
import Main.UI.UIManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.io.InputStream;

import static Main.Main.keyI;

public class EndPanel extends JPanel implements Runnable{

    public final static double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public final static double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private float leaderAlpha = 0f, titleAlpha = 1f;
    private static final float movementFactor = 0.15f;
    private float parallaxSelected = 1.0f;
    public static boolean leader = false, victory;
    private Font leaderboardFont;

    private int[] scores;
    private String[] names;

    public UIManager ui;

    public static Thread endThread;

    private VolatileImage[] background;
    private VolatileImage deathTitle, circleBackground, victoryTitle, leaderBoardBackground;

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

        loadResources();
    }

    /**
     * Sets up the end panel
     */
    public void setup() {
        this.requestFocusInWindow();
        keyI.uPressed = false;

        ui = new UIManager(null, false);
        ScoreHandler.addScore(Main.Main.name, GamePanel.points);
        ScoreHandler.writeScoresToFile();

        scores = ScoreHandler.getScores();
        names = ScoreHandler.getNames();

        startThread();
    }

    /**
     * Loads resources
     */
    private void loadResources(){
        background = new VolatileImage[7];

        background[0] = ImageHandler.loadImage("Images/Backgrounds/Sword Parallax/BG1.png");
        background[1] = ImageHandler.loadImage("Images/Backgrounds/Sword Parallax/Close1.png");
        background[2] = ImageHandler.loadImage("Images/Backgrounds/Sword Parallax/Dense Atmostphere1.png");
        background[3] = ImageHandler.loadImage("Images/Backgrounds/Sword Parallax/Far1.png");
        background[4] = ImageHandler.loadImage("Images/Backgrounds/Sword Parallax/Fog1.png");
        background[5] = ImageHandler.loadImage("Images/Backgrounds/Sword Parallax/Mid1.png");


        circleBackground = ImageHandler.loadImage("Assets/Images/Backgrounds/The Circle Underground/Red Circle/The Circle 35x37 RED.png");
        deathTitle = ImageHandler.loadImage("Assets/Images/UI/Words/DeathTitle.png");
        victoryTitle = ImageHandler.loadImage("Assets/Images/UI/Words/VictoryTitle.png");
        leaderBoardBackground = ImageHandler.loadImage("Assets/Images/UI/Words/LeaderboardBackground.png");

        try (InputStream is = getClass().getResourceAsStream("/Assets/Font/04B_03__.TTF")) {
            if (is != null) {
                leaderboardFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 48f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(leaderboardFont);
            } else {
                throw new IOException("Font resource not found");
            }
        } catch (Exception e) {
            System.out.println("Failed to load custom leaderboard font, using fallback. " + e.getMessage());
            leaderboardFont = new Font("Arial", Font.BOLD, 48);
        }
    }

    /**
     * Starts thread
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
            final double drawInterval = 1_000_000_000.0 / 60.0;
            long lastTime = System.nanoTime();
            double delta = 0;

            while (endThread != null) {
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
     * Updates UI
     */
    public void update() {
        ui.update();
        int target = ui.getSelectedButton();

        parallaxSelected += (target - parallaxSelected) * movementFactor;

        float targetLeaderAlpha = leader ? 1f : 0f;
        leaderAlpha += (targetLeaderAlpha - leaderAlpha) * movementFactor;

        float targetTitleAlpha = 1f - leaderAlpha;
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
     * Draws the end panel components
     * @param g Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawParallaxBackground(g2);

        // Leaderboard
        if (leaderAlpha > 0.01f) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, leaderAlpha));
            g2.drawImage(leaderBoardBackground, (int) (screenWidth / 2 - leaderBoardBackground.getWidth() * 1.5),
                    65, (int) (screenWidth / 2 + leaderBoardBackground.getWidth() * 1.5),
                    65 + (leaderBoardBackground.getHeight() * 3), 0, 0,
                    leaderBoardBackground.getWidth(), leaderBoardBackground.getHeight(), null);

            String title = "LEADERBOARD";
            g2.setColor(Color.WHITE);

            g2.setFont(leaderboardFont.deriveFont(Font.PLAIN, 80f));
            FontMetrics fm = g2.getFontMetrics();

            int textWidth = fm.stringWidth(title);

            g2.drawString(title, (int) (screenWidth / 2 - textWidth / 2.0), 65 + fm.getAscent() + 30);

            int bgLeft = (int) (screenWidth / 2 - leaderBoardBackground.getWidth() * 1.5);
            int bgWidth = leaderBoardBackground.getWidth() * 3;
            int bgHeight = leaderBoardBackground.getHeight() * 3;

            int col1 = bgLeft + bgWidth / 4;
            int col2 = bgLeft + 3 * bgWidth / 4;
            int startY = 245;
            int rowHeight = 64;

            g2.setFont(leaderboardFont.deriveFont(Font.PLAIN, 36f));
            FontMetrics entryFm = g2.getFontMetrics();

            for (int i = 0; i < 10; i++) {
                String entry = (i + 1) + ". " + (i < names.length ? names[i] : "---") + " - " + (i < scores.length ? scores[i] : "---");
                int x = (i < 5) ? col1 : col2;
                int y = startY + (i % 5) * rowHeight;
                int entryWidth = entryFm.stringWidth(entry);
                g2.drawString(entry, x - entryWidth / 2, y);
            }

            String currentEntry = "Your Score: " + Main.Main.name + " - " + GamePanel.points;
            int currentY = 5 + bgHeight;
            int currentX = (int) (screenWidth / 2 - entryFm.stringWidth(currentEntry) / 2.0);
            g2.drawString(currentEntry, currentX, currentY);

            g2.setComposite(old);
        }

        // Title
        if (titleAlpha > 0.01f) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
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
                        (int) (screenWidth / 2 - (250 - 18.5) + offset/2.0), (int) (screenHeight * (1.76/3) - (250 - 18.5) - 20),
                        (int) (screenWidth / 2 + (250 - 18.5) + offset/2.0), (int) (screenHeight * (1.76/3) + (250 - 18.5) - 20),
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