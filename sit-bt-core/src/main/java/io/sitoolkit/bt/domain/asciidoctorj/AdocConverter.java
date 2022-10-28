package io.sitoolkit.bt.domain.asciidoctorj;

import io.sitoolkit.bt.domain.translation.MinhonTranslator;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.command.TranslationMode;
import io.sitoolkit.bt.infrastructure.config.AtConfig;
import io.sitoolkit.bt.infrastructure.web.ApacheHttpWebClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.DescriptionList;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.ListItem;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.ast.Row;
import org.asciidoctor.ast.Section;
import org.asciidoctor.ast.Table;
import org.asciidoctor.converter.ConverterFor;
import org.asciidoctor.converter.StringConverter;

@ConverterFor("adoc")
public class AdocConverter extends StringConverter {

  private static final String LINE_SEPARATOR = "\n";
  private static final String ATTR_ID = "id";
  private static final String ATTR_STYLE = "style";
  private static final String ATTR_LANG = "language";
  private static final String ATTR_HEADER = "header-option";
  private static final String ATTR_COLS = "cols";
  private static final String ATTR_FORMAT = "format";

  private final AtConfig config = AtConfig.load();
  private final Translator translator =
      new MinhonTranslator(new ApacheHttpWebClient(config), config);

  public AdocConverter(String backend, Map<String, Object> opts) {
    super(backend, opts);
  }

  @Override
  public String convert(ContentNode node, String transform, Map<Object, Object> o) {
    if (transform == null) {
      transform = node.getNodeName();
    }

    if (node instanceof Document) {
      return convertDocumentNode((Document) node);
    } else if (node instanceof Section) {
      return convertSectionNode((Section) node);
    } else if (node instanceof Block) {
      // Block要素は複数パターンが存在するので場合分けして処理する
      switch (transform) {
        case "paragraph":
          // open, listing, pass要素内 の テキスト（paragraph）は翻訳しない
          if (node.getParent() != null
              && node.getParent().getNodeName().matches("open|listing|pass")) {
            return ((Block) node).getContent().toString();
          }
          return convertParagraphNode((Block) node);
        case "admonition":
          return convertAdmonitionNode((Block) node);
        case "literal":
          return convertLiteralNode((Block) node);
        case "open":
          return convertBlockNodeContentBetweenSymbols((Block) node, "--");
        case "listing":
          return convertBlockNodeContentBetweenSymbols((Block) node, "----");
        case "pass":
          return convertBlockNodeContentBetweenSymbols((Block) node, "++++");
        case "sidebar":
          return convertBlockNodeContentBetweenSymbols((Block) node, "****");
        case "thematic_break":
          // 水平線を出力
          return new StringBuilder().append("---").append(LINE_SEPARATOR).toString();
        default:
          // TODO quote, inline_quoted, inline_anchorを変換するconvertメソッドを実装する.
          return ((Block) node).getContent().toString();
      }
    } else if (node instanceof Table) {
      return convertTableNode((Table) node);
    } else if (node instanceof org.asciidoctor.ast.List) {
      return convertlistNode((org.asciidoctor.ast.List) node);
    } else if (node instanceof DescriptionList) {
      return convertDlistNode((DescriptionList) node);
    } else if (node instanceof PhraseNode) {
      return convertInlineBreakNode((PhraseNode) node);
    }
    return "";
  }

  private String convertDocumentNode(Document document) {
    StringBuilder result = new StringBuilder();
    if (StringUtils.isNotBlank(document.getTitle())) {
      result
          .append("= ")
          .append(translate(document.getTitle()))
          .append(LINE_SEPARATOR)
          .append(LINE_SEPARATOR);
    }
    return result.append(document.getContent()).toString();
  }

  // Sectionを以下のフォーマットで変換する.
  // === title
  //
  // content
  private String convertSectionNode(Section section) {
    return new StringBuilder()
        .append(String.join("", Collections.nCopies(section.getLevel() + 1, "=")))
        .append(" ")
        .append(translate(section.getTitle()))
        .append(LINE_SEPARATOR)
        .append(LINE_SEPARATOR)
        .append(section.getContent())
        .toString();
  }

