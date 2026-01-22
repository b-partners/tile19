package fr.birdia.tile19.model.airbus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class AirbusRequestBody {
  private int itemsPerPage;
  private int startPage;
  private String cloudCover;
  private String constellation;
  private String sortBy;
  private String workspace;
  private String processingLevel;
  private String relation;
  private Geometry geometry;
}
