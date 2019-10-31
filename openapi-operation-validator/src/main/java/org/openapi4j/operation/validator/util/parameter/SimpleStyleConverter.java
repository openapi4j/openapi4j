package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.parser.model.v3.Parameter;

import java.util.Map;

class SimpleStyleConverter extends AbstractFlatStyleConverter {
  private static final SimpleStyleConverter INSTANCE = new SimpleStyleConverter();

  private SimpleStyleConverter() {}

  public static SimpleStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(Parameter param, String rawValue) {
    final Map<String, Object> paramValues;
    paramValues = getParameterValues(param, rawValue, ",");

    return convert(param, paramValues);
  }
}
