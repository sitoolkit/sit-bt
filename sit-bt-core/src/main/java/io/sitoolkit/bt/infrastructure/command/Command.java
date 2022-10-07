package io.sitoolkit.bt.infrastructure.command;

import lombok.Data;

@Data
public class Command {

  private TranslationMode mode;

  private String source;

  private String target;

  private String filePattern;
}
