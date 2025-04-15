package fr.birdia.tile19.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class TileExtenderRequestBody {
  private int x;
  private int y;
  private int z;
  private String server;
  private String layer;
  private int shiftNb;
  private boolean isCropped;
  private double latitude;
  private double longitude;
}
