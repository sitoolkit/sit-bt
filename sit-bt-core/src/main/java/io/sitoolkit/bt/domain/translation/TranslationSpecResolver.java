package io.sitoolkit.bt.domain.translation;

import io.sitoolkit.bt.infrastructure.command.TranslationMode;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslationSpecResolver {

  public static Stream<TranslationSpec> toSpecs(
      String inOutPath, TranslationMode mode, String filePattern) {

    String[] inOutPathArr = inOutPath.split("->");

    Path inPath = Path.of(inOutPathArr[0]);
    Path outPath = Path.of(inOutPathArr[1]);

    // TODO Validation

    if (inPath.toFile().isFile()) {
      return Stream.of(new TranslationSpec(inPath, outPath, mode, true));
    } else {
      return toDirSpecs(inPath, outPath, mode, filePattern);
    }
  }

  static Stream<TranslationSpec> toDirSpecs(
      Path inDir, Path outDir, TranslationMode mode, String filePattern) {

    Pattern filePatternObj =
        filePattern == null
            ? null
            : Pattern.compile(filePattern.replace(".", "\\.").replace("*", ".*"));

    try {
      return Files.walk(inDir)
          .filter(path -> path.toFile().isFile())
          .map(
              inFile -> {
                boolean isTarget =
                    filePatternObj == null
                        ? true
                        : filePatternObj.matcher(inFile.getFileName().toString()).matches();
                Path outFile = outDir.resolve(inDir.relativize(inFile));
                return new TranslationSpec(inFile, outFile, mode, isTarget);
              })
          .collect(Collectors.toList())
          .stream();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
