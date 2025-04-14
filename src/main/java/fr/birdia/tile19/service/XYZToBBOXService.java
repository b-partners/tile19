package fr.birdia.tile19.service;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.springframework.stereotype.Service;

@Service
public class XYZToBBOXService {

  public double[] xyzToBBox(int x, int y, int z) {
    // Convert tile x, y, z to lat/lon bounds
    double[] bboxWGS84 = tileBounds(x, y, z);

    // Transform EPSG:4326 toEPSG:3857
    CRSFactory crsFactory = new CRSFactory();
    CoordinateReferenceSystem wgs84 = crsFactory.createFromName("EPSG:4326");
    CoordinateReferenceSystem webMercator = crsFactory.createFromName("EPSG:3857");

    CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    CoordinateTransform transform = ctFactory.createTransform(wgs84, webMercator);

    ProjCoordinate ll = new ProjCoordinate(bboxWGS84[0], bboxWGS84[1]);
    ProjCoordinate ur = new ProjCoordinate(bboxWGS84[2], bboxWGS84[3]);

    ProjCoordinate llTransformed = new ProjCoordinate();
    ProjCoordinate urTransformed = new ProjCoordinate();

    transform.transform(ll, llTransformed);
    transform.transform(ur, urTransformed);

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
