package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DelimitedStyleConverter implements FlatStyleConverter {
  private static final Pattern REGEX = Pattern.compile("([^&]+)(?:=)([^&]*)");
  private final String delimRegex;

  DelimitedStyleConverter(String delimRegex) {
    this.delimRegex = delimRegex;
  }

  @Override
  public JsonNode convert(Parameter param, String rawValue) throws ResolutionException {
    if (rawValue == null) {
      return JsonNodeFactory.instance.nullNode();
    }

    if ("array".equals(param.getSchema().getType())) {
      Map<String, Object> paramValues = new HashMap<>();

      List<String> arrayValues = new ArrayList<>();
      String paramName = param.getName();

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
      paramValues.put(paramName, arrayValues);

      return convert(param, paramValues);
    } else {
      throw new ResolutionException("Delimited parameter cannot be an object or primitive.");
    }
  }
}
