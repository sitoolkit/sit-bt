package io.sitoolkit.bt.domain.file;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Paragraph {

  private StringBuilder text = new StringBuilder();

  @Getter @Setter private boolean ignored = false;
  @Getter @Setter private String escapePrefix = "";
  @Getter @Setter private int spaceCount = 0;

  public void append(String line) {
    if (text.length() > 0) {
      text.append(System.lineSeparator());
    }
    text.append(line);
  }

  public String getText() {
    return text.toString();
  }
}
