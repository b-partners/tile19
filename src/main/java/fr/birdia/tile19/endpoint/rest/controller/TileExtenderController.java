package fr.birdia.tile19.endpoint.rest.controller;

import static org.springframework.http.MediaType.TEXT_PLAIN;

import fr.birdia.tile19.model.TileExtenderRequestBody;
import fr.birdia.tile19.service.ImageExtenderService;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
    String direction = String.valueOf(body.getShiftDirection());
    String shiftDirection = direction == null ? "RIGHT_LEFT_SIDE" : direction;

    String base64Encoded =
        imageExtenderService.process(
            x,
            y,
            z,
            body.getServer(),
            body.getLayer(),
            body.getShiftNb(),
            shiftDirection,
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

  @PostMapping(path = "/extend/existing-tiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> extendExistingTiles(
      @RequestPart("file1") MultipartFile file1,
      @RequestPart("file2") MultipartFile file2,
      @RequestPart("file3") MultipartFile file3,
      @RequestPart("file4") MultipartFile file4,
      @RequestPart("file5") MultipartFile file5,
      @RequestPart("file6") MultipartFile file6,
      @RequestPart("file7") MultipartFile file7,
      @RequestPart("file8") MultipartFile file8,
      @RequestPart("file9") MultipartFile file9)
      throws Exception {

    List<BufferedImage> images =
        Arrays.asList(
            ImageIO.read(file1.getInputStream()),
            ImageIO.read(file2.getInputStream()),
            ImageIO.read(file3.getInputStream()),
            ImageIO.read(file4.getInputStream()),
            ImageIO.read(file5.getInputStream()),
            ImageIO.read(file6.getInputStream()),
            ImageIO.read(file7.getInputStream()),
            ImageIO.read(file8.getInputStream()),
            ImageIO.read(file9.getInputStream()));

    String base64Encoded = imageExtenderService.extendExistingTiles(images);

    return ResponseEntity.ok().contentType(TEXT_PLAIN).body(base64Encoded);
  }
}
