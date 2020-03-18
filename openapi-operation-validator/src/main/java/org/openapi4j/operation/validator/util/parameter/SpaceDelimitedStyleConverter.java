package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.Collection;

public class SpaceDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final SpaceDelimitedStyleConverter INSTANCE = new SpaceDelimitedStyleConverter();

  private SpaceDelimitedStyleConverter() {
    super(" ");
  }

  public static SpaceDelimitedStyleConverter instance() {
    return INSTANCE;
  }

  public JsonNode convert(AbsParameter<?> param, String paramName, Collection<String> values) {
    return convert(param, paramName, String.join(delimiter, values));
  }
}
