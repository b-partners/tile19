package fr.birdia.tileonetonine.model.airbus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureLinks {
  private SimpleLink delete;
  private SimpleLink monitor;
  private SimpleLink quicklook;
  private SimpleLink thumbnail;
  private SimpleLink wms;
  private SimpleLink wmts;
}
