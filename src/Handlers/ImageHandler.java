
package Handlers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageHandler {

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(ImageHandler.class.getClassLoader().getResourceAsStream(path)));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void draw(Graphics2D g2,int x, int y, int col, int row, int width, int height, BufferedImage image) {
        g2.drawImage(image, x, y, x + width, y + height, col * width, row * height, (col + 1) * width, (row + 1) * height, null);
    }
}
