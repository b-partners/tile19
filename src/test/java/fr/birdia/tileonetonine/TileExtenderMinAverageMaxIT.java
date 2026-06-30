package fr.birdia.tileonetonine;

import static fr.birdia.tileonetonine.testdata.Zone.marnes;
import static fr.birdia.tileonetonine.testdata.Zone.parthenay_pcrs_2;
import static fr.birdia.tileonetonine.testdata.Zone.tarn_et_garonne;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.birdia.tileonetonine.conf.FacadeIT;
import fr.birdia.tileonetonine.endpoint.rest.controller.TileExtenderController;
import fr.birdia.tileonetonine.service.ImageExtenderService;
import fr.birdia.tileonetonine.service.TilesDownloaderService;
import fr.birdia.tileonetonine.service.TilesMergerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Slf4j
@Disabled
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
    ResponseEntity<String> response = tileExtenderController.extendImage(parthenay_pcrs_2());
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
