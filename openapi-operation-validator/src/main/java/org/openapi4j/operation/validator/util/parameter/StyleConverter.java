package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.operation.validator.util.TypeConverter;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.Map;

interface StyleConverter {
  JsonNode convert(Parameter param, String rawValue) throws ResolutionException;

  default JsonNode convert(Parameter param, Map<String, Object> paramValues) {
    String style = param.getSchema().getType();
    Schema schema = param.getSchema();

    if ("object".equals(style)) {
      return TypeConverter.instance().convertObject(schema, paramValues);
    } else if ("array".equals(style)) {
      return TypeConverter.instance().convertArray(schema.getItemsSchema(), paramValues.get(param.getName()));
    } else {
      return TypeConverter.instance().convertPrimitiveType(schema, paramValues.get(param.getName()));
    }
  }
}
