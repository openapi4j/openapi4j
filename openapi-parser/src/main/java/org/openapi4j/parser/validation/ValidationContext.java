package org.openapi4j.parser.validation;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.validation.ValidationResults;

import java.util.IdentityHashMap;
import java.util.Map;

public class ValidationContext<O extends OAI> {
  private final Map<Object, Object> visitedObjects = new IdentityHashMap<>();

  public <V> void validate(O api, V value, final Validator<O, V> validator, ValidationResults results) {
    if (isVisited(value)) {
      return;
    }

    validator.validate(this, api, value, results);
  }

  private boolean isVisited(Object o) {
    if (visitedObjects.containsKey(o)) {
      return true;
    } else {
      visitedObjects.put(o, o);
      return false;
    }
  }
}
