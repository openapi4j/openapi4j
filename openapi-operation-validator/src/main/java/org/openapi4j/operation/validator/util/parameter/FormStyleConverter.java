package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.operation.validator.util.TypeConverter;
import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.*;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

public class FormStyleConverter {
  private static final FormStyleConverter INSTANCE = new FormStyleConverter();

  private FormStyleConverter() {}

  public static FormStyleConverter instance() {
    return INSTANCE;
  }

  public JsonNode convert(AbsParameter<?> param, String paramName, Collection<String> paramValues) {
    if (paramValues == null) {
      return null;
    }

    String type = param.getSchema().getSupposedType();
    if (TYPE_ARRAY.equals(type)) {
      return TypeConverter.instance().convertArray(param.getSchema().getItemsSchema(), new ArrayList<>(paramValues));
    } else if (TYPE_OBJECT.equals(type)) {
      Map<String, Object> values = new HashMap<>();
      values.put(paramName, paramValues.stream().findFirst().orElse(null));
      return TypeConverter.instance().convertObject(param.getSchema(), values);
    } else {
      return TypeConverter.instance().convertPrimitive(param.getSchema(), paramValues.stream().findFirst().orElse(null));
    }
  }
}
