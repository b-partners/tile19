package fr.birdia.tile19.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.birdia.tile19.concurrency.Workers;
import fr.birdia.tile19.service.ImageExtenderService;
import fr.birdia.tile19.service.TilesDownloaderService;
import fr.birdia.tile19.service.TilesMergerService;
import fr.birdia.tile19.service.XYZToBBOXService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ImageExtenderServiceTest {
  XYZToBBOXService xyzToBBOXService = new XYZToBBOXService();
  TilesDownloaderService downloader = new TilesDownloaderService(xyzToBBOXService);
  TilesMergerService merger = new TilesMergerService();
  Workers workers = new Workers();
  ImageExtenderService extender = new ImageExtenderService(downloader, merger, workers);

  @Test
  public void full_dijon_image_extension_ok() throws Exception {
    String base64Result =
        extender.process(
            538969, 367435, 20, "geoserver", "COTE_D_OR_2022_5cm", 0, false, 47.3212601, 5.040525);

    image_extension_assertion(base64Result, "dijon_full_image_extended.jpg");
  }

  @Test
  public void full_herault_image_extension_ok() throws Exception {
    String base64Result =
        extender.process(
            533789, 383778, 20, "geoserver", "HERAULT_2020_5cm", 0, false, 43.3804375, 3.2621094);

    image_extension_assertion(base64Result, "herault_full_image_extended.jpg");
  }

  @Test
  public void full_image_extension_ok() throws Exception {
    String base64Result =
        extender.process(
            538596,
            377561,
            20,
            "geoserver",
            "Auvergne_Rhone_Alpes_All_Region_5cm",
            0,
            false,
            44.9120193,
            4.9125046);

    image_extension_assertion(base64Result, "full_image_extended.jpg");
  }

  @Test
  public void image_extension_cropped_ok() throws Exception {
    String base64Result =
        extender.process(
            538596,
            377561,
            20,
            "geoserver",
            "Auvergne_Rhone_Alpes_All_Region_5cm",
            0,
            true,
            44.9120193,
            4.9125046);

    image_extension_assertion(base64Result, "test_output_cropped.jpg");
  }

  @Test
  public void image_extension_shifted_left_ok() throws Exception {
    String base64Result =
        extender.process(
            538596,
            377561,
            20,
            "geoserver",
            "Auvergne_Rhone_Alpes_All_Region_5cm",
            -2,
            true,
            44.9120193,
            4.9125046);

    image_extension_assertion(base64Result, "test_shifted_left.jpg");
  }

  @Test
  public void image_extension_shifted_right_ok() throws Exception {
    String base64Result =
        extender.process(
            538596,
            377561,
            20,
            "geoserver",
            "Auvergne_Rhone_Alpes_All_Region_5cm",
            2,
            true,
            44.9120193,
            4.9125046);

    image_extension_assertion(base64Result, "test_shifted_right.jpg");
  }

  public void image_extension_assertion(String base64Result, String filename) throws Exception {
    assertNotNull(base64Result, "The base64 result should not be null");
    assertFalse(base64Result.isEmpty(), "The base64 result should not be empty");

    // Try decoding and reading the image to validate it
    byte[] decodedBytes = Base64.getDecoder().decode(base64Result);
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));

    assertNotNull(image, "The decoded image should not be null");
    assertTrue(
        image.getWidth() > 0 && image.getHeight() > 0, "Image dimensions should be greater than 0");

    File outputDir = new File("src/test/resources/output_tiles");
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }
    File outputFile = new File(outputDir, filename.endsWith(".jpg") ? filename : filename + ".jpg");
    ImageIO.write(image, "jpg", outputFile);
  }
}
