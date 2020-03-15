package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.util.StringUtil;
import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.*;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;

class DelimitedStyleConverter extends FlatStyleConverter {
  protected final String delimiter;

  DelimitedStyleConverter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public JsonNode convert(AbsParameter<?> param, String paramName, String paramValue) {
    if (paramValue == null) {
      return null;
    }

    if (!TYPE_ARRAY.equals(param.getSchema().getSupposedType())) {
      // delimited parameter cannot be an object or primitive
      return null;
    }

    List<String> values = StringUtil.tokenize(paramValue, delimiter, false, false);

    List<String> arrayValues = new ArrayList<>();

    for (String value : values) {
      if (param.isExplode()) {
        arrayValues.add(value);
      } else {
        arrayValues.addAll(StringUtil.tokenize(value, delimiter, false, false));
      }
    }

    Map<String, Object> paramValues = new HashMap<>();

    if (!arrayValues.isEmpty()) { // Param found ?
      paramValues.put(paramName, arrayValues);
    }

    return convert(param, paramName, paramValues);
  }
}
