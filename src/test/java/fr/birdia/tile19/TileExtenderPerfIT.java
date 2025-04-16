package fr.birdia.tile19;

import static fr.birdia.tile19.testdata.Zone.alpes_maritimes;
import static fr.birdia.tile19.testdata.Zone.auvergne_rhone_alpes;
import static fr.birdia.tile19.testdata.Zone.bas_rhin;
import static fr.birdia.tile19.testdata.Zone.charente;
import static fr.birdia.tile19.testdata.Zone.cote_d_or_2022;
import static fr.birdia.tile19.testdata.Zone.cote_d_or_2024;
import static fr.birdia.tile19.testdata.Zone.finistere;
import static fr.birdia.tile19.testdata.Zone.gironde;
import static fr.birdia.tile19.testdata.Zone.haut_de_seine;
import static fr.birdia.tile19.testdata.Zone.haut_rhin;
import static fr.birdia.tile19.testdata.Zone.herault;
import static fr.birdia.tile19.testdata.Zone.indre_et_loire;
import static fr.birdia.tile19.testdata.Zone.loire_atlantique;
import static fr.birdia.tile19.testdata.Zone.manche;
import static fr.birdia.tile19.testdata.Zone.marnes;
import static fr.birdia.tile19.testdata.Zone.meurthe_et_moselle;
import static fr.birdia.tile19.testdata.Zone.moselle;
import static fr.birdia.tile19.testdata.Zone.ortho_lisieux;
import static fr.birdia.tile19.testdata.Zone.pcrs_1;
import static fr.birdia.tile19.testdata.Zone.pcrs_2;
import static fr.birdia.tile19.testdata.Zone.pcrs_3;
import static fr.birdia.tile19.testdata.Zone.pcrs_4;
import static fr.birdia.tile19.testdata.Zone.pcrs_5;
import static fr.birdia.tile19.testdata.Zone.pcrs_6;
import static fr.birdia.tile19.testdata.Zone.pcrs_7;
import static fr.birdia.tile19.testdata.Zone.pcrs_8;
import static fr.birdia.tile19.testdata.Zone.pcrs_9;
import static fr.birdia.tile19.testdata.Zone.photo_aerienne_1;
import static fr.birdia.tile19.testdata.Zone.photo_aerienne_2;
import static fr.birdia.tile19.testdata.Zone.photo_aerienne_3;
import static fr.birdia.tile19.testdata.Zone.rhone;
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
public class TileExtenderPerfIT extends FacadeIT {
  @Autowired TileExtenderController tileExtenderController;
  @Autowired ImageExtenderService imageExtenderService;
  @Autowired TilesDownloaderService tilesDownloaderService;
  @Autowired TilesMergerService tilesMergerService;

  @Test
  public void extend_tarn_et_garonne_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(tarn_et_garonne());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Tarn et Garonne Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_Heurtault_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(photo_aerienne_1());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("71 Rue Heurtault, 93300 Aubervilliers Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_haut_rhin_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(haut_rhin());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Haut Rhin Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_haut_de_seine_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(haut_de_seine());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info(
        "Haut de seine : 18 Rue Marie Et Pierre Curie, 92800 Puteaux Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_chambery_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_1());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("1 Rue Sommeiller, 73000 Chambéry Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_parthenay_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_2());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("1 Rue de la Vau Saint-Jacques, 79200 Parthenay, France Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_Nogent_sur_Marne_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(photo_aerienne_2());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("2 Rue François Rolland, 94130 Nogent-sur-Marne Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_gironde_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(gironde());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Gironde Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_loire_atlantique_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(loire_atlantique());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Loire Atlantique Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_poitiers_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_3());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("17 Rue Geneviève Fauconnier, 86000 Poitiers Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_saint_jacques_de_la_lande_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_4());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("63 Bd Jean Mermoz, 35136 Saint-Jacques-de-la-Lande Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_toulouse_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_5());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("2 Rue de Cugnaux, 31300 Toulouse Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_indre_et_loire_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(indre_et_loire());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("27 Rue Édouard Vaillant, 37000 Tours, France Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_carcassone_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_6());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("4 Rue Jacques Louis David, 11000 Carcassonne Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_arles_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_7());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info(
        "Mas de la Chassagnette, D36 Route Sambuc, 13200 Arles, FranceElapsed time={}",
        elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_alpes_maritimes_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(alpes_maritimes());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Alpes Maritimes Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_charente_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(charente());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Charente Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_bas_rhin_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(bas_rhin());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Bas Rhin Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_auvergne_rhone_alpes_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(auvergne_rhone_alpes());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Auvergne Rhone Alpes Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_rhone_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(rhone());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Rhone Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_meurthe_et_moselle_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(meurthe_et_moselle());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Meurthe et Moselle Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_ortho_lisieux_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(ortho_lisieux());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Ortho Lisieux Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_cote_d_or_2022_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(cote_d_or_2022());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Cote d'Or 2022 Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_cote_d_or_2024_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(cote_d_or_2024());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Cote d'Or 2024 Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_finistere_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(finistere());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Finistere Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_manche_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(manche());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Manche Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_vannes_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_8());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("13 Rue Honoré Daumier, 56000 Vannes Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_rouen_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(pcrs_9());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("76000 Rouen, France Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_moselle_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(moselle());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Moselle Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_marnes_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(marnes());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Marnes Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_paris_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(photo_aerienne_3());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("8 rue puget 75018 Paris Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }

  @Test
  public void extend_herault_faster_image_ok() throws Exception {
    long start = System.currentTimeMillis();
    ResponseEntity<String> response = tileExtenderController.extendImage(herault());
    long end = System.currentTimeMillis();
    long elapsedTime = end - start;

    log.info("Herault Elapsed time={}", elapsedTime);

    assertNotNull(response);
  }
}
