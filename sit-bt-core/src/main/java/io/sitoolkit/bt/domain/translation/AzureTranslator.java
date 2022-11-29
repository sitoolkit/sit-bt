package io.sitoolkit.bt.domain.translation;

import com.google.gson.JsonArray;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
@RequiredArgsConstructor
public class AzureTranslator implements Translator {

  // TODO keyとlocationは利用者のローカルファイルから取得する
  private static final String KEY = "set-your-azure-api-key";
  private static final String LOCATION = "japaneast";
  private static final OkHttpClient client = new OkHttpClient();

  @Override
  public String ja2en(String text) {
    return translate(
        text,
        "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=ja&to=en");
  }

  @Override
  public String en2ja(String text) {
    return translate(
        text,
        "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=en&to=ja");
  }

  private String translate(String text, String apiUrl) {
    // TODO リクエスト処理は ApacheHttpWebClient.java に統合する
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.Companion.create("[{\"Text\": \"" + text + "\"}]", mediaType);
    Request request =
        new Request.Builder()
            .url(apiUrl)
            .post(body)
            .addHeader("Ocp-Apim-Subscription-Key", KEY)
            .addHeader("Ocp-Apim-Subscription-Region", LOCATION)
            .addHeader("Content-type", "application/json")
            .build();

    try {
      Response response = client.newCall(request).execute();
      String translatedText = getTranslatedTextByResponceBody(response.body());

      log.debug("Original Text: {}", text);
      log.debug("Translated Text: {}", translatedText);

      return translatedText;
    } catch (IOException e) {
      return "Error: " + e.getMessage();
    }
  }

  private static String getTranslatedTextByResponceBody(ResponseBody responseBody)
      throws IOException {
    String responseBodyAsString = responseBody.string();
    JsonArray jsonArray =
        com.google.gson.JsonParser.parseString(responseBodyAsString).getAsJsonArray();
    JsonArray translations = jsonArray.get(0).getAsJsonObject().getAsJsonArray("translations");
    return translations.get(0).getAsJsonObject().get("text").getAsString();
  }
}
