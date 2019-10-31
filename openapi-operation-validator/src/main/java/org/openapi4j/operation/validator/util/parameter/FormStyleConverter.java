package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.parser.model.v3.Parameter;
import org.openapi4j.parser.model.v3.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FormStyleConverter extends AbstractFlatStyleConverter {
  private static final Pattern REGEX = Pattern.compile("([^&]+)(?:=)([^&]*)");

  private static final FormStyleConverter INSTANCE = new FormStyleConverter();

  private FormStyleConverter() {}

  public static FormStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  JsonNode convert(Parameter param, String rawValue) throws ResolutionException {
    String type = param.getSchema().getType();
    Map<String, Object> paramValues;

    if ("array".equals(type)) {
      paramValues = getArrayValues(param, rawValue);
    } else if ("object".equals(type)) {
      paramValues = getObjectValues(param, rawValue);
    } else {
      paramValues = getPrimitiveValue(param, rawValue);
    }

    return convert(param, paramValues);
  }

  private Map<String, Object> getArrayValues(Parameter param, String rawValue) throws ResolutionException {
    Map<String, Object> paramValues = new HashMap<>();

    List<String> arrayValues = new ArrayList<>();
    String paramName = param.getName();

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

  private Map<String, Object> getObjectValues(Parameter param, String rawValue) throws ResolutionException {
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
      return paramValues;
    } else {
      Matcher matcher = REGEX.matcher(rawValue);
      return (matcher.find())
        ? super.getParameterValues(param, matcher.group(2), ",")
        : new HashMap<>();
    }
  }

  private Map<String, Object> getPrimitiveValue(Parameter param, String rawValue) throws ResolutionException {
    Map<String, Object> paramValues = new HashMap<>();
    String paramName = param.getName();

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
