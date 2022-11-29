package io.sitoolkit.bt.domain.translation;

import io.sitoolkit.bt.infrastructure.command.TranslationEngine;
import io.sitoolkit.bt.infrastructure.config.AtConfig;
import io.sitoolkit.bt.infrastructure.web.ApacheHttpWebClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicTranslatorFactory {

  private static final AtConfig config = AtConfig.load();
  private static final MinhonTranslator minhonTranslator =
      new MinhonTranslator(new ApacheHttpWebClient(config), config);
  private static final AwsTranslator awsTranslator = new AwsTranslator();
  private static final AzureTranslator azureTranslator = new AzureTranslator();

  public static Translator createTranslator(TranslationEngine engine) {
    return createTranslator(String.valueOf(engine));
  }

  public static Translator createTranslator(String engineName) {
    switch (engineName.toUpperCase()) {
      case "MINHON":
        return minhonTranslator;
      case "AWS":
        return awsTranslator;
      case "AZURE":
        return azureTranslator;
      default:
        return minhonTranslator;
    }
  }
}
