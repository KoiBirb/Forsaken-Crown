package Main.UI;

import Handlers.ImageHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class UIElement {

    protected int x, y, width, height;
    BufferedImage imageGlow, image;

    public UIElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        image = ImageHandler.loadImage("Assets/Images/UI/UI - Borders and HP/UI - 16x16 UI Tileset.png");
        imageGlow = ImageHandler.loadImage("Assets/Images/UI/UI - Borders and HP/UI - 16x16 UI Tileset with glow.png");
    }

    public abstract void draw(Graphics2D g2);
}
