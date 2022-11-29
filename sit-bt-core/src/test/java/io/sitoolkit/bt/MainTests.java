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

    String inOutPath = inputFile + ":" + outputFile;
    main.execute(new String[] {"-m", "ja2en", inOutPath});

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

    String inOutPath = inputDir + ":" + outputDir;
    main.execute(new String[] {"-m", "ja2en", inOutPath, "-p", "*.adoc"});

    assertTrue(
        outputDir.toFile().exists(),
        "Expected output directory doesn't exist: " + outputDir.toAbsolutePath());

    assertEquals("This will be translated.", Files.readString(outputDir.resolve("index.adoc")));
    assertEquals("これは翻訳されません。", Files.readString(outputDir.resolve("some/content.txt")));
  }

  @Test
  public void markdownFileTest() throws URISyntaxException, IOException {
    Path inputFile = Path.of(getClass().getResource("MainTests/fileTest/file.md").toURI());
    Path outputFile = inputFile.getParent().resolve("file_en.md");

    main.execute(new String[] {"-m", "ja2en", inputFile.toString() + ":" + outputFile.toString()});

    Path expectedFile =
        Path.of(getClass().getResource("MainTests/fileTest/file_en_expected.md").toURI());

    assertEquals(Files.readString(expectedFile), Files.readString(outputFile));
  }

  @Test
  public void asciiDocFileTest() throws URISyntaxException, IOException {
    Path inputFile = Path.of(getClass().getResource("MainTests/fileTest/file.adoc").toURI());
    Path outputFile = inputFile.getParent().resolve("file_en.adoc");

    main.execute(
        new String[] {
          "-m", "ja2en", inputFile.toString() + ":" + outputFile.toString() + "-e" + "minhon"
        });

    Path expectedFile =
        Path.of(getClass().getResource("MainTests/fileTest/file_en_expected.adoc").toURI());

    assertEquals(Files.readString(expectedFile), Files.readString(outputFile));
  }

  @Test
  public void markdownFileAwsTest() throws URISyntaxException, IOException {
    Path inputFile = Path.of(getClass().getResource("MainTests/fileTest/file_aws.md").toURI());
    Path outputFile = inputFile.getParent().resolve("file_en_aws.md");

    main.execute(
        new String[] {
          "-m", "ja2en", inputFile.toString() + ":" + outputFile.toString(), "-e", "aws"
        });

    Path expectedFile =
        Path.of(getClass().getResource("MainTests/fileTest/file_en_aws_expected.md").toURI());

    assertEquals(Files.readString(expectedFile), Files.readString(outputFile));
  }

  @Test
  public void asciiDocFileAwsTest() throws URISyntaxException, IOException {
    Path inputFile = Path.of(getClass().getResource("MainTests/fileTest/file_aws.adoc").toURI());
    Path outputFile = inputFile.getParent().resolve("file_en_aws.adoc");

    main.execute(
        new String[] {
          "-m", "ja2en", inputFile.toString() + ":" + outputFile.toString(), "-e", "aws"
        });

    Path expectedFile =
        Path.of(getClass().getResource("MainTests/fileTest/file_en_aws_expected.adoc").toURI());

    assertEquals(Files.readString(expectedFile), Files.readString(outputFile));
  }

  @Test
  public void markdownFileAzureTest() throws URISyntaxException, IOException {
    Path inputFile = Path.of(getClass().getResource("MainTests/fileTest/file_azure.md").toURI());
    Path outputFile = inputFile.getParent().resolve("file_en_azure.md");

    main.execute(
        new String[] {
          "-m", "ja2en", inputFile.toString() + ":" + outputFile.toString(), "-e", "azure"
        });

    Path expectedFile =
        Path.of(getClass().getResource("MainTests/fileTest/file_en_azure_expected.md").toURI());

    assertEquals(Files.readString(expectedFile), Files.readString(outputFile));
  }
}
