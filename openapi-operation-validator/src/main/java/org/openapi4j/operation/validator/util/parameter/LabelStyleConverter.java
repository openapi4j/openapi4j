package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.parser.model.v3.Parameter;

import java.util.Map;

class LabelStyleConverter extends AbstractFlatStyleConverter {
  private static final LabelStyleConverter INSTANCE = new LabelStyleConverter();

  private LabelStyleConverter() {}

  public static LabelStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(Parameter param, String rawValue) {
    final Map<String, Object> paramValues;
    paramValues = getParameterValues(param, rawValue.substring(1), param.isExplode() ? "\\." : ",");

    return convert(param, paramValues);
  }
}
