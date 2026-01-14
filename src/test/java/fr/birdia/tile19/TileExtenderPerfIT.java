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
import static fr.birdia.tile19.testdata.Zone.ille_et_vilaine_la_lande_pcrs_4;
import static fr.birdia.tile19.testdata.Zone.indre_et_loire;
import static fr.birdia.tile19.testdata.Zone.loire_atlantique;
import static fr.birdia.tile19.testdata.Zone.manche;
import static fr.birdia.tile19.testdata.Zone.marnes;
import static fr.birdia.tile19.testdata.Zone.meurthe_et_moselle;
import static fr.birdia.tile19.testdata.Zone.moselle;
import static fr.birdia.tile19.testdata.Zone.ortho_lisieux;
import static fr.birdia.tile19.testdata.Zone.parthenay_pcrs_2;
import static fr.birdia.tile19.testdata.Zone.pcrs_1;
import static fr.birdia.tile19.testdata.Zone.pcrs_6;
import static fr.birdia.tile19.testdata.Zone.pcrs_7;
import static fr.birdia.tile19.testdata.Zone.pcrs_8;
import static fr.birdia.tile19.testdata.Zone.pcrs_9;
import static fr.birdia.tile19.testdata.Zone.photo_aerienne_1;
import static fr.birdia.tile19.testdata.Zone.photo_aerienne_2;
import static fr.birdia.tile19.testdata.Zone.photo_aerienne_3;
import static fr.birdia.tile19.testdata.Zone.poitiers_pcrs_3;
import static fr.birdia.tile19.testdata.Zone.rhone;
import static fr.birdia.tile19.testdata.Zone.tarn_et_garonne;
import static fr.birdia.tile19.testdata.Zone.toulouse_haute_garonne;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.birdia.tile19.conf.FacadeIT;
import fr.birdia.tile19.endpoint.rest.controller.TileExtenderController;
import fr.birdia.tile19.model.TileExtenderRequestBody;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Slf4j
@Disabled
public class TileExtenderPerfIT extends FacadeIT {
  @Autowired TileExtenderController tileExtenderController;

  private static final boolean IS_LOCAL = "local".equals(System.getenv("ENV"));
  private static final Duration NON_LOCAL_MAX_DURATION = Duration.ofMinutes(2);
  private static final Duration SLOW_MAX_DURATION =
      IS_LOCAL ? Duration.ofSeconds(20) : NON_LOCAL_MAX_DURATION;
  private static final Duration QUICK_MAX_DURATION =
      IS_LOCAL ? Duration.ofSeconds(7) : NON_LOCAL_MAX_DURATION;
  private static final int MAX_RETRY_NB = 3;

  private void extendTile(TileExtenderRequestBody request, String name, Duration maxDuration) {
    int attempt = 0;
    long elapsedTime;
    ResponseEntity<String> response = null;

    while (true) {
      attempt++;
      var start = currentTimeMillis();

      try {
        response = tileExtenderController.extendImage(request);
        elapsedTime = currentTimeMillis() - start;

        log.info("{} (attempt {}). Elapsed time={}ms", name, attempt, elapsedTime);

        assertNotNull(response);
        assertTrue(elapsedTime < maxDuration.toMillis());
        return;
      } catch (Exception e) {
        elapsedTime = currentTimeMillis() - start;
        log.warn("{} failed on attempt {} after {}ms", name, attempt, elapsedTime, e);

        if (attempt > MAX_RETRY_NB) {
          throw new RuntimeException(
              String.format("%s : Test failed after %d attempts", name, attempt), e);
        }
      }
    }
  }

  private void extendTile(TileExtenderRequestBody request, String name) {
    extendTile(request, name, QUICK_MAX_DURATION);
  }

  @Test
  public void extend_tarn_et_garonne() {
    extendTile(tarn_et_garonne(), "Tarn et Garonne");
  }

  @Test
  public void extend_Heurtault() {
    extendTile(photo_aerienne_1(), "71 Rue Heurtault, 93300 Aubervilliers");
  }

  @Test
  public void extend_haut_rhin() {
    extendTile(haut_rhin(), "Haut Rhin");
  }

  @Test
  public void extend_haut_de_seine() {
    extendTile(haut_de_seine(), "Haut de seine : 18 Rue Marie Et Pierre Curie, 92800 Puteaux");
  }

  @Test
  @Disabled
  public void extend_chambery() {
    extendTile(pcrs_1(), "1 Rue Sommeiller, 73000 Chambéry");
  }

