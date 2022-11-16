package io.sitoolkit.bt.application;

import io.sitoolkit.bt.domain.assemblies.TranslationAssembliesFactory;
import io.sitoolkit.bt.domain.file.Paragraph;
import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.translation.TranslationSpec;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.command.TranslationEngine;
import io.sitoolkit.bt.infrastructure.command.TranslationMode;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileTranslationService {

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

      if (spec.getEngine() == null) {
        spec.setEngine(TranslationEngine.MINHON);
      }

      String outputText = translate(spec.getInputFile(), spec.getMode(), spec.getEngine());

      Files.writeString(spec.getOutputFile(), outputText);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  String translate(Path file, TranslationMode mode, TranslationEngine engine) {
    // 翻訳対象の ファイルの拡張子 と ユーザが指定した翻訳エンジン から、
    // 利用する Resolver, ParagraphGroup, translator を判別して取得する
    TranslationAssembliesFactory factory =
        TranslationAssembliesFactory.createTranslationAssemblies(file, engine);
    ParagraphResolver resolver = factory.getParagraphResolver();
    ParagraphGroup paragraphGroup = factory.getParagraphGroup();
    Translator translator = factory.getTranslator();

    List<Paragraph> paragraphs = resolver.resolve(file);

    if (log.isDebugEnabled()) {
      for (Paragraph paragraph : paragraphs) {
        log.debug("Paragraph: {}", paragraph);
      }
    }

    List<ParagraphGroup> groups = paragraphGroup.grouping(paragraphs);
    groups.stream().forEach(group -> group.reduce(translator.translate(mode, group.getAllText())));

    return paragraphs.stream()
        .map(resolver::correct)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
