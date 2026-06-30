package fr.birdia.tileonetonine.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TilesMergerService {
  public BufferedImage merge(List<List<BufferedImage>> list2D) {
    List<BufferedImage> mergedRows = list2D.stream().map(this::horizontalConcatenation).toList();
    return verticalConcatenation(mergedRows);
  }

  private BufferedImage horizontalConcatenation(List<BufferedImage> images) {
    int targetHeight = images.stream().mapToInt(BufferedImage::getHeight).min().orElseThrow();
    int totalWidth = images.stream().mapToInt(img -> scaleWidth(img, targetHeight)).sum();

    BufferedImage result = new BufferedImage(totalWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = result.createGraphics();
    applyQualityRenderingHints(g);
    int x = 0;

    for (BufferedImage img : images) {
      int scaledWidth = scaleWidth(img, targetHeight);
      g.drawImage(
          img, x, 0, x + scaledWidth, targetHeight, 0, 0, img.getWidth(), img.getHeight(), null);
      x += scaledWidth;
    }

    g.dispose();
    return result;
  }

  private BufferedImage verticalConcatenation(List<BufferedImage> images) {
    int targetWidth = images.stream().mapToInt(BufferedImage::getWidth).min().orElseThrow();
    int totalHeight = images.stream().mapToInt(img -> scaleHeight(img, targetWidth)).sum();

    BufferedImage result = new BufferedImage(targetWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = result.createGraphics();
    applyQualityRenderingHints(g);
    int y = 0;

    for (BufferedImage img : images) {
      int scaledHeight = scaleHeight(img, targetWidth);
      g.drawImage(
          img, 0, y, targetWidth, y + scaledHeight, 0, 0, img.getWidth(), img.getHeight(), null);
      y += scaledHeight;
    }

    g.dispose();
    return result;
  }

  private int scaleWidth(BufferedImage img, int targetHeight) {
    return img.getWidth() * targetHeight / img.getHeight();
  }

  private int scaleHeight(BufferedImage img, int targetWidth) {
    return img.getHeight() * targetWidth / img.getWidth();
  }

  private void applyQualityRenderingHints(Graphics2D g2d) {
    g2d.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }
}