  // Blockを 以下のフォーマットで変換する.
  // attribute
  // .title
  // content
  private String convertParagraphNode(Block block) {
    StringBuilder result = new StringBuilder();
    // attribute
    result.append(getBlockNodeOption(block));
    // title
    result.append(getBlockNodeTitle(block));
    return result
        .append(translate(block.getContent().toString()))
        .append(LINE_SEPARATOR)
        .toString();
  }

  // Blockを style: content のフォーマットで変換する.
  // ex. NOTE: Asciidoc is so good.
  private String convertAdmonitionNode(Block block) {
    StringBuilder result = new StringBuilder();
    // attribute
    result.append(getBlockNodeOption(block));
    // title
    result.append(getBlockNodeTitle(block));
    if (block.getAttribute(ATTR_STYLE) != null) {
      result.append(block.getAttribute(ATTR_STYLE).toString()).append(": ");
    }
    return result
        .append(translate(block.getContent().toString()))
        .append(LINE_SEPARATOR)
        .toString();
  }

  // Blockを 半角スペース+content のフォーマットで変換する.
  // なお、元々のテキストが「行頭スペース」か「....」なのかは AST(Block)から判別不可.
  private String convertLiteralNode(Block block) {
    return new StringBuilder()
        .append(" ")
        .append(translate(block.getContent().toString()))
        .append(LINE_SEPARATOR)
        .toString();
  }

  // Blockを以下のフォーマットで変換する.
  // [[id]]
  // [source,language]
  // .title
  // symbols
  // content（翻訳なし）
  // symbols
  private String convertBlockNodeContentBetweenSymbols(Block block, String symbols) {
    StringBuilder result = new StringBuilder();
    // attribute
    result.append(getBlockNodeOption(block));
    // title
    result.append(getBlockNodeTitle(block));
    // body
    return result
        .append(symbols)
        .append(LINE_SEPARATOR)
        .append(block.getContent())
        .append(LINE_SEPARATOR)
        .append(symbols)
        .append(LINE_SEPARATOR)
        .toString();
  }

  // Blockのtitle部分を抽出する.
  private String getBlockNodeTitle(Block block) {
    if (StringUtils.isBlank(block.getTitle())) {
      return "";
    }
    return new StringBuilder()
        .append(".")
        .append(translate(block.getTitle()))
        .append(LINE_SEPARATOR)
        .toString();
  }

  // BlockのOption部分を抽出する.
  // [[id]]
  // [source,language]
  // [%hardbreaks]
  private String getBlockNodeOption(Block block) {
    StringBuilder option = new StringBuilder();
    // ex. 1=source, 2=ruby, style=source, language=ruby, id=app-listing, title=app.rb
    Map<String, Object> attr = block.getAttributes();
    if (attr != null && !attr.isEmpty()) {
      // adocファイル内のリンクに使用されるID
      if (attr.get(ATTR_ID) != null) {
        option
            .append("[[")
            .append(attr.get(ATTR_ID).toString())
            .append("]]")
            .append(LINE_SEPARATOR);
      }
      // ソースブロックオプション
      if (attr.get(ATTR_STYLE) != null && attr.get(ATTR_STYLE).toString().equals("source")) {
        option.append("[").append(attr.get(ATTR_STYLE).toString());
        if (attr.get(ATTR_LANG) != null) {
          option.append(",").append(attr.get(ATTR_LANG).toString());
        }
        option.append("]").append(LINE_SEPARATOR);
      }
      // 改行オプション
      if (attr.get("hardbreaks-option") != null) {
        option.append("[%hardbreaks]").append(LINE_SEPARATOR);
      }
    }
    return option.toString();
  }

