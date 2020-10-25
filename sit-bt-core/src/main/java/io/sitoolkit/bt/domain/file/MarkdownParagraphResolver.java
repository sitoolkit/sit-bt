package io.sitoolkit.bt.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParagraphResolver {

  private Pattern escapePrefixPattern = Pattern.compile("^##+");

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

      paragraph.append(line);

      // 次の```までの範囲は翻訳しない
      if (line.startsWith("```")) {
        paragraph.setIgnored(true);

      } else if (line.startsWith("##")) {
        // ##から始まる場合、翻訳時に##+が除去されるため、翻訳前に退避する
        paragraph.setEscapePrefix(findPrefix(line));
      }
    }
    paragraphs.add(paragraph);

    return paragraphs;
  }

  private String findPrefix(String line) {
    Matcher matcher = escapePrefixPattern.matcher(line);
    matcher.find();
    return matcher.group();
  }

  public String correct(String translatedText, Paragraph paragraph) {

    String escapePrefix = paragraph.getEscapePrefix();

    // 待避接頭辞が未設定の場合は、補正しない
    if (escapePrefix.isEmpty()) {
      return translatedText;
    }

    // 待避接頭辞が設定済の場合は、翻訳APIにて発生する下記2つの事象に対して補正をかける
    // ※「#」を除いて翻訳した場合の翻訳結果が見出しとして適切な文章にならないため、
    // 　「#」がついた状態で翻訳を行い、翻訳結果の「#」の有無に応じて補正処理を行う
    // 　・先頭から「#」が2つ以上続き、その後の文字がアルファベットの場合に「#」が除去される
    // 　・先頭から「#」が2つ以上続き、その後の文字がアルファベット以外の場合に「#」の後ろの半角スペースが除去される
    if (translatedText.startsWith("##")) {
      StringBuilder correctPrefix = new StringBuilder();
      correctPrefix.append(escapePrefix);
      correctPrefix.append(" ");
      return translatedText.replace(escapePrefix, correctPrefix.toString());
    }

    StringBuilder correctText = new StringBuilder();
    correctText.append(escapePrefix);
    correctText.append(" ");
    correctText.append(translatedText);
    return correctText.toString();
  }
}
