package io.sitoolkit.bt.infrastructure.command;

public enum TranslationEngine {
  MINHON,
  GEOFLUENT,
  AWS,
  AZURE;

  public static TranslationEngine parse(String engine) {
    return TranslationEngine.valueOf(engine.toUpperCase());
  }
}
