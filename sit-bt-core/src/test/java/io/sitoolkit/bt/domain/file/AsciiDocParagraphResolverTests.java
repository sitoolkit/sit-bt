package io.sitoolkit.bt.domain.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sitoolkit.bt.infrastructure.util.TestResourceUtils;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AsciiDocParagraphResolverTests {

  AsciiDocParagraphResolver resolver = new AsciiDocParagraphResolver();

  @Test
  public void test() {
    Path input = TestResourceUtils.res2path(this, "input.adoc");

    List<Paragraph> paragraphs = resolver.resolve(input);

    System.out.println(paragraphs);

    assertEquals(11, paragraphs.size());
  }
}
