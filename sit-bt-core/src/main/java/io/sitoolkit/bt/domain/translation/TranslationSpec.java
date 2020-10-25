package io.sitoolkit.bt.domain.translation;

import io.sitoolkit.bt.infrastructure.command.TranslationMode;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationSpec {

  private Path inputFile;

  private Path outputFile;

  private TranslationMode mode;

  private boolean target;
}
