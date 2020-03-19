package org.openapi4j.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openapi4j.core.util.MultiStringMap;
import org.openapi4j.core.util.StringUtil;
import org.openapi4j.operation.validator.util.convert.TypeConverter;
import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DeepObjectStyleConverter {
  private static final DeepObjectStyleConverter INSTANCE = new DeepObjectStyleConverter();

  private DeepObjectStyleConverter() {
  }

  public static DeepObjectStyleConverter instance() {
    return INSTANCE;
  }

  public JsonNode convert(AbsParameter<?> param, String paramName, MultiStringMap<String> paramPairs, List<String> visitedParams) {
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Collection<String>> valueEntry : paramPairs.entrySet()) {
      String propPath = valueEntry.getKey();

      if (propPath.startsWith(paramName + "[")) {
        // tokenize
        List<String> properties = StringUtil.tokenize(propPath, "[]", true, true);
        if (properties.size() == 2) {
          String propName = properties.get(1);

          // Convert value or get string representation
          JsonNode value = TypeConverter.instance().convertPrimitive(
            param.getSchema().getProperty(propName),
            valueEntry.getValue().stream().findFirst().orElse(null));

          result.set(propName, value);

          visitedParams.add(propPath);
        }
      }
    }

    return result;
  }
}
