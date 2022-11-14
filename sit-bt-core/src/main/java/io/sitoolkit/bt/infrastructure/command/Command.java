package io.sitoolkit.bt.infrastructure.command;

import java.util.List;
import lombok.Data;

@Data
public class Command {

  private TranslationMode mode;

  private List<String> inOutPaths;

  private String filePattern;

  private TranslationEngine engine;
}
