package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;

class DelimitedStyleConverter extends FlatStyleConverter {
  private static final Pattern REGEX = Pattern.compile("([^&]+)(?:=)([^&]*)");
  private final String delimRegex;

  DelimitedStyleConverter(String delimRegex) {
    this.delimRegex = delimRegex;
  }

  @Override
  public JsonNode convert(AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    if (TYPE_ARRAY.equals(param.getSchema().getSupposedType())) {
      Map<String, Object> paramValues = new HashMap<>();

      List<String> arrayValues = new ArrayList<>();

      Matcher matcher = REGEX.matcher(rawValue);
      while(matcher.find()) {
        if (matcher.group(1).equals(paramName)) {
          if (param.isExplode()) {
            arrayValues.add(matcher.group(2));
          } else {
            arrayValues.addAll(Arrays.asList(matcher.group(2).split(delimRegex)));
          }
        }
      }

      if (arrayValues.isEmpty()) {
        return null;
      }

      paramValues.put(paramName, arrayValues);

      return convert(param, paramName, paramValues);
    } else {
      // Delimited parameter cannot be an object or primitive.
      return JsonNodeFactory.instance.nullNode();
    }
  }
}
