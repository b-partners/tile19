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

  @PutMapping(path = "/existing-tiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> extendExistingTiles(
      @RequestParam("file1") MultipartFile file1,
      @RequestParam("file2") MultipartFile file2,
      @RequestParam("file3") MultipartFile file3,
      @RequestParam("file4") MultipartFile file4,
      @RequestParam("file5") MultipartFile file5,
      @RequestParam("file6") MultipartFile file6,
      @RequestParam("file7") MultipartFile file7,
      @RequestParam("file8") MultipartFile file8,
      @RequestParam("file9") MultipartFile file9)
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
