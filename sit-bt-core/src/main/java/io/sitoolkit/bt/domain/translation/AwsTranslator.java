package io.sitoolkit.bt.domain.translation;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class AwsTranslator implements Translator {

  private static final AWSCredentialsProvider awsCreds =
      DefaultAWSCredentialsProviderChain.getInstance();

  private static final AmazonTranslate translate =
      AmazonTranslateClient.builder()
          .withCredentials(new AWSStaticCredentialsProvider(awsCreds.getCredentials()))
          .build();

  @Override
  public String ja2en(String text) {
    return translate(text, "ja", "en");
  }

  @Override
  public String en2ja(String text) {
    return translate(text, "en", "ja");
  }

  private String translate(String text, String sourceLanguageCode, String targetLanguageCode) {
    if (StringUtils.isEmpty(text)) {
      return "";
    }

    // テキストを Amazon Translate が翻訳可能なサイズに分割する.
    List<String> sentences = SegmentSplitter.splitSegmentByLineBreak(text, 5000);
    List<TranslateTextRequest> requests = new ArrayList<>();
    sentences.stream()
        .map(
            sentence ->
                new TranslateTextRequest()
                    .withText(sentence)
                    .withSourceLanguageCode(sourceLanguageCode)
                    .withTargetLanguageCode(targetLanguageCode))
        .forEachOrdered(requests::add);

    List<TranslateTextResult> results = new ArrayList<>();
    requests.stream().map(translate::translateText).forEachOrdered(results::add);

    StringBuilder translatedText = new StringBuilder();
    results.forEach(result -> translatedText.append(result.getTranslatedText()));

    return translatedText.toString();
  }
}
