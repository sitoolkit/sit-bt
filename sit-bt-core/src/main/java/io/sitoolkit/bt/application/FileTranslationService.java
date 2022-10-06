package io.sitoolkit.bt.application;

import io.sitoolkit.bt.domain.file.Paragraph;
import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.file.ParagraphResolverFactory;
import io.sitoolkit.bt.domain.translation.TranslationSpec;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.command.TranslationMode;
import io.sitoolkit.bt.infrastructure.util.FileTypeUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileTranslationService {

  private final Translator translator;
  private final ParagraphResolverFactory factory;

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

      String outputText = translate(spec.getInputFile(), spec.getMode());

      Files.writeString(spec.getOutputFile(), outputText);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  String translate(Path file, TranslationMode mode) {
    ParagraphResolver resolver = factory.createResolver(FileTypeUtils.path2fileType(file));
    List<Paragraph> paragraphs = resolver.resolve(file);

    if (log.isDebugEnabled()) {
      for (Paragraph paragraph : paragraphs) {
        log.debug("Paragraph: {}", paragraph);
      }
    }

    List<ParagraphGroup> groups = ParagraphGroup.grouping(paragraphs);

    groups.stream().forEach(group -> group.reduce(translator.translate(mode, group.getAllText())));

    return paragraphs.stream()
        .map(resolver::correct)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
