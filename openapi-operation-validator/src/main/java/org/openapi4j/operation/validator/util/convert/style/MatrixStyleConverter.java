package org.openapi4j.operation.validator.util.convert.style;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.OAIContext;
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

public class MatrixStyleConverter extends FlatStyleConverter {
  private static final Pattern PREFIXED_SEMICOLON_NAME_REGEX = Pattern.compile("(?:;)([^;]+)(?:=)([^;]*)");

  private static final MatrixStyleConverter INSTANCE = new MatrixStyleConverter();

  private MatrixStyleConverter() {
  }

  public static MatrixStyleConverter instance() {
    return INSTANCE;
  }

  @Override
  public JsonNode convert(OAIContext context, AbsParameter<?> param, String paramName, String rawValue) {
    if (rawValue == null) {
      return null;
    }

    final Map<String, Object> paramValues;
    paramValues = getValues(context, param, paramName, rawValue, param.isExplode() ? ";" : ",");

    return convert(context, param, paramName, paramValues);
  }

  private Map<String, Object> getValues(OAIContext context, AbsParameter<?> param, String paramName, String rawValue, String splitPattern) {
    String type = param.getSchema().getSupposedType(context);

    if (TYPE_OBJECT.equals(type)) {
      return getObjectValues(context, param, paramName, rawValue, splitPattern);
    } else {
      Map<String, Object> values = new HashMap<>();

      if (TYPE_ARRAY.equals(type)) {
        List<String> arrayValues = getArrayValues(param, rawValue, splitPattern);
        if (arrayValues != null && !arrayValues.isEmpty()) {
          values.put(paramName, arrayValues);
        }
      } else {
        Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);
        if (matcher.matches()) {
          values.put(matcher.group(1), matcher.group(2));
        }
      }

      return values;
    }
  }

  private Map<String, Object> getObjectValues(OAIContext context,
                                              AbsParameter<?> param,
                                              String paramName,
                                              String rawValue,
                                              String splitPattern) {

    Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);

    if (param.isExplode()) {
      Map<String, Object> values = new HashMap<>();
      while (matcher.find()) {
        values.put(matcher.group(1), matcher.group(2));
      }
      return values;
    } else {
      return (matcher.find())
        ? getParameterValues(context, param, paramName, matcher.group(2), splitPattern)
        : null;
    }
  }

  private List<String> getArrayValues(AbsParameter<?> param, String rawValue, String splitPattern) {
    Matcher matcher = PREFIXED_SEMICOLON_NAME_REGEX.matcher(rawValue);

    if (param.isExplode()) {
      List<String> arrayValues = new ArrayList<>();
      while (matcher.find()) {
        arrayValues.add(matcher.group(2));
      }
      return arrayValues;
    } else {
      return matcher.matches()
        ? Arrays.asList(matcher.group(2).split(splitPattern))
        : null;
    }
  }
}
