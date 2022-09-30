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

  private static final String VERTICAL_BAR_REPLACEMENT = "<VB>";
  private final Pattern escapePrefixPattern = Pattern.compile("^\\.");
  private final Pattern quartPrefixPattern = Pattern.compile("(.*)(` )(.*)( `)(.*)");
  private final Pattern hatPrefixPattern = Pattern.compile("(.*)(\\^ )(.*)( \\^)(.*)");
  private final Pattern tildePrefixPattern = Pattern.compile("(.*)(\\~ )(.*)( \\~)(.*)");
  private final Pattern underScorePrefixPattern =
      Pattern.compile("(\\_){1,2}([^\\_]+)(\\_){1,2}(.*)", Pattern.DOTALL);
  private final Pattern footNotePrefixPattern =
      Pattern.compile("^(TIP|IMPORTANT|WARNING|CAUTION|NOTE) :.*", Pattern.DOTALL);
  private final Pattern consecutiveEqualsPrefixPattern =
      Pattern.compile("^(= ){2,6}(.*)", Pattern.DOTALL);
  private final Pattern bothendEqualsPrefixPattern =
      Pattern.compile("(=+)([^=]+)(=+)", Pattern.DOTALL);
  private final Pattern forwardEqualsPrefixPattern = Pattern.compile("(=+)([^=]+)", Pattern.DOTALL);
  private final Pattern asteriskPrefixPattern = Pattern.compile("(.*)(\\* +)(.*)", Pattern.DOTALL);
  private final Pattern periodPrefixPattern =
      Pattern.compile("(.*)(\\[\\.)( +)([^\\]]+\\])( +)(.*)", Pattern.DOTALL);
  private final Pattern hashTagPrefixPattern =
      Pattern.compile("(.*)(#)([^#]+)( +)(#)(.*)", Pattern.DOTALL);

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

      if (paragraph.isIgnored() && (line.startsWith("----") || line.startsWith("...."))) {
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
      // TODO paragraph単位ではなく、line単位で行頭の空行を設定できるようにする
      // 翻訳時に行頭の半角スペースが除去されるため、翻訳前に半角スペースの個数を設定する
      else if (line.startsWith(" ")) {
        paragraph.setSpaceCount(line.length() - line.replaceFirst("^( )+", "").length());
      }

      // 翻訳時に | が除去されるため、翻訳前に置換する
      if (line.startsWith("|")) {
        line = line.replaceAll("\\|", VERTICAL_BAR_REPLACEMENT);
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

    // 翻訳時に発生するAsciiDoc記法の崩れを調整する
    translatedText = adjust(translatedText);

    // TODO paragraph単位ではなく、line単位で行頭の空行を設定できるようにする
    // 退避した半角スペースを挿入する
    if (paragraph.getSpaceCount() > 0) {
      String space = "";
      for (int i = 0; i < paragraph.getSpaceCount(); i++) {
        space = space + " ";
      }
      translatedText = space + translatedText;
    }

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

  private String adjust(String translatedText) {
    if (translatedText.isEmpty()) {
      return translatedText;
    }

    // 文中の置換用文字列を「|」に置換する
    if (translatedText.contains(VERTICAL_BAR_REPLACEMENT)) {
      translatedText = translatedText.replaceAll(VERTICAL_BAR_REPLACEMENT, "|");
    }

    // 翻訳APIは「// xxx」を「/ / xxx」と翻訳するため、
    // 不要なスペースを削除する
    if (translatedText.contains("/ / ")) {
      translatedText = translatedText.replaceAll("/ / ", "// ");
    }

    // シングルクォート、ダブルクォートの表記が崩れるため調整する
    if (translatedText.contains("' `")) {
      translatedText = translatedText.replaceAll("' `", "'`");
    }
    if (quartPrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("(`)( )(.*)( )(`)", "$1$3$5");
    }

    // ハットの前後に半角スペースが挿入されるため、削除する
    if (hatPrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("(\\^)( )(.*)( )(\\^)", "$1$3$5");
    }

    // チルダの前後に半角スペースが挿入されるため、削除する
    if (tildePrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("(\\~)( )(.*)( )(\\~)", "$1$3$5");
    }

    // 翻訳APIはアンダースコアを「 _ 」と翻訳するため、
    // 「_」の両端の半角スペースを除去し、「text is _XXX_ ...」とする
    if (translatedText.contains("_")) {
      translatedText = translatedText.replaceAll("( \\_ | \\_|\\_ )", "_");
      if (underScorePrefixPattern.matcher(translatedText).matches()) {
        translatedText = translatedText.replaceAll("(\\_+)([^\\_]+)(\\_+)", " $1$2$3 ");
      }
    }

    // 翻訳APIはコロンを「 : 」と翻訳するため、不要なスペースを除去する
    if (footNotePrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceFirst(" :", ":");
    }
    if (translatedText.contains(" : :")) {
      translatedText = translatedText.replaceAll(" : :", "::");
    }

    // 翻訳APIはイコールを「 = 」と翻訳するため、
    // 「=」と後続の文字列の間にのみ半角スペースを挿入するように調整する
    if (consecutiveEqualsPrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("( =|= )", "=");
      // 翻訳APIは4つ以上連続した「=」を翻訳すると、「=」が1つ増えるので、調整する
      if (translatedText.contains("=====")) {
        translatedText = translatedText.replaceAll("=====", "====");
      }
      Matcher m1 = bothendEqualsPrefixPattern.matcher(translatedText);
      Matcher m2 = forwardEqualsPrefixPattern.matcher(translatedText);
      if (m1.matches()) {
        // 翻訳APIは「====== XXX ======」（前後6つのイコール）を翻訳すると、
        // 「======= XXX ========」（前7、後8つのイコール）となるため、$1 $2 $1 で置換する
        translatedText = m1.replaceAll("$1 $2 $1");
      } else if (m2.matches()) {
        translatedText = m2.replaceAll("$1 $2");
      }
    }

    // TODO Bold表記が崩れるので対応する
    // 翻訳APIは連続するアスタリスクを「 * 」と翻訳するため、
    // 「*」と後続の文字列の間にのみ半角スペースを挿入するように調整する
    if (asteriskPrefixPattern.matcher(translatedText).matches()) {
      translatedText =
          translatedText.replaceAll("\\* ", "*").replaceAll("(\\*+)([^\\*]+)", "$1 $2");
    }

    // 翻訳APIは [.XXX] を [. XXX] と翻訳するため、不要な半角スペースを削除する
    if (periodPrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("(\\[\\.)( +)([^\\]]+\\])( +)", "$1$3");
    }

    // 翻訳APIは「#」を「 #」と翻訳するため、不要な半角スペースを削除する
    if (hashTagPrefixPattern.matcher(translatedText).matches()) {
      translatedText = translatedText.replaceAll("(#)([^#]+)( +)(#)", "$1$2$4");
    }

    return translatedText;
  }
}
