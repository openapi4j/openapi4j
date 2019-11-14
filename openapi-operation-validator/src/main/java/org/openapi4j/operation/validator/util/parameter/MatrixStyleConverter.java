package org.openapi4j.operation.validator.util.parameter;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

class MatrixStyleConverter implements FlatStyleConverter {
  private static final Pattern PREFIXED_SEMICOLON_NAME_REGEX = Pattern.compile("(?:;)([^;]+)(?:=)([^;]*)");

  private static final MatrixStyleConverter INSTANCE = new MatrixStyleConverter();

  private MatrixStyleConverter() {}

  public static MatrixStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    final Map<String, Object> paramValues;
    paramValues = getParameterValues(param, paramName, rawValue, PREFIXED_SEMICOLON_NAME_REGEX, param.isExplode() ? ";" : ",");

    return paramValues.size() != 0 ? convert(param, paramName, paramValues) : null;
  }

  private Map<String, Object> getParameterValues(AbsParameter<?> param, String paramName, String rawValue, Pattern subPattern, String splitPattern) {
    String type = param.getSchema().getSupposedType();

    if (TYPE_OBJECT.equals(type)) {
      return getObjectValues(param, paramName, rawValue, subPattern, splitPattern);
    }

    Map<String, Object> values = new HashMap<>();

    if (TYPE_ARRAY.equals(type)) {
      values.put(
        paramName,
        getArrayValues(param, rawValue, subPattern, splitPattern));
    } else {
      Matcher matcher = subPattern.matcher(rawValue);
      if (matcher.matches()) {
        values.put(matcher.group(1), matcher.group(2));
      }
    }

    return values;
  }

  private Map<String, Object> getObjectValues(AbsParameter<?> param, String paramName, String rawValue, Pattern subPattern, String splitPattern) {
    if (param.isExplode()) {
      Map<String, Object> values = new HashMap<>();

      Matcher matcher = subPattern.matcher(rawValue);
      while (matcher.find()) {
        values.put(matcher.group(1), matcher.group(2));
      }

      return values;
    }

    Matcher matcher = subPattern.matcher(rawValue);
    return (matcher.find())
      ? getParameterValues(param, paramName, matcher.group(2), splitPattern)
      : null;
  }

  private List<String> getArrayValues(AbsParameter<?> param, String rawValue, Pattern subPattern, String splitPattern) {
    if (param.isExplode()) {
      List<String> arrayValues = new ArrayList<>();

      Matcher matcher = subPattern.matcher(rawValue);
      while (matcher.find()) {
        arrayValues.add(matcher.group(2));
      }

      return arrayValues;
    }

    Matcher matcher = subPattern.matcher(rawValue);

    return matcher.matches()
      ? Arrays.asList(matcher.group(2).split(splitPattern))
      : null;
  }
}
