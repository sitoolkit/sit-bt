package io.sitoolkit.bt.application;

import io.sitoolkit.bt.domain.file.MarkdownParagraphResolver;
import io.sitoolkit.bt.domain.translation.TranslationSpec;
import io.sitoolkit.bt.domain.translation.Translator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileTranslationService {

  private final Translator translator;

  public void translate(TranslationSpec spec) {

    log.info("Input file:{}", spec.getInputFile().toAbsolutePath());
    log.info("Output file:{}", spec.getOutputFile().toAbsolutePath());

    Path outDir = spec.getOutputFile().toAbsolutePath().getParent();

    try {
      if (!outDir.toFile().exists()) {
        Files.createDirectories(outDir);
      }

      if (!spec.isTarget()) {
        Files.copy(spec.getInputFile(), spec.getOutputFile());
        return;
      }

      // String inputText = Files.readString(spec.getInputFile());
      // String outputText = translator.translate(spec.getMode(), inputText);

      MarkdownParagraphResolver resolver = new MarkdownParagraphResolver();
      String outputText =
          resolver.resolve(spec.getInputFile()).stream()
              .map(
                  paragraph -> {
                    if (paragraph.isIgnored()) {
                      return paragraph.getText();
                    } else {
                      return resolver.correct(
                          translator.translate(spec.getMode(), paragraph.getText()), paragraph);
                    }
                  })
              .collect(Collectors.joining(System.lineSeparator()));

      Files.writeString(spec.getOutputFile(), outputText);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
