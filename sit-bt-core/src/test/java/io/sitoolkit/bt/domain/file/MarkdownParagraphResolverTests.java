package io.sitoolkit.bt.domain.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.sitoolkit.bt.infrastructure.util.TestResourceUtils;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MarkdownParagraphResolverTests {

  MarkdownParagraphResolver resolver = new MarkdownParagraphResolver();

  @Test
  public void test() {
    Path input = TestResourceUtils.res2path(this, "input.md");

    List<Paragraph> paragraphs = resolver.resolve(input);

    System.out.println(paragraphs);

    assertEquals(5, paragraphs.size());
  }
}
