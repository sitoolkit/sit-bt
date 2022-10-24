package io.sitoolkit.bt.domain.asciidoctorj;

import io.sitoolkit.bt.domain.translation.MinhonTranslator;
import io.sitoolkit.bt.domain.translation.Translator;
import io.sitoolkit.bt.infrastructure.command.TranslationMode;
import io.sitoolkit.bt.infrastructure.config.AtConfig;
import io.sitoolkit.bt.infrastructure.web.ApacheHttpWebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.Row;
import org.asciidoctor.ast.Section;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.ast.Table;
import org.asciidoctor.extension.Treeprocessor;

public class Ja2EnTreeprocessor extends Treeprocessor {

  private static final String CONTEXT = "context";
  private final AtConfig config = AtConfig.load();
  private final Translator translator =
      new MinhonTranslator(new ApacheHttpWebClient(config), config);

  public Ja2EnTreeprocessor() {}

  @Override
  public Document process(Document document) {
    translatedocumentJa2En(document);
    translateBlockJa2En(document);
    return document;
  }

  private void translatedocumentJa2En(Document document) {
    String title = document.getTitle();
    if (StringUtils.isNotBlank(title)) {
      document.setTitle(translator.translate(TranslationMode.JA2EN, title));
    }
    String docTitle = document.getDoctitle();
    if (StringUtils.isNotBlank(docTitle)) {
      document.setTitle(translator.translate(TranslationMode.JA2EN, docTitle));
    }
  }

  private void translateBlockJa2En(StructuralNode block) {

    List<StructuralNode> blocks = block.getBlocks();

    for (int i = 0; i < blocks.size(); i++) {
      StructuralNode currentBlock = blocks.get(i);
      if (currentBlock instanceof StructuralNode) {

        String title = currentBlock.getTitle();
        if (StringUtils.isNotBlank(title)) {
          currentBlock.setTitle(translator.translate(TranslationMode.JA2EN, title));
        }

        translateSectionNodes(currentBlock.findBy(Map.of(CONTEXT, ":section")));
        translateParagraphNodes(currentBlock.findBy(Map.of(CONTEXT, ":paragraph")));
        translateTableNodes(currentBlock.findBy(Map.of(CONTEXT, ":table")));
        // translatedUlistNodes(currentBlock.findBy(Map.of("context", ":ulist")));
        // translatedOlistNodes(currentBlock.findBy(Map.of("context", ":olist")));
        // translatedEmbeddedNodes(currentBlock.findBy(Map.of("context", ":embedded")));
        // translatedListingNodes(currentBlock.findBy(Map.of("context", ":listing")));
      }
    }
  }

  private void translateSectionNodes(List<StructuralNode> sectionNodes) {
    if (!sectionNodes.isEmpty()) {
      for (StructuralNode node : sectionNodes) {
        Section section = (Section) node;
        String title = section.getTitle();
        if (StringUtils.isNotBlank(title)) {
          section.setTitle(translator.translate(TranslationMode.JA2EN, title));
        }
      }
    }
  }

  private void translateParagraphNodes(List<StructuralNode> paragraphNodes) {
    if (!paragraphNodes.isEmpty()) {
      for (StructuralNode node : paragraphNodes) {
        List<String> lines = ((Block) node).getLines();
        List<String> translatedLines = new ArrayList<>();
        if (lines != null && !lines.isEmpty()) {
          lines.forEach(
              line -> {
                translatedLines.add(
                    translator.translate(TranslationMode.JA2EN, String.join("", line)));
              });
        }
        ((Block) node).setLines(translatedLines);
      }
    }
  }

  private void translateTableNodes(List<StructuralNode> tableNodes) {
    if (!tableNodes.isEmpty()) {
      for (StructuralNode node : tableNodes) {
        Table table = (Table) node;
        // Titleを翻訳
        String title = table.getTitle();
        if (StringUtils.isNotBlank(title)) {
          table.setTitle(translator.translate(TranslationMode.JA2EN, title));
        }
        // Headerを翻訳
        if (table.getHeader() != null && !table.getHeader().isEmpty()) {
          setTranslatedText2Row(table.getHeader());
        }
        // BODYを翻訳
        if (table.getBody() != null && !table.getBody().isEmpty()) {
          setTranslatedText2Row(table.getBody());
        }
      }
    }
  }

  private void setTranslatedText2Row(List<Row> rows) {
    for (Row row : rows) {
      row.getCells()
          .forEach(
              cell -> {
                cell.setSource(translator.translate(TranslationMode.JA2EN, cell.getText()));
              });
    }
  }
}
