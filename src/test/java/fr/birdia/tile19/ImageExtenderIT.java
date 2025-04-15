package fr.birdia.tile19;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.birdia.tile19.conf.FacadeIT;
import fr.birdia.tile19.endpoint.rest.controller.TileExtenderController;
import fr.birdia.tile19.model.TileExtenderRequestBody;
import fr.birdia.tile19.service.ImageExtenderService;
import fr.birdia.tile19.service.TilesDownloaderService;
import fr.birdia.tile19.service.TilesMergerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

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
}
