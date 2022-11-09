package io.sitoolkit.bt.domain.assemblies;

import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.util.FileTypeUtils;
import java.nio.file.Path;

public abstract class TranslationAssembliesFactory {
  public abstract ParagraphResolver getParagraphResolver();

  public abstract ParagraphGroup getParagraphGroup();

  public abstract Translator getTranslator();

  public static TranslationAssembliesFactory createTranslationAssemblies(Path file) {
    String fileType = FileTypeUtils.path2fileType(file);
    switch (fileType) {
      case "md":
        return new MarkdownTranslationAsbFactory();
      case "adoc":
        return new AdocTranslationAsbFactory();
      default:
        return new MarkdownTranslationAsbFactory();
    }
  }
}
