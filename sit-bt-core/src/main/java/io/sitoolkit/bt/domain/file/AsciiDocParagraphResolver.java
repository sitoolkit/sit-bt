package io.sitoolkit.bt.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AsciiDocParagraphResolver implements ParagraphResolver {

  private Pattern escapePrefixPattern = Pattern.compile("^\\.");
  private Pattern consecutiveEqualsPrefixPattern =
      Pattern.compile("^(= ){2,6}(.*)", Pattern.DOTALL);

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

      if (paragraph.isIgnored() && line.startsWith("----")) {
        paragraph.append(line);
        paragraphs.add(paragraph);
        paragraph = new Paragraph();
        continue;
      }

      // 空行は翻訳しない
      if (line.isBlank()) {
        paragraphs.add(paragraph);
        paragraph = new Paragraph();
        paragraph.append(line);
        paragraph.setIgnored(true);
        paragraphs.add(paragraph);
        paragraph = new Paragraph();
        continue;
      }

      paragraph.append(line);

      // 次の----までの範囲は翻訳しない
      if (line.startsWith("----")) {
        paragraph.setIgnored(true);
      }
    }
    paragraphs.add(paragraph);

    return paragraphs;
  }

  @Override
  public String correct(Paragraph paragraph) {
    return correct(
        paragraph.getText(),
        paragraph.getTranslatedText(),
        paragraph.getEscapePrefix(),
        paragraph.isIgnored());
  }

  public String correct(
      String originalText, String translatedText, String escapePrefix, boolean ignored) {

    if (ignored) {
      return originalText;
    }

    if (escapePrefix.isEmpty()) {
      return translatedText;
    }

    // 翻訳APIはイコールを「 = 」と翻訳するため、
    // 「=」と後続の文字列の間にのみ半角スペースを挿入するように調整する
    if (consecutiveEqualsPrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("( =|= )", "=");
    }

    StringBuilder correctText = new StringBuilder();
    correctText.append(escapePrefix);
    correctText.append(translatedText);
    return correctText.toString();
  }
}