  // Tableを以下のフォーマットで変換する.
  // .title
  // [options]
  // |===
  // header
  // body
  // |===
  private String convertTableNode(Table table) {
    // title
    StringBuilder title = new StringBuilder();
    if (StringUtils.isNotBlank(table.getTitle())) {
      title.append(".").append(translate(table.getTitle())).append(LINE_SEPARATOR);
    }
    // option
    StringBuilder headerOption = new StringBuilder();
    if (table.getHeader() != null && !table.getHeader().isEmpty()) {
      java.util.List<StringBuilder> options = new ArrayList<>();
      // ヘッダー指定の有無
      if (table.getAttribute(ATTR_HEADER) != null) {
        options.add(new StringBuilder().append("options=\"header\""));
      }
      // カラム幅
      if (table.getAttribute(ATTR_COLS) != null) {
        options.add(
            new StringBuilder()
                .append("cols=\"")
                .append(table.getAttribute(ATTR_COLS).toString())
                .append("\""));
      }
      // フォーマット
      if (table.getAttribute(ATTR_FORMAT) != null) {
        options.add(
            new StringBuilder()
                .append("format=\"")
                .append(table.getAttribute(ATTR_FORMAT).toString())
                .append("\""));
      }
      if (!options.isEmpty()) {
        headerOption
            .append("[")
            .append(String.join(", ", options))
            .append("]")
            .append(LINE_SEPARATOR);
      }
    }

    // formatがCSVか判別
    boolean isCsvFormat =
        table.getAttribute(ATTR_FORMAT) != null
            && table.getAttribute(ATTR_FORMAT).toString().equals("csv");
    // Header
    StringBuilder header =
        new StringBuilder().append(convertRowList(table.getHeader(), isCsvFormat));
    // BODY
    StringBuilder body = new StringBuilder().append(convertRowList(table.getBody(), isCsvFormat));

    return title
        .append(headerOption)
        .append("|===")
        .append(LINE_SEPARATOR)
        .append(header)
        .append(body)
        .append("|===")
        .append(LINE_SEPARATOR)
        .toString();
  }

  // List<Row> を テーブルのカラム部分のフォーマットに変換する.
  private String convertRowList(java.util.List<Row> rows, boolean isCsvFormat) {
    StringBuilder result = new StringBuilder();
    for (Row row : rows) {
      if (isCsvFormat) {
        // CSV形式の場合はカンマでカラムを区切る.
        row.getCells().forEach(cell -> result.append(translate(cell.getText())).append(","));
        // 末尾のカンマを削除した上で改行
        result.deleteCharAt(result.length() - 1).append(LINE_SEPARATOR);
      } else {
        // CSV形式以外の場合はパイプラインでカラムを区切る.
        row.getCells().forEach(cell -> result.append("|").append(translate(cell.getText())));
        result.append(LINE_SEPARATOR);
      }
    }
    return result.toString();
  }

  // Listを marker text のフォーマットで変換する.
  // ex.
  // .title
  // * level1
  // ** level2
  // *** level3
  private String convertlistNode(org.asciidoctor.ast.List list) {
    StringBuilder result = new StringBuilder();
    // ListTitle
    if (StringUtils.isNotBlank(list.getTitle())) {
      result.append(".").append(translate(list.getTitle())).append(LINE_SEPARATOR);
    }
    // ListItem
    list.findBy(Map.of("context", ":list_item"))
        .forEach(item -> result.append(convertListItem((ListItem) item)));
    return result.toString();
  }

  // DescriptionListを marker text のフォーマットで変換する.
  // ex.
  // . level1
  // .. level2
  // ... level3
  private String convertDlistNode(DescriptionList dlist) {
    StringBuilder result = new StringBuilder();
    // ListTitle
    if (StringUtils.isNotBlank(dlist.getTitle())) {
      result.append(".").append(translate(dlist.getTitle())).append(LINE_SEPARATOR);
    }
    // ListItem
    dlist.getItems().forEach(item -> result.append(convertListItem(item.getDescription())));
    return result.append(LINE_SEPARATOR).toString();
  }

  // ListItemを marker text のフォーマットで変換する.
  // ex. *** section3
  private String convertListItem(ListItem listItem) {
    if (listItem == null || !listItem.hasText()) {
      return "";
    }
    return new StringBuilder()
        .append(listItem.getMarker())
        .append(" ")
        .append(translate(listItem.getText()))
        .append(LINE_SEPARATOR)
        .toString();
  }

  // PhraseNodeを text + のフォーマットで変換する.
  private String convertInlineBreakNode(PhraseNode phrase) {
    return new StringBuilder().append(translate(phrase.getText())).append(" +").toString();
  }

  private String translate(String text) {
    // TODO translationMode、および、翻訳エンジンを指定可能にする.
    return translator.translate(TranslationMode.JA2EN, text);
  }
}
