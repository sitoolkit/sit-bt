package io.sitoolkit.bt.domain.file;

import java.nio.file.Path;
import java.util.List;

public interface ParagraphResolver {
  List<Paragraph> resolve(Path file);

  default String correct(String translatedText, Paragraph paragraph) {
    return translatedText;
  }
}
