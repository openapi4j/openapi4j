package org.openapi4j.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openapi4j.core.util.MultiStringMap;
import org.openapi4j.core.util.StringUtil;
import org.openapi4j.operation.validator.util.convert.TypeConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.*;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

public class DeepObjectStyleConverter {
  private static final DeepObjectStyleConverter INSTANCE = new DeepObjectStyleConverter();

  private DeepObjectStyleConverter() {}

  public static DeepObjectStyleConverter instance() {
    return INSTANCE;
  }

  public JsonNode convert(AbsParameter<?> param, String paramName, MultiStringMap<String> paramPairs, List<String> visitedParams) {
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Collection<String>> valueEntry : paramPairs.entrySet()) {
      String propPath = valueEntry.getKey();

      if (propPath.startsWith(paramName + "[")) {
        fillProperties(param.getSchema(), propPath, valueEntry.getValue(), result);
        visitedParams.add(propPath);
      }
    }

    return result;
  }

  private void fillProperties(Schema propSchema, String propPath, Collection<String> propValues, ObjectNode result) {
    // tokenize
    List<String> properties = StringUtil.tokenize(propPath, "[]", true, true);

    for (int idx = 1; idx < properties.size(); ++idx) { // skip root param name
      String propName = properties.get(idx);
      result.set(propName, fillProperty(propSchema.getProperty(propName), propName, propValues));
    }
  }

  private JsonNode fillProperty(Schema schema, String propName, Collection<String> propValues) {
    String type = schema.getSupposedType();
    if (TYPE_ARRAY.equals(type)) {
      return TypeConverter.instance().convertArray(schema, new ArrayList<>(propValues));
    } else if (TYPE_OBJECT.equals(type)) {
      Map<String, Object> content = new HashMap<>();
      content.put(propName, propValues.stream().findFirst().orElse(null));
      return TypeConverter.instance().convertObject(schema, content);
    } else {
      return TypeConverter.instance().convertPrimitive(schema, propValues.stream().findFirst().orElse(null));
    }
  }
}
