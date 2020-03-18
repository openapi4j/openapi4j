package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeepObjectStyleConverter extends FlatStyleConverter {
  private static final Pattern REGEX = Pattern.compile("(?:([^&]+)\\[([^&]+)])(?:=)([^&]*)");

  private static final DeepObjectStyleConverter INSTANCE = new DeepObjectStyleConverter();

  private DeepObjectStyleConverter() {}

  public static DeepObjectStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    Map<String, Object> paramValues = new HashMap<>();

    Schema paramSchema = param.getSchema();

    Matcher matcher = REGEX.matcher(rawValue);
    while (matcher.find()) {
      if (paramName.equalsIgnoreCase(matcher.group(1)) && paramSchema.hasProperty(matcher.group(2))) {
        paramValues.put(matcher.group(2), matcher.group(3));
      }
    }

    return convert(param, paramName, paramValues);
  }
}
