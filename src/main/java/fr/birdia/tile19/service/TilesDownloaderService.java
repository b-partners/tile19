package fr.birdia.tile19.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TilesDownloaderService {
  private final String GEOSERVER_BASE_URL = "http://35.181.83.111/geoserver/cite/wms";
  private final String IGN_BASE_URL = "https://data.geopf.fr/wmts";
  private final String GEOSERVER = "geoserver";
  private final XYZToBBOXService xyzToBBoxService;

  static double[] tileToLatLon(int x, int y, int zoom) {
    int n = (int) Math.pow(2, zoom);
    double lonDeg = x / (double) n * 360.0 - 180.0;
    double latRad = Math.atan(Math.sinh(Math.PI * (1 - 2 * y / (double) n)));
    double latDeg = Math.toDegrees(latRad);

    return new double[] {latDeg, lonDeg};
  }

  public static int[] convertTilesCoordinateToTileColTileRow(int x, int y, int zoom) {
    double[] latLon = tileToLatLon(x, y, zoom);
    double latDeg = latLon[0];
    double lonDeg = latLon[1];
    // Perform the Mercator projection to tile coordinates
    int n = (int) Math.pow(2, zoom);
    double xtile = n * ((lonDeg + 180) / 360);
    double latRad = Math.toRadians(latDeg);
    double ytile = n * (1 - (Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI)) / 2;

    return new int[] {(int) xtile, (int) ytile};
  }

  private HashMap<String, String> configureGeoserverParams(
      String layer, double minX, double maxX, double minY, double maxY) {
    HashMap<String, String> params = new HashMap<>();
    params.put("layers", layer);
    params.put("format", "image/jpeg");
    params.put("width", "1024");
    params.put("height", "1024");
    params.put("bbox", minX + "," + minY + "," + maxX + "," + maxY);
    params.put("srs", "EPSG:3857");
    params.put("transparent", "true");
    params.put("service", "WMS");
    params.put("request", "GetMap");
    return params;
  }

  private HashMap<String, String> configureIgnParams(int tileCol, int tileRow, int zoom) {
    HashMap<String, String> params = new HashMap<>();
    params.put("SERVICE", "WMTS");
    params.put("REQUEST", "GetTile");
    params.put("VERSION", "1.0.0");
    params.put("LAYER", "ORTHOIMAGERY.ORTHOPHOTOS");
    params.put("TILEMATRIXSET", "PM");
    params.put("TILEMATRIX", String.valueOf(zoom));
    params.put("TILECOL", String.valueOf(tileCol));
    params.put("TILEROW", String.valueOf(tileRow));
    params.put("STYLE", "normal");
    params.put("FORMAT", "image/jpeg");
    return params;
  }

  public BufferedImage download(int xTile, int yTile, int zoom, String server, String layer)
      throws IOException, InterruptedException {
    StringBuilder urlBuilder;

    if (GEOSERVER.equals(server)) {
      urlBuilder = new StringBuilder();
      double[] bbox = xyzToBBoxService.xyzToBBox(xTile, yTile, zoom);
      HashMap<String, String> params =
          configureGeoserverParams(layer, bbox[0], bbox[2], bbox[1], bbox[3]);
      urlBuilder.append(GEOSERVER_BASE_URL).append("?");

      params.forEach(
          (key, value) ->
              urlBuilder
                  .append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                  .append("=")
                  .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                  .append("&"));
      urlBuilder.setLength(urlBuilder.length() - 1);
    } else {
      int[] tilColRow = convertTilesCoordinateToTileColTileRow(xTile, yTile, zoom);
      int tileCol = tilColRow[0];
      int tileRow = tilColRow[1];

      HashMap<String, String> params = configureIgnParams(tileCol, tileRow, zoom);
      urlBuilder = new StringBuilder(IGN_BASE_URL);
      urlBuilder.append("?");
      for (HashMap.Entry<String, String> entry : params.entrySet()) {
        urlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
        urlBuilder.append("=");
        urlBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        urlBuilder.append("&");
      }
      urlBuilder.setLength(urlBuilder.length() - 1);
    }

    log.info("DEBUG URL: {}", urlBuilder);

    HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(urlBuilder.toString()))
            .header("Accept", "image/png, image/jpeg;q=0.9, */*;q=0.8")
            .header("User-Agent", "TileDownloader/1.0")
            .timeout(Duration.ofSeconds(60))
            .GET()
            .build();

    HttpResponse<InputStream> response =
        client.send(request, HttpResponse.BodyHandlers.ofInputStream());

    if (response.statusCode() == 200) {
      String contentType = response.headers().firstValue("Content-Type").orElse("");
      if (contentType.startsWith("image")) {
        try (InputStream is = response.body()) {
          return ImageIO.read(is);
        }
      } else {
        String error = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
        System.err.println("WMS Error: " + error);
      }
    } else {
      System.err.println("HTTP Error " + response.statusCode() + ": " + response.body());
    }
    return null;
  }
}
