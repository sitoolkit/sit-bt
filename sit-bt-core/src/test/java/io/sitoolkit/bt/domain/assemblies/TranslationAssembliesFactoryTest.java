package io.sitoolkit.bt.domain.assemblies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sitoolkit.bt.domain.file.AsciiDocParagraphResolver;
import io.sitoolkit.bt.domain.file.MarkdownParagraphResolver;
import io.sitoolkit.bt.domain.file.ParagraphGroup;
import io.sitoolkit.bt.domain.file.ParagraphResolver;
import io.sitoolkit.bt.domain.translation.AdocTranslator;
import io.sitoolkit.bt.domain.translation.GeofluentTranslator;
import io.sitoolkit.bt.domain.translation.MinhonTranslator;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.command.TranslationEngine;
import io.sitoolkit.bt.infrastructure.util.TestResourceUtils;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class TranslationAssembliesFactoryTest {

  @Test
  public void AdocTest() {
    Path inputAdocFile = TestResourceUtils.res2path(this, "input3.adoc");

    // Asciidoc × みんなの翻訳
    TranslationAssembliesFactory adocMinhonFactory =
        TranslationAssembliesFactory.createTranslationAssemblies(
            inputAdocFile, TranslationEngine.MINHON);
    assertEquals(adocMinhonFactory.getClass(), AdocTranslationAsbFactory.class);

    ParagraphResolver minhonResolver = adocMinhonFactory.getParagraphResolver();
    ParagraphGroup minhonParagraphGroup = adocMinhonFactory.getParagraphGroup();
    Translator minhonTranslator = adocMinhonFactory.getTranslator();
    assertEquals(minhonResolver.getClass(), AsciiDocParagraphResolver.class);
    assertEquals(minhonParagraphGroup.getClass(), ParagraphGroup.class);
    assertEquals(minhonTranslator.getClass(), AdocTranslator.class);

    // Asciidoc × GeoFluent
    TranslationAssembliesFactory adocGeofluentFactory =
        TranslationAssembliesFactory.createTranslationAssemblies(
            inputAdocFile, TranslationEngine.GEOFLUENT);
    assertEquals(adocMinhonFactory.getClass(), AdocTranslationAsbFactory.class);

    ParagraphResolver geofluentResolver = adocGeofluentFactory.getParagraphResolver();
    ParagraphGroup geofluentParagraphGroup = adocGeofluentFactory.getParagraphGroup();
    Translator geofluentTranslator = adocGeofluentFactory.getTranslator();
    assertEquals(geofluentResolver.getClass(), AsciiDocParagraphResolver.class);
    assertEquals(geofluentParagraphGroup.getClass(), ParagraphGroup.class);
    assertEquals(geofluentTranslator.getClass(), AdocTranslator.class);
  }

  @Test
  public void MarkdownTest() {
    Path inputMarkdownFile = TestResourceUtils.res2path(this, "input3.md");

    // Markdown × みんなの翻訳
    TranslationAssembliesFactory markdownMinhonFactory =
        TranslationAssembliesFactory.createTranslationAssemblies(
            inputMarkdownFile, TranslationEngine.MINHON);
    assertEquals(markdownMinhonFactory.getClass(), MarkdownTranslationAsbFactory.class);

    ParagraphResolver minhonResolver = markdownMinhonFactory.getParagraphResolver();
    ParagraphGroup minhonParagraphGroup = markdownMinhonFactory.getParagraphGroup();
    Translator minhonTranslator = markdownMinhonFactory.getTranslator();
    assertEquals(minhonResolver.getClass(), MarkdownParagraphResolver.class);
    assertEquals(minhonParagraphGroup.getClass(), ParagraphGroup.class);
    assertEquals(minhonTranslator.getClass(), MinhonTranslator.class);

    // Markdown × GeoFluent
    TranslationAssembliesFactory markdownGeofluentFactory =
        TranslationAssembliesFactory.createTranslationAssemblies(
            inputMarkdownFile, TranslationEngine.GEOFLUENT);
    assertEquals(markdownGeofluentFactory.getClass(), MarkdownTranslationAsbFactory.class);

    ParagraphResolver geofluentResolver = markdownGeofluentFactory.getParagraphResolver();
    ParagraphGroup geofluentParagraphGroup = markdownGeofluentFactory.getParagraphGroup();
    Translator geofluentTranslator = markdownGeofluentFactory.getTranslator();
    assertEquals(geofluentResolver.getClass(), MarkdownParagraphResolver.class);
    assertEquals(geofluentParagraphGroup.getClass(), ParagraphGroup.class);
    assertEquals(geofluentTranslator.getClass(), GeofluentTranslator.class);
  }
}
