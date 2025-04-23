
package Handlers;

import javax.imageio.ImageIO;
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
}
