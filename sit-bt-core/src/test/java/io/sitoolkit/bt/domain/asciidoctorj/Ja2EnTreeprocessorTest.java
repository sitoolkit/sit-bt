package io.sitoolkit.bt.domain.asciidoctorj;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sitoolkit.bt.infrastructure.util.TestResourceUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.converter.StringConverter;
import org.junit.jupiter.api.Test;

public class Ja2EnTreeprocessorTest {

  @Test
  public void test() throws URISyntaxException, IOException {
    Asciidoctor asciidoctor = Asciidoctor.Factory.create();
    asciidoctor.javaExtensionRegistry().treeprocessor(Ja2EnTreeprocessor.class);
    asciidoctor.javaConverterRegistry().register(StringConverter.class);

    Path inputFile = TestResourceUtils.res2path(this, "input2.adoc");
    Path outputHtmlFile = inputFile.getParent().resolve("file_en_by_asciidoctorj.html");
    Path expectedHtmlFile =
        TestResourceUtils.res2path(this, "file_en_by_asciidoctorj_expected.html");

    // TODO adoc2htmlではなく、adoc2adocを可能にする
    asciidoctor.convertFile(
        inputFile.toFile(),
        OptionsBuilder.options().backend("html").toFile(outputHtmlFile.toFile()));

    assertEquals(Files.readString(expectedHtmlFile), Files.readString(outputHtmlFile));
  }
}
