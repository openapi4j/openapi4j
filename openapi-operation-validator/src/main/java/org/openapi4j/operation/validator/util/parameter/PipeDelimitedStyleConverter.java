package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.Collection;

public class PipeDelimitedStyleConverter extends DelimitedStyleConverter {
  private static final PipeDelimitedStyleConverter INSTANCE = new PipeDelimitedStyleConverter();

  private PipeDelimitedStyleConverter() {
    super("|");
  }

  public static PipeDelimitedStyleConverter instance() {
    return INSTANCE;
  }

  public JsonNode convert(AbsParameter<?> param, String paramName, Collection<String> values) {
    return convert(param, paramName, String.join(delimiter, values));
  }
}
