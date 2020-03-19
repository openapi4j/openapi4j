package org.openapi4j.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.Map;

public class SimpleStyleConverter extends FlatStyleConverter {
  private static final SimpleStyleConverter INSTANCE = new SimpleStyleConverter();

  private SimpleStyleConverter() {}

  public static SimpleStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(AbsParameter<?> param, String paramName, String rawValue) {
    final Map<String, Object> paramValues;
    paramValues = getParameterValues(param, paramName, rawValue, ",");

    return convert(param, paramName, paramValues);
  }
}
