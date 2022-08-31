package com.dk.study.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author qixlin
 * @date 2021/06/21 15:38
 */
public class ImageUtil {

    public static OutputStream convertToJpeg(InputStream in) throws IOException {
        try (ByteArrayOutputStream bas = new ByteArrayOutputStream()) {
            BufferedImage read = ImageIO.read(in);
            BufferedImage bufferedImage = new BufferedImage(read.getWidth(), read.getHeight(), BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics().drawImage(read, 0, 0, Color.white, null);
            ImageIO.write(bufferedImage, "jpg", bas);
            return bas;
        }
    }
}
