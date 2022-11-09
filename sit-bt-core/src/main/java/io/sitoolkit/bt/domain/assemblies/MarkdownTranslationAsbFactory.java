package io.sitoolkit.bt.domain.assemblies;

import io.sitoolkit.bt.domain.file.MarkdownParagraphResolver;
import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.translation.MinhonTranslator;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.config.AtConfig;
import io.sitoolkit.bt.infrastructure.web.ApacheHttpWebClient;

public class MarkdownTranslationAsbFactory extends TranslationAssembliesFactory {
  @Override
  public ParagraphResolver getParagraphResolver() {
    return new MarkdownParagraphResolver();
  }

  @Override
  public ParagraphGroup getParagraphGroup() {
    return new ParagraphGroup(true);
  }

  @Override
  public Translator getTranslator() {
    AtConfig config = AtConfig.load();
    return new MinhonTranslator(new ApacheHttpWebClient(config), config);
  }
}
