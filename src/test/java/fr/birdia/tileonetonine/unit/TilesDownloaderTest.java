package fr.birdia.tileonetonine.unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.birdia.tileonetonine.service.TilesDownloaderService;
import fr.birdia.tileonetonine.service.XYZToBBOXService;
import fr.birdia.tileonetonine.service.airbus.AirbusPNEOService;
import fr.birdia.tileonetonine.validator.ImageValidator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

@Disabled
public class TilesDownloaderTest {
  XYZToBBOXService xyzToBBOXService = new XYZToBBOXService();
  RestTemplate restTemplate = new RestTemplate();
  AirbusPNEOService airbusPNEOService =
      new AirbusPNEOService(
          restTemplate,
          System.getenv("AIRBUS_AUTHENTICATION_BASEURL"),
          System.getenv("AIRBUS_API_KEY"),
          System.getenv("AIRBUS_SEARCHAPI_BASEURL"));
  ImageValidator imageValidator = new ImageValidator(restTemplate);
  TilesDownloaderService tilesDownloaderService =
      new TilesDownloaderService(xyzToBBOXService, airbusPNEOService, restTemplate, imageValidator);

  @Test
  public void tiles_downloader_geoserver_ok() throws IOException, InterruptedException {
    int haguenauXtile = 546992;
    int haguenauYTile = 360926;
    int zoom = 20;
    String server = "geoserver";
    String layer = "Bas-Rhin_2023_5cm";
    BufferedImage image =
        tilesDownloaderService.download(haguenauXtile, haguenauYTile, zoom, server, layer);

    assertNotNull(image);
    saveImage(image, haguenauXtile, haguenauYTile, zoom);
  }

  @Test
  void tiles_downloader_ign_ok() throws IOException, InterruptedException {
    int x_19 = 259694;
    int y_19 = 182005;
    int z = 19;
    String server = "geoserver_ign";
    String layer = "Bas-Rhin_2023_5cm";

    BufferedImage image = tilesDownloaderService.download(x_19, y_19, z, server, layer);

    assertNotNull(image);
    saveImage(image, x_19, y_19, z);
  }

  public void saveImage(BufferedImage image, int x, int y, int z) throws IOException {
    File outputDir = new File("src/test/resources/tiles/");
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }
    File outputFile = new File(outputDir, String.format("ign_tile_%d_%d_%d.jpg", x, y, z));
    boolean result = ImageIO.write(image, "jpg", outputFile);
    if (result) {
      System.out.println("Image saved to: " + outputFile.getAbsolutePath());
    } else {
      System.out.println("Failed to save image.");
    }
  }
}
