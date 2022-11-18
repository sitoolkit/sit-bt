package io.sitoolkit.bt.infrastructure.command;

// TODO 翻訳エンジンを追加する
public enum TranslationEngine {
  MINHON;

  public static TranslationEngine parse(String engine) {
    return TranslationEngine.valueOf(engine.toUpperCase());
  }
}
