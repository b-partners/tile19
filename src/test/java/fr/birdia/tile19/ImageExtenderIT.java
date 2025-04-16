package fr.birdia.tile19;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.birdia.tile19.conf.FacadeIT;
import fr.birdia.tile19.endpoint.rest.controller.TileExtenderController;
import fr.birdia.tile19.model.TileExtenderRequestBody;
import fr.birdia.tile19.service.ImageExtenderService;
import fr.birdia.tile19.service.TilesDownloaderService;
import fr.birdia.tile19.service.TilesMergerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ImageExtenderIT extends FacadeIT {
  @Autowired TileExtenderController tileExtenderController;
  @Autowired ImageExtenderService imageExtenderService;
  @Autowired TilesDownloaderService tilesDownloaderService;
  @Autowired TilesMergerService tilesMergerService;

  public TileExtenderRequestBody body() {
    return TileExtenderRequestBody.builder()
        .x(538969)
        .y(367435)
        .z(20)
        .server("geoserver")
        .layer("COTE_D_OR_2022_5cm")
        .shiftNb(0)
        .isCropped(true)
        .latitude(47.3212601)
        .longitude(5.040525)
        .build();
  }

  //  {"y": 374065, "x": 524765, "z": 20, "server": "geoserver", "layer": "CHARENTE_2019_5cm",
  // "shiftNb": 0, "isCropped": true, "latitude": 45.7557272, "longitude": 0.1639253}
  public TileExtenderRequestBody charenteBody() {
    return TileExtenderRequestBody.builder()
        .x(524765)
        .y(374065)
        .z(20)
        .server("geoserver")
        .layer("CHARENTE_2019_5cm")
        .shiftNb(0)
        .isCropped(true)
        .latitude(45.7557272)
        .longitude(0.1639253)
        .build();
  }

  //  {"y": 383778, "x": 533789, "z": 20, "server": "geoserver", "layer": "HERAULT_2020_5cm",
  // "shiftNb": 0, "isCropped": true, "latitude": 43.3804375, "longitude": 3.2621094}

  public TileExtenderRequestBody herault() {
    return TileExtenderRequestBody.builder()
        .x(533789)
        .y(383778)
        .z(20)
        .server("geoserver")
        .layer("HERAULT_2020_5cm")
        .shiftNb(0)
        .isCropped(true)
        .latitude(43.3804375)
        .longitude(3.2621094)
        .build();
  }

  @Test
  public void extend_image_ok() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(body());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));

    assertEquals(1119.412271788272, xOffset);
    assertEquals(994.7748578980581, yOffset);
    assertNotNull(response);
  }

  @Test
  public void extend_image_ko() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(charenteBody());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));

    //    assertEquals(1119.412271788272, xOffset);
    //    assertEquals(994.7748578980581, yOffset);
    assertNotNull(response);
  }

  @Test
  public void herault_extend_image_ko() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(herault());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));

    //    assertEquals(1119.412271788272, xOffset);
    //    assertEquals(994.7748578980581, yOffset);

    log.info("body={}", response.getBody());
    assertNotNull(response);
  }
}
