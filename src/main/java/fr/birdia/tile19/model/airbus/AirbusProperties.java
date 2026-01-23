package fr.birdia.tile19.model.airbus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AirbusProperties {
  private String wmtsUrl;
  private String bearer;
}
