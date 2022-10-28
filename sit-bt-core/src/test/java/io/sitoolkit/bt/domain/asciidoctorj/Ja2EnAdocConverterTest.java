package io.sitoolkit.bt.domain.asciidoctorj;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sitoolkit.bt.infrastructure.util.TestResourceUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.junit.jupiter.api.Test;

public class Ja2EnAdocConverterTest {

  @Test
  public void test() throws URISyntaxException, IOException {
    Asciidoctor asciidoctor = Asciidoctor.Factory.create();
    asciidoctor.javaConverterRegistry().register(AdocConverter.class);

    Path inputFile = TestResourceUtils.res2path(this, "input2.adoc");
    Path outputFile = inputFile.getParent().resolve("file_en_by_asciidoctorj.adoc");
    Path expectedFile = TestResourceUtils.res2path(this, "file_en_by_asciidoctorj_expected.adoc");

    asciidoctor.convertFile(
        inputFile.toFile(), OptionsBuilder.options().backend("adoc").toFile(outputFile.toFile()));

    assertEquals(Files.readString(expectedFile), Files.readString(outputFile));
  }
}
