package io.sitoolkit.bt.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class HtmlParagraphResolver implements ParagraphResolver {

  private static final String DOCTYPE_TAG_WITHOUT_EXMARK = "<DOCTYPE html>";
  private static final String DOCTYPE_TAG = "<!DOCTYPE html>";

  @Override
  public List<Paragraph> resolve(Path file) {

    List<String> lines = null;

    try {
      lines = Files.readAllLines(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    List<Paragraph> paragraphs = new ArrayList<>();
    Paragraph paragraph = new Paragraph();

    for (String line : lines) {
      paragraph.append(line);
    }
    paragraphs.add(paragraph);

    return paragraphs;
  }

  @Override
  public String correct(Paragraph paragraph) {
    String translatedText = paragraph.getTranslatedText();
    // AWS TranslateでHTMLを翻訳する場合、DOCTYPEタグが崩れるため正しいタグに置換する
    if (StringUtils.isNotEmpty(translatedText)
        && translatedText.contains(DOCTYPE_TAG_WITHOUT_EXMARK)) {
      translatedText = translatedText.replaceAll(DOCTYPE_TAG_WITHOUT_EXMARK, DOCTYPE_TAG);
    }
    return translatedText;
  }
}
