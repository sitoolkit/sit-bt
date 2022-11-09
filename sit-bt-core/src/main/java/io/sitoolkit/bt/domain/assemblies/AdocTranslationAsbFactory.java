package io.sitoolkit.bt.domain.assemblies;

import io.sitoolkit.bt.domain.file.AsciiDocParagraphResolver;
import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.translation.AdocTranslator;
import io.sitoolkit.bt.domain.translation.Translator;

public class AdocTranslationAsbFactory extends TranslationAssembliesFactory {
  @Override
  public ParagraphResolver getParagraphResolver() {
    return new AsciiDocParagraphResolver();
  }

  @Override
  public ParagraphGroup getParagraphGroup() {
    return new ParagraphGroup(false);
  }

  @Override
  public Translator getTranslator() {
    return new AdocTranslator();
  }
}
