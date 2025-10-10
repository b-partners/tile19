package fr.birdia.tile19.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import org.springframework.stereotype.Service;

@Service
public class ImageDegraderService {

  public BufferedImage applyOpacityMask(BufferedImage src, float opacity, Color color) {
    if (src == null) {
      throw new IllegalArgumentException("Source image cannot be null");
    }

    int width = src.getWidth();
    int height = src.getHeight();

    BufferedImage masked = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = masked.createGraphics();

    g2d.drawImage(src, 0, 0, null);

    // Apply opaque layer
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    g2d.setColor(color);
    g2d.fillRect(0, 0, width, height);

    g2d.dispose();
    return masked;
  }
}
