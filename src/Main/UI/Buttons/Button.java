package Main.UI.Buttons;

import Handlers.ImageHandler;
import Main.UI.UIElement;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button extends UIElement {

    private boolean isHovered;

    public Button(int x, int y, int height, String imagePath) {
        super(x, y, (int) (height * 2.818181), height);

        this.x = x;
        this.y = y;
        this.height = height;
        this.width = (int) (height * 2.818181);
        this.isHovered = false;

        image = ImageHandler.loadImage(imagePath);
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, width, height, null);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
