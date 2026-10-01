package fr.birdia.tileonetonine.model.airbus;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class Geometry {
  private String type;
  private List<List<List<BigDecimal>>> coordinates;
}
