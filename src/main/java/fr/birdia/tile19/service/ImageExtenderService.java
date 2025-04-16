package fr.birdia.tile19.service;

import static fr.birdia.tile19.service.TilesDownloaderService.tileToLatLon;

import fr.birdia.tile19.concurrency.Workers;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImageExtenderService {
  private final TilesDownloaderService tileDownloader;
  private final TilesMergerService tileMerger;
  private final double EARTH_CIRCUMFERENCE_IN_METERS = 40075016.686;
  private final double LAT_KM_PER_DEG = 110.574;
  private final double LON_KM_PER_DEG = 111.320;
  private int imageSize = 1024;
  private Integer x = null;
  private Integer y = null;
  private Integer x1 = null;
  private Integer y1 = null;
  private Integer x2 = null;
  private Integer y2 = null;
  private Workers workers;

  public ImageExtenderService(
      TilesDownloaderService downloader, TilesMergerService merger, Workers workers) {
    this.tileDownloader = downloader;
    this.tileMerger = merger;
    this.workers = workers;
  }

  public double[] computeXYOffsets(double lat, double lon, int x, int y, int z) {
    double[] pixelCoords = convertCoordinatesToPixel(lat, lon, x, y, z);
    return new double[] {
      pixelCoords[0] - 512, pixelCoords[1] - 512, pixelCoords[0], pixelCoords[1]
    };
  }

  public String process(
      int x,
      int y,
      int z,
      String server,
      String layer,
      int shiftNb,
      boolean isCropped,
      double lat,
      double lon)
      throws Exception {

    long totalStart = System.currentTimeMillis();
    this.x = x;
    this.y = y;
    this.x1 = -1;
    this.x2 = 2;
    this.y1 = -1;
    this.y2 = 2;

    if (isCropped) {
      int cropSize = 1024;
      if (server.equals("geoserver_ign")) {
        cropSize = 256;
        imageSize = 256;
      }

      double[] pixelCoords = convertCoordinatesToPixel(lat, lon, x, y, z);
      String base64Data =
          downloadTiles(this.x, this.y, this.x1, this.x2, this.y1, this.y2, z, server, layer);
      byte[] imageBytes = Base64.getDecoder().decode(base64Data);
      BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));

      BufferedImage cropped =
          centerImageOnPoint(image, (int) pixelCoords[0], (int) pixelCoords[1], cropSize);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ImageIO.write(cropped, "jpg", outputStream);

      return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    } else if (shiftNb != 0) {
      this.x2 += shiftNb;
      this.x1 += shiftNb;

      String result =
          downloadTiles(this.x, this.y, this.x1, this.x2, this.y1, this.y2, z, server, layer);

      log.info("Processed with shift in {}ms", System.currentTimeMillis() - totalStart);
      return result;
    } else {
      String result =
          downloadTiles(this.x, this.y, this.x1, this.x2, this.y1, this.y2, z, server, layer);
      log.info("Processed (no crop) in {}ms", System.currentTimeMillis() - totalStart);
      return result;
    }
  }

  public String downloadTiles(
      int x, int y, int x1, int x2, int y1, int y2, int z, String server, String layer)
      throws IOException {
    BufferedImage[][] results = new BufferedImage[y2 - y1][x2 - x1];
    List<Callable<Void>> callables = new ArrayList<>();

    for (int dy = y1; dy < y2; dy++) {
      for (int dx = x1; dx < x2; dx++) {
        final int tileX = x + dx;
        final int tileY = y + dy;
        final int row = dy - y1;
        final int col = dx - x1;

        callables.add(
            () -> {
              BufferedImage img = tileDownloader.download(tileX, tileY, z, server, layer);
              results[row][col] = img;
              return null;
            });
      }
    }

    workers.invokeAll(callables);

    List<List<BufferedImage>> imgGrid =
        Arrays.stream(results).map(Arrays::asList).collect(Collectors.toList());

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      BufferedImage mergedImage = tileMerger.merge(imgGrid);

      ImageIO.write(mergedImage, "jpg", outputStream);
      return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
  }

  public double[] convertCoordinatesToPixel(double lat, double lon, int x, int y, int z) {
    double[] tileOrigin = tileToLatLon(x, y, z);
    double tileLat = tileOrigin[0];
    double tileLon = tileOrigin[1];

    double latRad = Math.toRadians(tileLat);
    double pixelSurfaceInMeters =
        (EARTH_CIRCUMFERENCE_IN_METERS * Math.cos(latRad)) / (Math.pow(2, z) * imageSize);

    double dxInKm = (lon - tileLon) * LON_KM_PER_DEG * Math.cos(latRad);
    double dyInKm = (lat - tileLat) * LAT_KM_PER_DEG;

    double dxInPx = dxInKm * 1000 / pixelSurfaceInMeters;
    double dyInPx = dyInKm * 1000 / pixelSurfaceInMeters;

    return new double[] {dxInPx + imageSize, -dyInPx + imageSize};
  }

  public BufferedImage centerImageOnPoint(BufferedImage image, int x, int y, int cropSize) {
    int left = x - cropSize / 2;
    int top = y - cropSize / 2;

    left = Math.max(left, 0);
    top = Math.max(top, 0);

    int right = Math.min(left + cropSize, image.getWidth());
    int bottom = Math.min(top + cropSize, image.getHeight());

    return image.getSubimage(left, top, right - left, bottom - top);
  }
}
