package fr.birdia.tile19.service.airbus;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import fr.birdia.tile19.model.airbus.AirbusAuthResponse;
import fr.birdia.tile19.model.airbus.AirbusFeature;
import fr.birdia.tile19.model.airbus.AirbusPNEOResponse;
import fr.birdia.tile19.model.airbus.AirbusProperties;
import fr.birdia.tile19.model.airbus.AirbusRequestBody;
import fr.birdia.tile19.model.airbus.Geometry;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class AirbusPNEOService {
  private RestTemplate restTemplate;
  private String airbusAuthenticationBaseUrl;
  private String airbusApiKey;
  private final UriComponents baseUrl;

  public AirbusPNEOService(
      RestTemplate restTemplate,
      @Value("${airbus.authentication.baseurl}") String airbusAuthenticationBaseUrl,
      @Value("${airbus.api.key}") String airbusApiKey,
      @Value("${airbus.searchapi.baseurl}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.airbusAuthenticationBaseUrl = airbusAuthenticationBaseUrl;
    this.airbusApiKey = airbusApiKey;
    this.baseUrl = UriComponentsBuilder.fromHttpUrl(baseUrl).build();
  }

  public String authenticateAirbus() {
    log.info("Process airbus authentication ...");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("apikey", airbusApiKey);
    body.add("grant_type", "api_key");
    body.add("client_id", "IDP");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
    ResponseEntity<AirbusAuthResponse> response =
        restTemplate.postForEntity(
            URI.create(airbusAuthenticationBaseUrl), request, AirbusAuthResponse.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      log.info("Airbus authentication successful");
      return response.getBody().getAccessToken();
    }

    throw new IllegalArgumentException("Unable to retrieve Airbus Access Token");
  }

  public AirbusProperties retrieveAirbusProperties(double lat, double lon) {
    String bearerToken = authenticateAirbus();
    Geometry geometry = convertLatLonToGeometry(lat, lon);
    AirbusRequestBody requestBody =
        AirbusRequestBody.builder()
            .constellation("PNEO")
            .workspace("public-pneo")
            .cloudCover("[0,10]")
            .itemsPerPage(10)
            .startPage(1)
            .processingLevel("SENSOR")
            .relation("intersects")
            .sortBy("-acquisitionDate")
            .geometry(geometry)
            .build();
    HttpHeaders headersWithBearer = customizeHeaders(bearerToken);
    headersWithBearer.setAccept(List.of(MediaType.APPLICATION_JSON));
    headersWithBearer.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
    HttpEntity<AirbusRequestBody> entity = new HttpEntity<>(requestBody, headersWithBearer);

    ResponseEntity<AirbusPNEOResponse> response =
        restTemplate.exchange(baseUrl.toUri(), HttpMethod.POST, entity, AirbusPNEOResponse.class);

    AirbusFeature feature = Objects.requireNonNull(response.getBody()).getFeatures().getFirst();
    String wmtsUrl =
        String.format(
            "%s/tiles/1.0.0/default/rgb/EPSG3857/", feature.getLinks().getWmts().getHref());
    return AirbusProperties.builder()
        .wmtsUrl(wmtsUrl)
        .updatedAt(feature.getProperties().getLastUpdateDate())
        .bearer(bearerToken)
        .build();
  }

  public Geometry convertLatLonToGeometry(double lat, double lon) {
    // Area in square meters
    double areaM2 = 900;
    // Side length of the square
    double sideM = Math.sqrt(areaM2);
    // Half side
    double halfSideM = sideM / 2;

    // Approximate meters per degree
    double metersPerDegLat = 111_320;
    double metersPerDegLon = metersPerDegLat * Math.cos(Math.toRadians(lat));

    // Degree deltas
    double deltaLat = halfSideM / metersPerDegLat;
    double deltaLon = halfSideM / metersPerDegLon;

    // Bounding box coordinates
    double minLat = lat - deltaLat;
    double maxLat = lat + deltaLat;
    double minLon = lon - deltaLon;
    double maxLon = lon + deltaLon;

    List<List<List<BigDecimal>>> polygonCoords = new ArrayList<>();
    List<List<BigDecimal>> coordinates = new ArrayList<>();
    coordinates.add(point(minLon, minLat));
    coordinates.add(point(maxLon, minLat));
    coordinates.add(point(maxLon, maxLat));
    coordinates.add(point(minLon, maxLat));
    coordinates.add(point(minLon, minLat));
    polygonCoords.add(coordinates);

    return Geometry.builder().type("Polygon").coordinates(polygonCoords).build();
  }

  public HttpHeaders customizeHeaders(String bearerToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(bearerToken);
    return headers;
  }

  private static List<BigDecimal> point(double lon, double lat) {
    return List.of(BigDecimal.valueOf(lon), BigDecimal.valueOf(lat));
  }
}
