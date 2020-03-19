package org.openapi4j.operation.validator.util.convert.style;

public class SpaceDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final SpaceDelimitedStyleConverter INSTANCE = new SpaceDelimitedStyleConverter();

  private SpaceDelimitedStyleConverter() {
    super(" ");
  }

  public static SpaceDelimitedStyleConverter instance() {
    return INSTANCE;
  }
}
