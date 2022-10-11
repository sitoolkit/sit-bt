package io.sitoolkit.bt.domain.file;

import io.sitoolkit.bt.infrastructure.util.FileTypeUtils;
import java.nio.file.Path;

public class ParagraphResolverFactory {
  private static final MarkdownParagraphResolver mdResolver = new MarkdownParagraphResolver();
  private static final AsciiDocParagraphResolver adResolver = new AsciiDocParagraphResolver();

  public ParagraphResolver createResolver(Path file) {
    String fileType = FileTypeUtils.path2fileType(file);
    switch (fileType) {
      case "md":
        return mdResolver;
      case "adoc":
        return adResolver;
      default:
        // TODO md, adoc形式以外を翻訳する汎用的なResolverを用意する
        return mdResolver;
    }
  }
}
