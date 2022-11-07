package io.sitoolkit.bt.domain.file;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParagraphGroup {

  private static final String LINE_SEPARATER = "\n";
  private static final String DELIMITER_STRING = "==DELIMITER==";
  private static final String DELIMITER = LINE_SEPARATER + DELIMITER_STRING + LINE_SEPARATER;
  private static final String SPLITTER = DELIMITER_STRING + LINE_SEPARATER;

  private List<Paragraph> group = new ArrayList<>();

  private void add(Paragraph paragraph) {
    group.add(paragraph);
  }

  private boolean isEmpty() {
    return group.isEmpty();
  }

  public String getAllText() {
    return group.stream().map(Paragraph::getText).collect(Collectors.joining(DELIMITER));
  }

  public String getAllTextWithoutDelimiter() {
    return group.stream()
        .map(Paragraph::getText)
        .collect(Collectors.joining(LINE_SEPARATER + LINE_SEPARATER));
  }

  public void reduce(String text) {
    int i = 0;
    for (String paragraphText : text.split(SPLITTER)) {
      group.get(i++).setTranslatedText(paragraphText);
    }
  }

  public static List<ParagraphGroup> grouping(List<Paragraph> paragraphs) {
    List<ParagraphGroup> groups = new ArrayList<>();
    ParagraphGroup group = new ParagraphGroup();

    int length = 0;
    for (Paragraph paragraph : paragraphs) {

      if (paragraph.isIgnored()) {
        continue;
      }

      group.add(paragraph);
      length += paragraph.getText().length();

      if (length > 1000) {
        groups.add(group);
        group = new ParagraphGroup();
      }
    }

    if (!group.isEmpty()) {
      groups.add(group);
    }

    return groups;
  }
}
