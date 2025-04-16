package fr.birdia.tile19.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TilesMergerService {

  public BufferedImage merge(List<List<BufferedImage>> list2D) {
    // Horizontally merge each row
    List<BufferedImage> mergedRows = list2D.stream().map(this::horizontalConcatenation).toList();

    // Vertically merge the rows
    return verticalConcatenation(mergedRows);
  }

  private BufferedImage horizontalConcatenation(List<BufferedImage> images) {
    System.out.println("Not null=" + images);
    int height = images.stream().mapToInt(BufferedImage::getHeight).min().orElseThrow();
    int totalWidth =
        images.stream().mapToInt(img -> img.getWidth() * height / img.getHeight()).sum();

    BufferedImage result = new BufferedImage(totalWidth, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = result.createGraphics();

    int x = 0;
    for (BufferedImage img : images) {
      int newWidth = img.getWidth() * height / img.getHeight();
      Image scaled = img.getScaledInstance(newWidth, height, Image.SCALE_SMOOTH);
      g.drawImage(scaled, x, 0, null);
      x += newWidth;
    }

    g.dispose();
    return result;
  }

  private BufferedImage verticalConcatenation(List<BufferedImage> images) {
    int width = images.stream().mapToInt(BufferedImage::getWidth).min().orElseThrow();
    int totalHeight =
        images.stream().mapToInt(img -> img.getHeight() * width / img.getWidth()).sum();

    BufferedImage result = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = result.createGraphics();

    int y = 0;
    for (BufferedImage img : images) {
      int newHeight = img.getHeight() * width / img.getWidth();
      Image scaled = img.getScaledInstance(width, newHeight, Image.SCALE_SMOOTH);
      g.drawImage(scaled, 0, y, null);
      y += newHeight;
    }

    g.dispose();
    return result;
  }
}
