package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.operation.validator.util.TypeConverter;
import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.Collection;
import java.util.Map;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

interface StyleConverter {
  JsonNode convert(AbsParameter<?> param, String paramName, String rawValue) throws ResolutionException;

  default JsonNode convert(AbsParameter<?> param, String paramName, Map<String, Object> paramValues) {
    if (paramValues == null || paramValues.size() == 0) {
      return null;
    }

    String style = param.getSchema().getSupposedType();
    Schema schema = param.getSchema();

    if (TYPE_OBJECT.equals(style)) {
      return TypeConverter.instance().convertObject(schema, paramValues);
    } else if (TYPE_ARRAY.equals(style)) {
      return TypeConverter.instance().convertArray(schema.getItemsSchema(), (Collection<Object>) paramValues.get(paramName));
    } else {
      return TypeConverter.instance().convertPrimitiveType(schema, paramValues.get(paramName));
    }
  }
}
