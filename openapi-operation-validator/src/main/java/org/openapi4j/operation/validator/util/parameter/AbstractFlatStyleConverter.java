package org.openapi4j.operation.validator.util.parameter;

import org.openapi4j.parser.model.v3.Parameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

abstract class AbstractFlatStyleConverter implements StyleConverter {
  Map<String, Object> getParameterValues(Parameter param, String rawValue, String splitPattern) {
    Map<String, Object> values = new HashMap<>();

    if (rawValue == null) {
      return values;
    }

    if ("object".equals(param.getSchema().getType())) {
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
    } else if ("array".equals(param.getSchema().getType())) {
      values.put(param.getName(), Arrays.asList(rawValue.split(splitPattern)));
    } else {
      values.put(param.getName(), rawValue);
    }

    return values;
  }
}
