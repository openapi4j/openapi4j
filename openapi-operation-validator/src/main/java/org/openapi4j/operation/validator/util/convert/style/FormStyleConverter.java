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

public class FormStyleConverter {
  private static final FormStyleConverter INSTANCE = new FormStyleConverter();

  private FormStyleConverter() {}

  public static FormStyleConverter instance() {
    return INSTANCE;
  }

  public JsonNode convert(AbsParameter<?> param, String paramName, MultiStringMap<String> paramPairs, List<String> visitedParams) {
    if (paramPairs == null) {
      return null;
    }

    JsonNode result;

    String type = param.getSchema().getSupposedType();
    if (TYPE_ARRAY.equals(type)) {
      result = getArrayValues(param, paramPairs.get(paramName));
      visitedParams.add(paramName);
    } else if (TYPE_OBJECT.equals(type)) {
      result = getObjectValues(param, paramName, paramPairs, visitedParams);
    } else {
      result = getPrimitiveValue(param, paramPairs.get(paramName));
      visitedParams.add(paramName);
    }

    return result;
  }

  private JsonNode getArrayValues(AbsParameter<?> param, Collection<String> paramValues) {
    if (paramValues == null) {
      return null;
    }

    List<Object> values = new ArrayList<>();
    if (param.isExplode()) {
      values.addAll(paramValues);
    } else {
      for (String paramValue : paramValues) {
        values.addAll(StringUtil.tokenize(paramValue, ",", false, false));
      }
    }

    return TypeConverter.instance().convertArray(param.getSchema().getItemsSchema(), values);
  }

  private JsonNode getObjectValues(AbsParameter<?> param, String paramName, MultiStringMap<String> values, List<String> visitedParams) {
    if (param.isExplode()) {
      return getExplodedObjectValues(param, values, visitedParams);
    } else {
      return getNotExplodedObjectValues(param, paramName, values, visitedParams);
    }
  }

  private JsonNode getExplodedObjectValues(AbsParameter<?> param, MultiStringMap<String> values, List<String> visitedParams) {
    ObjectNode result = JsonNodeFactory.instance.objectNode();

    for (Map.Entry<String, Schema> propEntry : param.getSchema().getProperties().entrySet()) {
      String propName = propEntry.getKey();
      Collection<String> paramValues = values.get(propName);

      if (paramValues != null) {
        JsonNode value = TypeConverter.instance().convertPrimitive(
          propEntry.getValue(),
          paramValues.stream().findFirst().orElse(null));

        result.set(propName, value);

        visitedParams.add(propName);
      }
    }

    return result.size() != 0 ? result : null;
  }

  private JsonNode getNotExplodedObjectValues(AbsParameter<?> param, String paramName, MultiStringMap<String> values, List<String> visitedParams) {
    Collection<String> paramValues = values.get(paramName);
    visitedParams.add(paramName);

    if (paramValues == null) {
      return null;
    }

    String value = paramValues.stream().findFirst().orElse(null);
    if (value == null) {
      return null;
    }

    ObjectNode result = JsonNodeFactory.instance.objectNode();

    List<String> arrayValues = StringUtil.tokenize(value, ",", false, false);
    int idx = 0;
    while (idx < arrayValues.size()) {
      String propName = arrayValues.get(idx++);
      String propValue = arrayValues.get(idx++);
      Schema propSchema = param.getSchema().getProperty(propName);

      result.set(propName, TypeConverter.instance().convertPrimitive(propSchema, propValue));
    }

    return result;
  }

  private JsonNode getPrimitiveValue(AbsParameter<?> param, Collection<String> paramValues) {
    if (paramValues == null) {
      return null;
    }

    return TypeConverter.instance().convertPrimitive(param.getSchema(), paramValues.stream().findFirst().orElse(null));
  }
}
