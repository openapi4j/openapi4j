package org.openapi4j.operation.validator.util.convert.style;

public class PipeDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final PipeDelimitedStyleConverter INSTANCE = new PipeDelimitedStyleConverter();

  private PipeDelimitedStyleConverter() {
    super("|");
  }

  public static PipeDelimitedStyleConverter instance() {
    return INSTANCE;
  }
}
