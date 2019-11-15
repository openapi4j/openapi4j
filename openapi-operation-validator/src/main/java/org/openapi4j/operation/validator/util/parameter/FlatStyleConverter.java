package org.openapi4j.operation.validator.util.parameter;

import org.openapi4j.parser.model.v3.AbsParameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

abstract class FlatStyleConverter implements StyleConverter {
  Map<String, Object> getParameterValues(AbsParameter<?> param, String paramName, String rawValue, String splitPattern) {
    if (rawValue == null) {
      return null;
    }

    Map<String, Object> values = new HashMap<>();

    if (TYPE_OBJECT.equals(param.getSchema().getSupposedType())) {
      if (param.isExplode()) {
        handleExplodedObject(param, splitPattern, rawValue, values);
      } else {
        handleNotExplodedObject(param, splitPattern, rawValue, values);
      }
    } else if (TYPE_ARRAY.equals(param.getSchema().getSupposedType())) {
      values.put(paramName, Arrays.asList(rawValue.split(splitPattern)));
    } else {
      values.put(paramName, rawValue);
    }

    return values;
  }

  private void handleExplodedObject(AbsParameter<?> param, String splitPattern, String rawValue, Map<String, Object> values) {
    Scanner scanner = new Scanner(rawValue);
    scanner.useDelimiter(splitPattern);
    while (scanner.hasNext()) {
      String[] propEntry = scanner.next().split("=");
      if (propEntry.length == 2 && param.getSchema().hasProperty(propEntry[0])) {
        values.put(propEntry[0], propEntry[1]);
      }
    }
    scanner.close();
  }

  private void handleNotExplodedObject(AbsParameter<?> param, String splitPattern, String rawValue, Map<String, Object> values) {
    String[] splitValues = rawValue.split(splitPattern);
    if (splitValues.length % 2 == 0) {
      int i = 0;
      while (i < splitValues.length) {
        if (param.getSchema().hasProperty(splitValues[i])) {
          values.put(splitValues[i++], splitValues[i++]);
        }
      }
    }
  }
}
