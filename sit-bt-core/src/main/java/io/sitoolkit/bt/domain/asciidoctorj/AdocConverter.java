package io.sitoolkit.bt.domain.asciidoctorj;

import java.util.Map;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.DescriptionList;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.ast.Section;
import org.asciidoctor.ast.Table;
import org.asciidoctor.converter.ConverterFor;
import org.asciidoctor.converter.StringConverter;

@ConverterFor("adoc")
public class AdocConverter extends StringConverter {

  private final AdocNodeConverter adocNodeConverter;

  public AdocConverter(String backend, Map<String, Object> opts) {
    super(backend, opts);
    // DocumentNodeから翻訳エンジンの名称を取得し、adocNodeConverterを生成する.
    Document document = (Document) opts.get("document");
    this.adocNodeConverter = new AdocNodeConverter(String.valueOf(document.getAttribute("engine")));
  }

  @Override
  public String convert(ContentNode node, String transform, Map<Object, Object> o) {
    if (transform == null) {
      transform = node.getNodeName();
    }

    if (node instanceof Document) {
      return adocNodeConverter.convertDocumentNode((Document) node);
    } else if (node instanceof Section) {
      return adocNodeConverter.convertSectionNode((Section) node);
    } else if (node instanceof Block) {
      // Block要素は複数パターンが存在するので場合分けして処理する
      switch (transform) {
        case "paragraph":
          // open, listing, pass要素内 の テキスト（paragraph）は翻訳しない
          if (node.getParent() != null
              && node.getParent().getNodeName().matches("open|listing|pass")) {
            return ((Block) node).getContent().toString();
          }
          return adocNodeConverter.convertParagraphNode((Block) node);
        case "admonition":
          return adocNodeConverter.convertAdmonitionNode((Block) node);
        case "literal":
          return adocNodeConverter.convertLiteralNode((Block) node);
        case "open":
          return adocNodeConverter.convertBlockNodeContentBetweenSymbols((Block) node, "--");
        case "listing":
          return adocNodeConverter.convertBlockNodeContentBetweenSymbols((Block) node, "----");
        case "pass":
          return adocNodeConverter.convertBlockNodeContentBetweenSymbols((Block) node, "++++");
        case "sidebar":
          return adocNodeConverter.convertBlockNodeContentBetweenSymbols((Block) node, "****");
        case "thematic_break":
          // 水平線を出力
          return new StringBuilder().append("---").append("\n").toString();
        default:
          // TODO quote, inline_quoted, inline_anchorを変換するconvertメソッドを実装する.
          return ((Block) node).getContent().toString();
      }
    } else if (node instanceof Table) {
      return adocNodeConverter.convertTableNode((Table) node);
    } else if (node instanceof org.asciidoctor.ast.List) {
      return adocNodeConverter.convertlistNode((org.asciidoctor.ast.List) node);
    } else if (node instanceof DescriptionList) {
      return adocNodeConverter.convertDlistNode((DescriptionList) node);
    } else if (node instanceof PhraseNode) {
      return adocNodeConverter.convertInlineBreakNode((PhraseNode) node);
    }
    return "";
  }
}
