package org.openapi4j.operation.validator.util.parameter;

import org.openapi4j.parser.model.v3.Parameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;

interface FlatStyleConverter extends StyleConverter {
  default Map<String, Object> getParameterValues(Parameter param, String rawValue, String splitPattern) {
    Map<String, Object> values = new HashMap<>();

    if (rawValue == null) {
      return values;
    }

    if (TYPE_OBJECT.equals(param.getSchema().getSupposedType())) {
      if (param.isExplode()) {
        Scanner scanner = new Scanner(rawValue);
        scanner.useDelimiter(splitPattern);
        while (scanner.hasNext()) {
          String[] propEntry = scanner.next().split("=");
          values.put(propEntry[0], propEntry[1]);
        }
        scanner.close();
      } else {
        String[] splitValues = rawValue.split(splitPattern);
        int i = 0;
        while (i < splitValues.length) {
          values.put(splitValues[i++], splitValues[i++]);
        }
      }
    } else if (TYPE_ARRAY.equals(param.getSchema().getSupposedType())) {
      values.put(param.getName(), Arrays.asList(rawValue.split(splitPattern)));
    } else {
      values.put(param.getName(), rawValue);
    }

    return values;
  }
}
