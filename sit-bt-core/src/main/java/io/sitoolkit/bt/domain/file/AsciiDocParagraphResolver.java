package io.sitoolkit.bt.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsciiDocParagraphResolver implements ParagraphResolver {

  private final Pattern escapePrefixPattern = Pattern.compile("^\\.");

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

      if (paragraph.isIgnored() && line.startsWith("```")) {
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

      // . から始まる場合、翻訳時に . が除去されるため、翻訳前に退避する
      if (line.startsWith(".")) {
        paragraph.setEscapePrefix(findPrefix(line));
      }

      paragraph.append(line);

      // 次の----までの範囲は翻訳しない
      if (line.startsWith("----") || line.startsWith("....")) {
        paragraph.setIgnored(true);
      }
    }
    paragraphs.add(paragraph);

    return paragraphs;
  }

  @Override
  public String correct(String translatedText, Paragraph paragraph) {

    // 翻訳APIはコロンを「 : 」と翻訳するため、
    // 「TIP」や「IMPORTANT」とコロンの間に空白を挿入しないように調整する
    if (translatedText.matches("^(TIP|IMPORTANT|WARNING|CAUTION|NOTE) :.*")) {
      translatedText = translatedText.replaceFirst(" :", ":");
    }

    // 翻訳APIはイコールを「 = 」と翻訳するため、
    // 「=」と後続の文字列の間にのみ半角スペースを挿入するように調整する
    if (Pattern.compile("^(= )(= )+(.*)", Pattern.DOTALL).matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("( =|= )", "=");
      Matcher m1 = Pattern.compile("(=+)([^=]+)(=+)(.*)", Pattern.DOTALL).matcher(translatedText);
      Matcher m2 = Pattern.compile("(=+)([^=]+)(.*)", Pattern.DOTALL).matcher(translatedText);
      if (m1.matches()) {
        translatedText = m1.replaceAll("$1 $2 $3$4");
      } else if (m2.matches()) {
        translatedText = m2.replaceAll("$1 $2$3");
      }
    }

    // 翻訳APIはアスタリスクを「*」と翻訳するため、
    // 「*」と後続の文字列の間にのみ半角スペースを挿入するように調整する
    if (Pattern.compile("(.*)(\\* +)(.*)", Pattern.DOTALL).matcher(translatedText).matches()) {
      translatedText =
          translatedText.replaceAll("\\* ", "*").replaceAll("(\\*+)([^\\*]+)", "$1 $2");
    }

    // TODO komatsu
    // テーブルの縦線 | が消えるので考慮する

    String escapePrefix = paragraph.getEscapePrefix();

    // 待避接頭辞が未設定の場合は、補正しない
    if (escapePrefix.isEmpty()) {
      return translatedText;
    }

    StringBuilder correctText = new StringBuilder();
    correctText.append(escapePrefix);
    correctText.append(translatedText);
    return correctText.toString();
  }

  private String findPrefix(String line) {
    Matcher matcher = escapePrefixPattern.matcher(line);
    matcher.find();
    return matcher.group();
  }
}
