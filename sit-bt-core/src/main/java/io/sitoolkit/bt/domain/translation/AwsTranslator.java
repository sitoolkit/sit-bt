package io.sitoolkit.bt.domain.translation;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import lombok.RequiredArgsConstructor;

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

    TranslateTextRequest request =
        new TranslateTextRequest()
            .withText(text)
            .withSourceLanguageCode(sourceLanguageCode)
            .withTargetLanguageCode(targetLanguageCode);
    TranslateTextResult result = translate.translateText(request);

    return result.getTranslatedText();
  }
}
