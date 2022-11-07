package io.sitoolkit.bt.application;

import io.sitoolkit.bt.domain.asciidoctorj.AdocConverter;
import io.sitoolkit.bt.domain.file.AsciiDocParagraphResolver;
import io.sitoolkit.bt.domain.file.Paragraph;
import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.file.ParagraphResolverFactory;
import io.sitoolkit.bt.domain.translation.TranslationSpec;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.command.TranslationMode;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;

@Slf4j
public class FileTranslationService {

  private final Translator translator;
  private final ParagraphResolverFactory factory;
  private final Asciidoctor asciidoctor = Asciidoctor.Factory.create();

  public FileTranslationService(Translator translator, ParagraphResolverFactory factory) {
    this.translator = translator;
    this.factory = factory;
    asciidoctor.javaConverterRegistry().register(AdocConverter.class);
  }

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
    ParagraphResolver resolver = factory.createResolver(file);
    List<Paragraph> paragraphs = resolver.resolve(file);

    if (log.isDebugEnabled()) {
      for (Paragraph paragraph : paragraphs) {
        log.debug("Paragraph: {}", paragraph);
      }
    }

    List<ParagraphGroup> groups = ParagraphGroup.grouping(paragraphs);

    if (resolver.getClass().equals(AsciiDocParagraphResolver.class)) {
      groups.stream()
          .forEach(
              group ->
                  group.reduce(
                      asciidoctor.convert(
                          group.getAllTextWithoutDelimiter(),
                          OptionsBuilder.options().backend("adoc"))));
    } else {
      groups.stream()
          .forEach(group -> group.reduce(translator.translate(mode, group.getAllText())));
    }

    return paragraphs.stream()
        .map(resolver::correct)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
