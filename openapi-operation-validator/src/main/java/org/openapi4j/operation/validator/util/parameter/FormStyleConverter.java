package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.parser.model.v3.AbsParameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

class FormStyleConverter implements FlatStyleConverter {
  private static final Pattern REGEX = Pattern.compile("([^&]+)(?:=)([^&]*)");

  private static final FormStyleConverter INSTANCE = new FormStyleConverter();

  private FormStyleConverter() {}

  public static FormStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    String type = param.getSchema().getSupposedType();
    Map<String, Object> paramValues;

    if (TYPE_ARRAY.equals(type)) {
      paramValues = getArrayValues(param, paramName, rawValue);
    } else if (TYPE_OBJECT.equals(type)) {
      paramValues = getObjectValues(param, paramName, rawValue);
    } else {
      paramValues = getPrimitiveValue(paramName, rawValue);
    }

    return convert(param, paramName, paramValues);
  }

  private Map<String, Object> getArrayValues(AbsParameter<?> param, String paramName, String rawValue) {
    Map<String, Object> paramValues = new HashMap<>();

    List<String> arrayValues = new ArrayList<>();

    Matcher matcher = REGEX.matcher(rawValue);
    while(matcher.find()) {
      if (matcher.group(1).equals(paramName)) {
        if (param.isExplode()) {
          arrayValues.add(matcher.group(2));
        } else {
          arrayValues.addAll(Arrays.asList(matcher.group(2).split(",")));
        }
      }
    }
    paramValues.put(paramName, arrayValues);

    return paramValues;
  }

  private Map<String, Object> getObjectValues(AbsParameter<?> param, String paramName, String rawValue) {
    Schema paramSchema = param.getSchema();

    if (param.isExplode()) {
      Map<String, Object> paramValues = new HashMap<>();

      Scanner scanner = new Scanner(rawValue);
      scanner.useDelimiter("&");
      while (scanner.hasNext()) {
        String[] propEntry = scanner.next().split("=");
        if (paramSchema.hasProperty(propEntry[0])) {
          paramValues.put(propEntry[0], propEntry[1]);
        }
      }
      scanner.close();
      return paramValues;
    } else {
      Matcher matcher = REGEX.matcher(rawValue);
      return (matcher.find())
        ? getParameterValues(param, paramName, matcher.group(2), ",")
        : new HashMap<>();
    }
  }

  private Map<String, Object> getPrimitiveValue(String paramName, String rawValue) {
    Map<String, Object> paramValues = new HashMap<>();

    Matcher matcher = REGEX.matcher(rawValue);
    while(matcher.find()) {
      if (matcher.group(1).equals(paramName)) {
        paramValues.put(paramName, matcher.group(2));
        break;
      }
    }

    return paramValues;
  }
}
