package fr.birdia.tile19;

import static fr.birdia.tile19.testdata.Zone.marnes;
import static fr.birdia.tile19.testdata.Zone.pcrs_2;
import static fr.birdia.tile19.testdata.Zone.tarn_et_garonne;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.birdia.tile19.conf.FacadeIT;
import fr.birdia.tile19.endpoint.rest.controller.TileExtenderController;
import fr.birdia.tile19.service.ImageExtenderService;
import fr.birdia.tile19.service.TilesDownloaderService;
import fr.birdia.tile19.service.TilesMergerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Slf4j
public class TileExtenderMinAverageMaxIT extends FacadeIT {
  @Autowired TileExtenderController tileExtenderController;
  @Autowired ImageExtenderService imageExtenderService;
  @Autowired TilesDownloaderService tilesDownloaderService;
  @Autowired TilesMergerService tilesMergerService;
  private long seuil = 20000;

  @Test
  public void extend_tarn_et_garonne_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(tarn_et_garonne());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Tarn et Garonne", elapsedTime);

    //    assertTrue(elapsedTime < this.seuil, "Elapsed time: " + elapsedTime + "ms");
    assertNotNull(response);
  }

  @Test
  public void extend_parthenay_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_2());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("1 Rue de la Vau Saint-Jacques, 79200 Parthenay, France", elapsedTime);

    //    assertTrue(elapsedTime < this.seuil, "Elapsed time: " + elapsedTime + "ms");
    assertNotNull(response);
  }

  public void extend_marnes_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(marnes());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Marnes", elapsedTime);

    //    assertTrue(elapsedTime < this.seuil, "Elapsed time: " + elapsedTime + "ms");
    assertNotNull(response);
  }
}
