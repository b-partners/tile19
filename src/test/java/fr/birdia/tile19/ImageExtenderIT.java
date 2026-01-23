package fr.birdia.tile19;

import static fr.birdia.tile19.model.TileExtenderRequestBody.ShiftDirection.RIGHT_LEFT_SIDE;
import static fr.birdia.tile19.model.TileExtenderRequestBody.ShiftDirection.UP_DOWN_SIDE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.birdia.tile19.conf.FacadeIT;
import fr.birdia.tile19.endpoint.rest.controller.TileExtenderController;
import fr.birdia.tile19.model.TileExtenderRequestBody;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Slf4j
@Disabled
public class ImageExtenderIT extends FacadeIT {
  @Autowired TileExtenderController tileExtenderController;

  public TileExtenderRequestBody degraded_body() {
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
        .isOpaque(true)
        .build();
  }

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

  public TileExtenderRequestBody lyon_shifted_down() {
    return TileExtenderRequestBody.builder()
        .x(538969)
        .y(367435)
        .z(20)
        .server("geoserver")
        .layer("COTE_D_OR_2022_5cm")
        .shiftNb(1)
        .isCropped(false)
        .shiftDirection(UP_DOWN_SIDE)
        .latitude(47.3212601)
        .longitude(5.040525)
        .build();
  }

  public TileExtenderRequestBody lyon_shifted_up() {
    return TileExtenderRequestBody.builder()
        .x(538969)
        .y(367435)
        .z(20)
        .server("geoserver")
        .layer("COTE_D_OR_2022_5cm")
        .shiftNb(-1)
        .isCropped(false)
        .shiftDirection(UP_DOWN_SIDE)
        .latitude(47.3212601)
        .longitude(5.040525)
        .build();
  }

  public TileExtenderRequestBody lyon_shifted_right() {
    return TileExtenderRequestBody.builder()
        .x(538969)
        .y(367435)
        .z(20)
        .server("geoserver")
        .layer("COTE_D_OR_2022_5cm")
        .shiftNb(1)
        .isCropped(false)
        .shiftDirection(RIGHT_LEFT_SIDE)
        .latitude(47.3212601)
        .longitude(5.040525)
        .build();
  }

  public TileExtenderRequestBody lyon_shifted_left() {
    return TileExtenderRequestBody.builder()
        .x(538969)
        .y(367435)
        .z(20)
        .server("geoserver")
        .layer("COTE_D_OR_2022_5cm")
        .shiftNb(-1)
        .isCropped(false)
        .shiftDirection(RIGHT_LEFT_SIDE)
        .latitude(47.3212601)
        .longitude(5.040525)
        .build();
  }

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

  public TileExtenderRequestBody airbusBody() {
    return TileExtenderRequestBody.builder()
        .x(264242)
        .y(191449)
        .z(19)
        .server("airbus")
        .layer("PNEO")
        .shiftNb(0)
        .isCropped(true)
        .latitude(43.599621309901735)
        .longitude(1.4410986644024693)
        .isOpaque(false)
        .build();
  }

  @Test
  @Disabled("Run locally")
  public void extend_airbus_images_ok() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(airbusBody());

    assertNotNull(response);

    Files.write(
        Paths.get("airbus-non-opaque-image.jpg"), Base64.getDecoder().decode(response.getBody()));
  }

  @Test
  public void extend_not_full_HD_image_ok() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(degraded_body());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));

    assertEquals(1119.412271788272, xOffset);
    assertEquals(994.7748578980581, yOffset);
    assertNotNull(response);

    Files.write(Paths.get("opaque-image.jpg"), Base64.getDecoder().decode(response.getBody()));
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
  public void extend_image_shifted_right_ok() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(lyon_shifted_right());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));

    assertEquals(1119.412271788272, xOffset);
    assertEquals(994.7748578980581, yOffset);
    assertNotNull(response);
  }

  @Test
  public void extend_image_shifted_left_ok() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(lyon_shifted_left());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));
    byte[] imageBytes = Base64.getDecoder().decode(response.getBody());

    assertEquals(1119.412271788272, xOffset);
    assertEquals(994.7748578980581, yOffset);
    assertNotNull(response);

    //    Files.write(Paths.get("lyon_shifted_left.jpg"), imageBytes);
  }

  @Test
  public void extend_image_shifted_down_ok() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(lyon_shifted_down());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));

    assertEquals(1119.412271788272, xOffset);
    assertEquals(994.7748578980581, yOffset);
    assertNotNull(response);

    byte[] imageBytes = Base64.getDecoder().decode(response.getBody());

    Files.write(Paths.get("output_shifted_down.jpg"), imageBytes);
  }

  @Test
  public void extend_image_shifted_up_ok() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(lyon_shifted_up());
    HttpHeaders headers = response.getHeaders();
    double xOffset = Double.parseDouble(headers.getFirst("x_offset"));
    double yOffset = Double.parseDouble(headers.getFirst("y_offset"));

    assertEquals(1119.412271788272, xOffset);
    assertEquals(994.7748578980581, yOffset);
    assertNotNull(response);

    byte[] imageBytes = Base64.getDecoder().decode(response.getBody());

    Files.write(Paths.get("output_shifted_up.jpg"), imageBytes);
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
