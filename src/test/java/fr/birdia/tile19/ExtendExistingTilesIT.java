package fr.birdia.tile19;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.birdia.tile19.conf.FacadeIT;
import fr.birdia.tile19.endpoint.rest.controller.TileExtenderController;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ExtendExistingTilesIT extends FacadeIT {
  @Autowired private TileExtenderController tileExtenderController;

  @Test
  public void testMergeTiles() throws Exception {
    MultipartFile file1 = createMultipartFile("tiles-to-merge/file1.png");
    MultipartFile file2 = createMultipartFile("tiles-to-merge/file2.png");
    MultipartFile file3 = createMultipartFile("tiles-to-merge/file3.png");
    MultipartFile file4 = createMultipartFile("tiles-to-merge/file4.png");
    MultipartFile file5 = createMultipartFile("tiles-to-merge/file5.png");
    MultipartFile file6 = createMultipartFile("tiles-to-merge/file6.png");
    MultipartFile file7 = createMultipartFile("tiles-to-merge/file7.png");
    MultipartFile file8 = createMultipartFile("tiles-to-merge/file8.png");
    MultipartFile file9 = createMultipartFile("tiles-to-merge/file9.png");

    ResponseEntity<String> response =
        tileExtenderController.extendExistingTiles(
            file1, file2, file3, file4, file5, file6, file7, file8, file9);

    assertNotNull(response);
  }

  private MultipartFile createMultipartFile(String filePath) throws IOException {
    ClassPathResource resource = new ClassPathResource(filePath);
    return new MockMultipartFile(
        filePath.substring(filePath.lastIndexOf("/") + 1), resource.getInputStream());
  }
}
