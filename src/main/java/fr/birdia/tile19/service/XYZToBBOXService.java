package fr.birdia.tile19.service;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.springframework.stereotype.Service;

@Service
public class XYZToBBOXService {

  private static final CRSFactory CRS_FACTORY = new CRSFactory();
  private static final CoordinateReferenceSystem WGS84 = CRS_FACTORY.createFromName("EPSG:4326");
  private static final CoordinateReferenceSystem WEB_MERCATOR =
      CRS_FACTORY.createFromName("EPSG:3857");
  private static final CoordinateTransform TRANSFORM;

  static {
    CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    TRANSFORM = ctFactory.createTransform(WGS84, WEB_MERCATOR);
  }

  public double[] xyzToBBox(int x, int y, int z) {
    double[] bboxWGS84 = tileBounds(x, y, z);

    ProjCoordinate ll = new ProjCoordinate(bboxWGS84[0], bboxWGS84[1]);
    ProjCoordinate ur = new ProjCoordinate(bboxWGS84[2], bboxWGS84[3]);

    ProjCoordinate llTransformed = new ProjCoordinate();
    ProjCoordinate urTransformed = new ProjCoordinate();

    TRANSFORM.transform(ll, llTransformed);
    TRANSFORM.transform(ur, urTransformed);

    return new double[] {
      llTransformed.x, llTransformed.y,
      urTransformed.x, urTransformed.y
    };
  }

  private static double[] tileBounds(int x, int y, int z) {
    double n = Math.pow(2.0, z);
    double lonMin = x / n * 360.0 - 180.0;
    double lonMax = (x + 1) / n * 360.0 - 180.0;
    double latMin = tile2lat(y + 1, z);
    double latMax = tile2lat(y, z);
    return new double[] {lonMin, latMin, lonMax, latMax};
  }

  private static double tile2lat(int y, int z) {
    double n = Math.PI - 2.0 * Math.PI * y / Math.pow(2.0, z);
    return Math.toDegrees(Math.atan(Math.sinh(n)));
  }
}
