package fr.birdia.tileonetonine.model.airbus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AirbusProperties {
  private String wmtsUrl;
  private String bearer;
  private String updatedAt;
}
