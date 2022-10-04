package io.sitoolkit.bt.domain.file;

public class ParagraphResolverFactory {

  public ParagraphResolver createResolver(String path) {
    String fileType = path.substring(path.lastIndexOf("."));
    switch (fileType) {
      case ".md":
        return new MarkdownParagraphResolver();
      case ".adoc":
        return new AsciiDocParagraphResolver();
      default:
        // TODO md, adoc形式以外を翻訳する汎用的なResolverを用意する
        return new MarkdownParagraphResolver();
    }
  }
}
