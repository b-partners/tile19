package fr.birdia.tile19.service;

import static fr.birdia.tile19.service.TilesDownloaderService.tileToLatLon;

import fr.birdia.tile19.concurrency.Workers;
import fr.birdia.tile19.model.airbus.AirbusProperties;
import fr.birdia.tile19.service.airbus.AirbusPNEOService;
import fr.birdia.tile19.validator.ImageValidator;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
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
  private final String AIRBUS_SERVER = "airbus";
  private Integer x = null;
  private Integer y = null;
  private Integer x1 = null;
  private Integer y1 = null;
  private Integer x2 = null;
  private Integer y2 = null;
  private Workers workers;
  private ImageDegraderService imageDegraderService;
  private AirbusPNEOService airbusPNEOService;
  private AirbusProperties airbusProperties;
  private ImageValidator imageValidator;

  public ImageExtenderService(
      TilesDownloaderService downloader,
      TilesMergerService merger,
      Workers workers,
      ImageDegraderService imageDegraderService,
      AirbusPNEOService airbusPNEOService,
      ImageValidator imageValidator) {
    this.tileDownloader = downloader;
    this.tileMerger = merger;
    this.workers = workers;
    this.imageDegraderService = imageDegraderService;
    this.airbusPNEOService = airbusPNEOService;
    this.imageValidator = imageValidator;
  }

  public String getLastUpdatedAtAirbus() {
    return this.airbusProperties.getUpdatedAt();
  }

  public double[] computeXYOffsets(double lat, double lon, int x, int y, int z, String server) {
    double[] pixelCoords = convertCoordinatesToPixel(lat, lon, x, y, z);
    return server.equals("geoserver_ign") || server.equals("airbus")
        ? new double[] {pixelCoords[0] - 128, pixelCoords[1] - 128, pixelCoords[0], pixelCoords[1]}
        : new double[] {pixelCoords[0] - 512, pixelCoords[1] - 512, pixelCoords[0], pixelCoords[1]};
  }

  public String process(
      int x,
      int y,
      int z,
      String server,
      String layer,
      int shiftNb,
      String shiftDirection,
      boolean isCropped,
      double lat,
      double lon,
      boolean isOpaque)
      throws Exception {
    this.x = x;
    this.y = y;
    this.x1 = -1;
    this.x2 = 2;
    this.y1 = -1;
    this.y2 = 2;

    if (isCropped) {
      int cropSize = 1024;
      if (server.equals("geoserver_ign") || server.equals("airbus")) {
        cropSize = 256;
        this.imageSize = 256;
      }

      double[] pixelCoords = convertCoordinatesToPixel(lat, lon, x, y, z);
      byte[] imageBytes =
          downloadTilesBytes(
              this.x, this.y, this.x1, this.x2, this.y1, this.y2, z, server, layer, isOpaque, lat,
              lon);
      BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
      BufferedImage cropped =
          centerImageOnPoint(image, (int) pixelCoords[0], (int) pixelCoords[1], cropSize);

      return convertImageToBase64(cropped);
    } else if (shiftNb != 0 && Objects.equals(shiftDirection, "RIGHT_LEFT_SIDE")) {
      this.x2 += shiftNb;
      this.x1 += shiftNb;

      return downloadTilesBytesAndConvertToBase64(z, server, layer, isOpaque, lat, lon);
    } else if (shiftNb != 0 && Objects.equals(shiftDirection, "UP_DOWN_SIDE")) {
      this.y2 += shiftNb;
      this.y1 += shiftNb;

      return downloadTilesBytesAndConvertToBase64(z, server, layer, isOpaque, lat, lon);
    }
    return downloadTilesBytesAndConvertToBase64(z, server, layer, isOpaque, lat, lon);
  }

  public String convertImageToBase64(BufferedImage image) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", outputStream);
    return Base64.getEncoder().encodeToString(outputStream.toByteArray());
  }

  public String downloadTilesBytesAndConvertToBase64(
      int z, String server, String layer, boolean isOpaque, double lat, double lon)
      throws IOException {
    byte[] imageBytes =
        downloadTilesBytes(
            this.x, this.y, this.x1, this.x2, this.y1, this.y2, z, server, layer, isOpaque, lat,
            lon);
    BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
    return convertImageToBase64(image);
  }

  public byte[] downloadTilesBytes(
      int x,
      int y,
      int x1,
      int x2,
      int y1,
      int y2,
      int z,
      String server,
      String layer,
      boolean isOpaque,
      double lat,
      double lon)
      throws IOException {

    BufferedImage[][] results = new BufferedImage[y2 - y1][x2 - x1];
    List<Callable<Void>> callables = new ArrayList<>();
    final boolean isAirbusServer = AIRBUS_SERVER.equals(server);
    this.airbusProperties =
        isAirbusServer ? airbusPNEOService.retrieveAirbusProperties(lat, lon) : null;

    for (int dy = y1; dy < y2; dy++) {
      for (int dx = x1; dx < x2; dx++) {
        final int tileX = x + dx;
        final int tileY = y + dy;
        final int row = dy - y1;
        final int col = dx - x1;

        callables.add(
            () -> {
              BufferedImage img;

              if (isAirbusServer) {
                img = tileDownloader.download(tileX, tileY, z, airbusProperties);
              } else {
                img = tileDownloader.download(tileX, tileY, z, server, layer);
              }
              results[row][col] = img;
              return null;
            });
      }
    }

    workers.invokeAll(callables);

    List<List<BufferedImage>> imgGrid =
        Arrays.stream(results).map(Arrays::asList).collect(Collectors.toList());
    BufferedImage mergedImage = tileMerger.merge(imgGrid);

    if (isOpaque) {
      BufferedImage masked =
          imageDegraderService.applyOpacityMask(mergedImage, 0.5f, new Color(255, 255, 255));
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ImageIO.write(masked, "jpg", outputStream);
      return outputStream.toByteArray();
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(mergedImage, "jpg", outputStream);
    return outputStream.toByteArray();
  }

  public String extendExistingTiles(List<BufferedImage> images) throws IOException {
    List<List<BufferedImage>> list2D =
        List.of(images.subList(0, 3), images.subList(3, 6), images.subList(6, 9));

    BufferedImage mergedImage = tileMerger.merge(list2D);
    return convertImageToBase64(mergedImage);
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
