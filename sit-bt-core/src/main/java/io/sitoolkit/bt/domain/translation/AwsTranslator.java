package io.sitoolkit.bt.domain.translation;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    // 合計ドキュメントサイズを Amazon Translate のドキュメントサイズの制限未満に維持する
    List<String> sentences = splitSegment(text, getLocaleBySourceLanguageCode(sourceLanguageCode));
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

  private Locale getLocaleBySourceLanguageCode(String sourceLanguageCode) {
    switch (sourceLanguageCode) {
      case "ja":
        return Locale.JAPAN;
      case "en":
        return Locale.ENGLISH;
      default:
        return Locale.ENGLISH;
    }
  }

  private List<String> splitSegment(String text, Locale locale) {
    List<String> res = new ArrayList<>();
    BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(locale);
    sentenceIterator.setText(text);
    int prevBoundary = sentenceIterator.first();
    int curBoundary = sentenceIterator.next();
    while (curBoundary != BreakIterator.DONE) {
      String sentence = text.substring(prevBoundary, curBoundary);
      res.add(sentence);
      prevBoundary = curBoundary;
      curBoundary = sentenceIterator.next();
    }
    return res;
  }
}
