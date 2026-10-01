package fr.birdia.tileonetonine.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {
  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("airbus.searchapi.baseurl", () -> "https://dummy.searchapi");
    registry.add("airbus.authentication.baseurl", () -> "https://dummy.authentication");
    registry.add("airbus.api.key", () -> "dummy");
  }
}
