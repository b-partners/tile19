package fr.birdia.tile19.endpoint.rest.controller;

import static org.springframework.http.MediaType.TEXT_PLAIN;

import fr.birdia.tile19.model.TileExtenderRequestBody;
import fr.birdia.tile19.service.ImageExtenderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class TileExtenderController {
  private final ImageExtenderService imageExtenderService;

  @PutMapping("/extend")
  public ResponseEntity<String> extendImage(@RequestBody TileExtenderRequestBody body)
      throws Exception {
    int x = body.getX();
    int y = body.getY();
    int z = body.getZ();
    String server = body.getServer();
    String layer = body.getLayer();
    int shiftNb = body.getShiftNb();
    boolean isCropped = body.isCropped();
    double lat = body.getLatitude();
    double lon = body.getLongitude();

    double[] offsets = imageExtenderService.computeXYOffsets(lat, lon, x, y, z);
    double xOffset = offsets[0];
    double yOffset = offsets[1];
    double dxInPx = offsets[2];
    double dyInPx = offsets[3];

    String base64Encoded =
        imageExtenderService.process(x, y, z, server, layer, shiftNb, isCropped, lat, lon);

    HttpHeaders headers = new HttpHeaders();
    headers.add("x_offset", String.valueOf(xOffset));
    headers.add("y_offset", String.valueOf(yOffset));
    headers.add("pointer_x", String.valueOf(dxInPx));
    headers.add("pointer_y", String.valueOf(dyInPx));

    return ResponseEntity.ok().contentType(TEXT_PLAIN).headers(headers).body(base64Encoded);
  }
}
