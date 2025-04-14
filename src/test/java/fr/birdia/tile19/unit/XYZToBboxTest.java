package fr.birdia.tile19.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.birdia.tile19.service.XYZToBBOXService;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class XYZToBboxTest {
  private final XYZToBBOXService service = new XYZToBBOXService();

  @Test
  public void convertTilesToBBOX() throws IOException {
    double[] bbox = service.xyzToBBox(546992, 360926, 20);

    double minX = bbox[0];
    double minY = bbox[1];
    double maxX = bbox[2];
    double maxY = bbox[3];

    String urlStr =
        String.format(
            "http://35.181.83.111/geoserver/cite/wms?"
                + "layers=Bas-Rhin_2023_5cm&format=image/jpeg&width=1024&height=1024"
                + "&bbox=%.2f,%.2f,%.2f,%.2f&srs=EPSG:3857&transparent=true&service=WMS&request=GetMap",
            minX, minY, maxX, maxY);
    URL url = new URL(urlStr);

    HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();

    assertEquals(200, responseCode);
    log.info("Generated WMS URL: {}", url);
  }
}
