package fr.birdia.tile19.endpoint.rest.controller;

import static org.springframework.http.MediaType.TEXT_PLAIN;

import fr.birdia.tile19.model.TileExtenderRequestBody;
import fr.birdia.tile19.service.ImageExtenderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class TileExtenderController {
  private final ImageExtenderService imageExtenderService;

  @PostMapping("/extend")
  public ResponseEntity<String> extendImage(@RequestBody TileExtenderRequestBody body)
      throws Exception {
    int x = body.getX();
    int y = body.getY();
    int z = body.getZ();
    double lat = body.getLatitude();
    double lon = body.getLongitude();

    String base64Encoded =
        imageExtenderService.process(
            x,
            y,
            z,
            body.getServer(),
            body.getLayer(),
            body.getShiftNb(),
            body.isCropped(),
            body.getLatitude(),
            body.getLongitude());

    double[] offsets = imageExtenderService.computeXYOffsets(lat, lon, x, y, z);
    HttpHeaders headers = new HttpHeaders();
    headers.add("x_offset", String.valueOf(offsets[0]));
    headers.add("y_offset", String.valueOf(offsets[1]));
    headers.add("pointer_x", String.valueOf(offsets[2]));
    headers.add("pointer_y", String.valueOf(offsets[3]));

    return ResponseEntity.ok().contentType(TEXT_PLAIN).headers(headers).body(base64Encoded);
  }
}
