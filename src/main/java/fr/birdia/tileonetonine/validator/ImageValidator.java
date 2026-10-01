package fr.birdia.tileonetonine.validator;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.birdia.tileonetonine.exception.BlankImageException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ImageValidator implements Consumer<BufferedImage> {
  private static final String IMAGE_VALIDATOR_API_URL = System.getenv("IMAGE_VALIDATOR_API_URL");
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public ImageValidator(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void accept(BufferedImage image) {
    if (image == null) {
      throw new BlankImageException("Image is null.");
    }

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "jpeg", baos);
      byte[] imageBytes = baos.toByteArray();
      String base64 = Base64.getEncoder().encodeToString(imageBytes);
      Map<String, String> body = new HashMap<>();
      body.put("base64image", base64);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(APPLICATION_JSON);
      HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
      ResponseEntity<String> response =
          restTemplate.postForEntity(IMAGE_VALIDATOR_API_URL, request, String.class);

      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new RuntimeException("Validator API error: " + response.getStatusCode());
      }

      JsonNode jsonNode = objectMapper.readTree(response.getBody());
      log.info("Validator response raw: {}", response.getBody());
      boolean isCorrupted = jsonNode.get("isCorrupted").asBoolean();

      if (isCorrupted) {
        throw new BlankImageException("Image is corrupted.");
      }

    } catch (IOException e) {
      throw new RuntimeException("Error processing image", e);
    }
  }
}
