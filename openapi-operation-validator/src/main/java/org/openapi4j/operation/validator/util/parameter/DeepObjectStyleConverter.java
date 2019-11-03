package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DeepObjectStyleConverter implements FlatStyleConverter {
  private static final Pattern REGEX = Pattern.compile("(?:([^&]+)\\[([^&]+)\\])(?:=)([^&]*)");

  private static final DeepObjectStyleConverter INSTANCE = new DeepObjectStyleConverter();

  private DeepObjectStyleConverter() {}

  public static DeepObjectStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(Parameter param, String rawValue) {
    if (rawValue == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    Map<String, Object> paramValues = new HashMap<>();

    Schema paramSchema = param.getSchema();

    Matcher matcher = REGEX.matcher(rawValue);
    while (matcher.find()) {
      if (param.getName().equalsIgnoreCase(matcher.group(1)) && paramSchema.hasProperty(matcher.group(2))) {
        paramValues.put(matcher.group(2), matcher.group(3));
      }
    }

    return convert(param, paramValues);
  }
}
