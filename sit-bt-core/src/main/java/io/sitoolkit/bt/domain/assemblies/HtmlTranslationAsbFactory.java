package io.sitoolkit.bt.domain.assemblies;

import io.sitoolkit.bt.domain.file.HtmlParagraphResolver;
import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.translation.BasicTranslatorFactory;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.command.TranslationEngine;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HtmlTranslationAsbFactory extends TranslationAssembliesFactory {

  private final TranslationEngine engine;

  @Override
  public ParagraphResolver getParagraphResolver() {
    return new HtmlParagraphResolver();
  }

  @Override
  public ParagraphGroup getParagraphGroup() {
    return new ParagraphGroup(true);
  }

  @Override
  public Translator getTranslator() {
    return BasicTranslatorFactory.createTranslator(this.engine);
  }
}
