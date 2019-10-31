package org.openapi4j.operation.validator.util.parameter;

class SpaceDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final SpaceDelimitedStyleConverter INSTANCE = new SpaceDelimitedStyleConverter();

  private SpaceDelimitedStyleConverter() {
    super("%20");
  }

  public static SpaceDelimitedStyleConverter instance() {
    return INSTANCE;
  }
}
