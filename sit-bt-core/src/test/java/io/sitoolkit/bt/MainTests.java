package io.sitoolkit.bt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

public class MainTests {

  Main main = new Main();

  @Test
  public void fileTest() throws URISyntaxException, IOException {
    Path inputFile = Path.of(getClass().getResource("MainTests/fileTest/file.txt").toURI());
    Path outputFile = inputFile.getParent().resolve("file_en.txt");

    Files.deleteIfExists(outputFile);

    main.execute(
        new String[] {"-m", "ja2en", "-s", inputFile.toString(), "-t", outputFile.toString()});

    assertTrue(
        outputFile.toFile().exists(),
        "Expected output file doesn't exist: " + outputFile.toAbsolutePath());

    assertEquals("This is a pen.", Files.readString(outputFile));
  }

  @Test
  public void dirTest() throws URISyntaxException, IOException {
    Path inputDir =
        Path.of(getClass().getResource("MainTests/dirTest/index.adoc").toURI()).getParent();
    Path outputDir = inputDir.resolve("en");

    FileUtils.deleteDirectory(outputDir.toFile());

    main.execute(
        new String[] {
          "-m", "ja2en", "-p", "*.adoc", "-s", inputDir.toString(), "-t", outputDir.toString()
        });

    assertTrue(
        outputDir.toFile().exists(),
        "Expected output directory doesn't exist: " + outputDir.toAbsolutePath());

    assertEquals("This will be translated.", Files.readString(outputDir.resolve("index.adoc")));
    assertEquals("これは翻訳されません。", Files.readString(outputDir.resolve("some/content.txt")));
  }

  /*
  @Test
  public void markdownFileTest() throws URISyntaxException, IOException {
    Path inputFile = Path.of(getClass().getResource("MainTests/fileTest/file.md").toURI());
    Path outputFile = inputFile.getParent().resolve("file_en.md");

    main.execute(
        new String[] {"-m", "ja2en", "-s", inputFile.toString(), "-t", outputFile.toString()});

    Path expectedFile =
        Path.of(getClass().getResource("MainTests/fileTest/file_en_expected.md").toURI());

    assertEquals(Files.readString(expectedFile), Files.readString(outputFile));
  }
  */
}
