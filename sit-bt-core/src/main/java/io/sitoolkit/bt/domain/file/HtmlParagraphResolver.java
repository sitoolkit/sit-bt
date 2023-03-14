package io.sitoolkit.bt.domain.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class HtmlParagraphResolver implements ParagraphResolver {

  private static final String REPLACEMENT_TR_START = "\\=\\=TR_START\\=\\=";
  private static final String REPLACEMENT_TR_END = "\\=\\=TR_END\\=\\=";
  private static final String REPLACEMENT_P = "\\=\\=P\\=\\=";
  private static final String TAG_TR_START = "\\<tr\\>";
  private static final String TAG_TR_END = "\\<\\/tr\\>";
  private static final String TAG_BR = "\\<br\\>";
  private static final String REGEX_LINEBREAK = "\\r\\n|\\r|\\n";
  private static final String REGEX_P_TABLEBLOCK = "(^.*\\<p class\\=\"tableblock\"\\>)(.*)(\\<\\/p\\>.*)";
  private static final Pattern PATTERN_P_TABLEBLOCK = Pattern.compile(REGEX_P_TABLEBLOCK);

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
    // 処理中のテキストがthタグ、もしくは、tdタグ内に存在するかを判定するフラグ
    boolean inTableData = false;
    // thタグ、tdタグ内のテキストを一時的に格納する変数
    StringBuilder tableData = new StringBuilder();

    for (String line : lines) {

      if (paragraph.isIgnored()
          && (line.contains("</style>") || line.contains("</script>") || line.contains("</colgroup>"))) {
        paragraph.append(line);
        paragraphs.add(paragraph);
        paragraph = new Paragraph();
        continue;
      }

      if (!paragraph.isIgnored()) {
        // 翻訳時の記法崩れ防止のため、trタグは置換用文字列に変換しておく
        if (line.contains("<tr>")) {
          line = line.replaceAll(TAG_TR_START, REPLACEMENT_TR_START);
        }
        if (line.contains("</tr>")) {
          line = line.replaceAll(TAG_TR_END, REPLACEMENT_TR_END);
        }
        // 暫定対応
        // th,tdタグ内の <p class="tableblock"></p> に囲まれたテキストを翻訳した際、
        // テキストがtdタグ外に出力される事象を防ぐため、テキストを置換用文字列で囲む
        if (line.contains("<p class=\"tableblock\">")) {
          Matcher matcher = PATTERN_P_TABLEBLOCK.matcher(line);
          if (matcher.matches()) {
            // group(2) は pタグ内のテキスト部分
            line = matcher.group(1)
                + "==P=="
                + matcher.group(2)
                + "==P=="
                + matcher.group(3);
            // 暫定対応
            // tdタグ内のpタグ内のテキストにbrタグが存在する場合、
            // brタグがtdタグの外へ出力される事象を防ぐため、brタグを削除する
            if (line.contains("<br>")) {
              line = line.replaceAll(TAG_BR, "");
            }
          }
        }
        // 翻訳時の記法崩れ防止のため、thタグ、tdタグは1行にまとめる
        if (line.contains("</th>") || line.contains("</td>")) {
          tableData.append(line.replaceAll(REGEX_LINEBREAK, ""));
          paragraph.append(tableData.toString());
          tableData.delete(0, tableData.length());
          inTableData = false;
          continue;
        }
        // 空行は翻訳しない
        if (!paragraph.isIgnored() && line.isBlank()) {
          paragraphs.add(paragraph);
          paragraph = new Paragraph();
          paragraph.append(line);
          paragraph.setIgnored(true);
          paragraphs.add(paragraph);
          paragraph = new Paragraph();
          continue;
        }
        // 翻訳時の記法崩れ防止のため、thタグ、tdタグは1行にまとめる
        if (inTableData || line.contains("<th") || line.contains("<td")) {
          tableData.append(line.replaceAll(REGEX_LINEBREAK, ""));
          inTableData = true;
          continue;
        }
      }

      paragraph.append(line);

      // 以下のタグが次にくるまで翻訳しない
      // ・</style>
      // ・</script>
      // ・</colgroup>
      if (line.contains("<style>") || line.contains("<script>") || line.contains("<colgroup>")) {
        if (paragraph.getText() != null && !paragraph.getText().isEmpty()) {
          paragraphs.add(paragraph);
          paragraph = new Paragraph();
        }
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

    // 置換用文字列を元に戻す
    if (StringUtils.isNotBlank(translatedText)) {
      translatedText = translatedText
          .replaceAll(REPLACEMENT_TR_START, TAG_TR_START)
          .replaceAll(REPLACEMENT_TR_END, TAG_TR_END)
          .replaceAll(REPLACEMENT_P, "");
    }

    if (escapePrefix.isEmpty()) {
      return translatedText;
    }

    StringBuilder correctText = new StringBuilder();
    correctText.append(escapePrefix);
    correctText.append(" ");
    correctText.append(translatedText);
    return correctText.toString();
  }
}
