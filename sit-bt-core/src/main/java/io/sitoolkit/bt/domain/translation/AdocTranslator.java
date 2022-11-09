package io.sitoolkit.bt.domain.translation;

import io.sitoolkit.bt.domain.asciidoctorj.AdocConverter;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;

public class AdocTranslator implements Translator {

  private final Asciidoctor asciidoctor = Asciidoctor.Factory.create();

  public AdocTranslator() {
    asciidoctor.javaConverterRegistry().register(AdocConverter.class);
  }

  @Override
  public String ja2en(String text) {
    return asciidoctor.convert(text, OptionsBuilder.options().backend("adoc"));
  }

  @Override
  public String en2ja(String text) {
    // TODO translationModeを指定可能にする.
    return text;
  }
}
