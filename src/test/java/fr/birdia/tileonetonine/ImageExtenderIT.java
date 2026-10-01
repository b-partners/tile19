package fr.birdia.tileonetonine;

import static fr.birdia.tileonetonine.model.TileExtenderRequestBody.ShiftDirection.RIGHT_LEFT_SIDE;
import static fr.birdia.tileonetonine.model.TileExtenderRequestBody.ShiftDirection.UP_DOWN_SIDE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.birdia.tileonetonine.conf.FacadeIT;
import fr.birdia.tileonetonine.endpoint.rest.controller.TileExtenderController;
import fr.birdia.tileonetonine.model.CityTileTestCase;
import fr.birdia.tileonetonine.model.TileExtenderRequestBody;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Slf4j
@Disabled
public class ImageExtenderIT extends FacadeIT {
  @Autowired TileExtenderController tileExtenderController;

  //  46.386617184543546, 5.86114453923984
  public TileExtenderRequestBody ignBody() {
    return TileExtenderRequestBody.builder()
        .x(270679)
        .y(185708)
        .z(19)
        .server("geoserver_ign")
        .layer("FLUX_IGN_2023_20CM")
        .shiftNb(0)
        .isCropped(false)
        .latitude(46.3864618)
        .longitude(5.861166)
        .isOpaque(false)
        .build();
  }

  public TileExtenderRequestBody opaque_body() {
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
        .isCropped(false)
        .latitude(43.599621309901735)
        .longitude(1.4410986644024693)
        .isOpaque(false)
        .build();
  }

  public TileExtenderRequestBody throwBlankExceptionBody() {
    return TileExtenderRequestBody.builder()
        .x(521906)
        .y(368610)
        .z(20)
        .server("geoserver")
        .layer("PCRS")
        .shiftNb(0)
        .isCropped(false)
        .latitude(47.047005283518075)
        .longitude(-0.8176651895582759)
        .isOpaque(false)
        .build();
  }

  @Test
  void test_throw_blank_exception() throws Exception {
    assertThrows(
        RuntimeException.class,
        () -> tileExtenderController.extendImage(throwBlankExceptionBody()));
  }

  @Test
  void test_ign_tile19() throws Exception {
    ResponseEntity<String> response = tileExtenderController.extendImage(ignBody());
    Files.write(Paths.get("ign-test-images"), Base64.getDecoder().decode(response.getBody()));
  }

  @ParameterizedTest(name = "Download image for {0}")
  @MethodSource("cityTileProvider")
  @Disabled
  public void extend_dijon(CityTileTestCase city) throws Exception {
    log.info("Processing city={}", city.city());
    TileExtenderRequestBody body = createTileExtenderRequestBodyFrom(city);
    ResponseEntity<String> response = tileExtenderController.extendImage(body);
    //    assertNotNull(response);
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
    ResponseEntity<String> response = tileExtenderController.extendImage(opaque_body());
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

  public TileExtenderRequestBody createTileExtenderRequestBodyFrom(
      CityTileTestCase cityTileTestCase) {
    String[] xyzTile = cityTileTestCase.xyzTile().split("_");

    return TileExtenderRequestBody.builder()
        .z(Integer.parseInt(xyzTile[0]))
        .x(Integer.parseInt(xyzTile[1]))
        .y(Integer.parseInt(xyzTile[2]))
        .server("airbus")
        .layer("PNEO")
        .shiftNb(0)
        .isCropped(false)
        .latitude(cityTileTestCase.latitude())
        .longitude(cityTileTestCase.longitude())
        .build();
  }

  static Stream<CityTileTestCase> cityTileProvider() {
    return Stream.of(
        //        new CityTileTestCase("Dijon", 47.341749, 5.020057, "19_269454_183673"), // failed
        new CityTileTestCase("Mans", 48.012534, 0.173570, "19_262396_182222"),
        new CityTileTestCase("Mans", 48.018972, 0.179513, "19_262405_182208"),
        new CityTileTestCase("Paris", 48.8566, 2.3522, "19_265242_180499"),
        new CityTileTestCase("Nantes", 47.2184, -1.5536, "19_259881_183938"),
        new CityTileTestCase("Lyon", 45.7640, 4.8357, "19_269186_187015"),
        new CityTileTestCase("Lille", 50.6292, 3.0573, "19_266596_176374"),
        new CityTileTestCase("Bordeaux", 44.8378, -0.5792, "19_261300_188933"),
        new CityTileTestCase("Marseille", 43.2965, 5.3698, "19_269964_192057"),
        new CityTileTestCase("Strasbourg", 48.5734, 7.7521, "19_273433_180995"),
        //            //        new CityTileTestCase("Montpellier", 44.1194, 3.2319,
        // "19_266850_190399"), // failed
        new CityTileTestCase("Caen", 49.4431, 1.0993, "19_263744_179064"),
        new CityTileTestCase("Grenoble", 45.1885, 5.7245, "19_270480_188210"),
        new CityTileTestCase("Nîmes", 43.9352, 4.1023, "19_268118_190772"),
        new CityTileTestCase("Perpignan", 42.6977, 2.8956, "19_266361_193249"),
        new CityTileTestCase("Annecy", 46.2044, 6.1432, "19_271090_186092"),
        //            ////        new CityTileTestCase("Cahors", 44.0140, 1.7043,
        // "19_264626_190613"), // failed
        new CityTileTestCase("Calais", 50.9513, 1.8587, "19_264850_175632"),
        new CityTileTestCase("Rennes", 48.1173, -1.6778, "19_259700_181994"),
        new CityTileTestCase("Orléans", 47.9029, 1.9093, "19_264924_182461"),
        new CityTileTestCase("Angers", 47.4784, -0.5632, "19_261323_183379"),
        new CityTileTestCase("Bayeux", 49.1829, -0.3700, "19_261605_179645"),
        new CityTileTestCase("Nice", 43.7102, 7.2620, "19_272720_191226"));
  }
}
