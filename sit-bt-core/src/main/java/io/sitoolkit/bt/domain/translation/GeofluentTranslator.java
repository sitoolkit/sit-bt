package io.sitoolkit.bt.domain.translation;

import com.jayway.jsonpath.JsonPath;
import io.sitoolkit.bt.infrastructure.config.AtConfig;
import io.sitoolkit.bt.infrastructure.web.WebClient;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GeofluentTranslator implements Translator {

  private final WebClient webClient;

  private final AtConfig config;

  // TODO GeoFuluent（もしくは、他翻訳エンジン）に対応する
  // Draft版動作確認のため、現状は みんなの翻訳API にリクエストしている
  public String ja2en(String text) {
    return translate(text, "https://mt-auto-minhon-mlt.ucri.jgn-x.jp/api/mt/generalNT_ja_en/");
  }

  @Override
  public String en2ja(String text) {
    return translate(text, "https://mt-auto-minhon-mlt.ucri.jgn-x.jp/api/mt/generalNT_en_ja/");
  }

  private String translate(String text, String apiUrl) {
    Map<String, String> params = new HashMap<>();

    params.put("key", config.getApiKey());
    params.put("name", config.getUser());
    params.put("type", "json");
    params.put("text", text);

    String response = webClient.post(apiUrl, params);
    String translatedText = JsonPath.read(response, "$.resultset.result.text");

    log.debug("Original Text: {}", text);
    log.debug("Translated Text: {}", translatedText);

    return translatedText;
  }
}