  @Test
  public void extend_parthenay() {
    extendTile(parthenay_pcrs_2(), "1 Rue de la Vau Saint-Jacques, 79200 Parthenay, France");
  }

  @Test
  public void extend_Nogent_sur_Marne() {
    extendTile(photo_aerienne_2(), "2 Rue François Rolland, 94130 Nogent-sur-Marne");
  }

  @Test
  public void extend_gironde() {
    extendTile(gironde(), "Gironde");
  }

  @Test
  public void extend_loire_atlantique() {
    extendTile(loire_atlantique(), "Loire Atlantique");
  }

  @Test
  public void extend_poitiers() {
    extendTile(poitiers_pcrs_3(), "17 Rue Geneviève Fauconnier, 86000 Poitiers");
  }

  @Test
  public void extend_saint_jacques_de_la_lande() {
    extendTile(
        ille_et_vilaine_la_lande_pcrs_4(), "63 Bd Jean Mermoz, 35136 Saint-Jacques-de-la-Lande");
  }

  @Test
  public void extend_toulouse() {
    extendTile(toulouse_haute_garonne(), "2 Rue de Cugnaux, 31300 Toulouse");
  }

  @Test
  public void extend_carcassone() {
    extendTile(pcrs_6(), "4 Rue Jacques Louis David, 11000 Carcassonne");
  }

  @Test
  public void extend_arles() {
    extendTile(
        pcrs_7(), "Mas de la Chassagnette, D36 Route Sambuc, 13200 Arles, FranceElapsed time={}");
  }

  @Test
  public void extend_alpes_maritimes() {
    extendTile(alpes_maritimes(), "Alpes Maritimes");
  }

  @Test
  public void extend_charente() {
    extendTile(charente(), "Charente");
  }

  @Test
  public void extend_bas_rhin() {
    extendTile(bas_rhin(), "Bas Rhin");
  }

  @Test
  public void extend_rhone() {
    extendTile(rhone(), "Rhone");
  }

  @Test
  @Disabled("server down")
  public void extend_ortho_lisieux() {
    extendTile(ortho_lisieux(), "Ortho Lisieux");
  }

  @Test
  public void extend_cote_d_or_2022() {
    extendTile(cote_d_or_2022(), "Cote d'Or 2022");
  }

  @Test
  public void extend_vannes() {
    extendTile(pcrs_8(), "13 Rue Honoré Daumier, 56000 Vannes");
  }

  @Test
  public void extend_paris() {
    extendTile(photo_aerienne_3(), "8 rue puget 75018 Paris");
  }

  @Test
  public void extend_herault() {
    extendTile(herault(), "Herault");
  }

  /* ************************************************************** */
  /* ************************ Slow queries ************************ */
  /* ************************************************************** */

  @Test
  public void extend_cote_d_or_2024() {
    extendTile(cote_d_or_2024(), "Cote d'Or 2024", SLOW_MAX_DURATION);
  }

  @Test
  public void extend_finistere() {
    extendTile(finistere(), "Finistere", SLOW_MAX_DURATION);
  }

  @Test
  public void extend_manche() {
    extendTile(manche(), "Manche", SLOW_MAX_DURATION);
  }

  @Disabled(
      "Rouen server is currently unavailable. It has been temporarily disabled to avoid deployment"
          + " failures and will be re-enabled later.")
  public void extend_rouen() {
    extendTile(pcrs_9(), "76000 Rouen, France", SLOW_MAX_DURATION);
  }

  @Test
  public void extend_moselle() {
    extendTile(moselle(), "Moselle", SLOW_MAX_DURATION);
  }

  @Test
  @Disabled
  public void extend_marnes() {
    extendTile(marnes(), "Marnes", SLOW_MAX_DURATION);
  }

  @Test
  public void extend_meurthe_et_moselle() {
    extendTile(meurthe_et_moselle(), "Meurthe et Moselle", SLOW_MAX_DURATION);
  }

  @Test
  public void extend_auvergne_rhone_alpes() {
    extendTile(auvergne_rhone_alpes(), "Auvergne Rhone Alpes", SLOW_MAX_DURATION);
  }

  @Test
  public void extend_indre_et_loire() {
    extendTile(indre_et_loire(), "27 Rue Édouard Vaillant, 37000 Tours, France", SLOW_MAX_DURATION);
  }
}
