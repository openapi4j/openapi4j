package org.openapi4j.schema.validator;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.schema.validator.util.ExtValidatorInstance;

import java.util.HashMap;
import java.util.Map;

public class ValidationContext<O extends OAI> {
  private final OAIContext<O> context;
  private final Map<String, JsonValidator<O>> visitedRefs = new HashMap<>();
  private final Map<Byte, Boolean> defaultOptions = new HashMap<>();
  private final Map<String, ExtValidatorInstance<O>> additionalValidators = new HashMap<>();

  public ValidationContext(OAIContext<O> context) {
    this.context = context;
  }

  public OAIContext<O> getContext() {
    return context;
  }

  public void addReference(String ref, JsonValidator<O> validator) {
    visitedRefs.put(ref, validator);
  }

  public JsonValidator<O> getReference(String ref) {
    return visitedRefs.get(ref);
  }

  public ValidationContext<O> setOption(byte option, boolean value) {
    defaultOptions.put(option, value);
    return this;
  }

  /**
   * Get the value from the given option name.
   *
   * @param option The given option.
   * @return The corresponding value, {@code false} if the option is not set.
   */
  public boolean getOption(byte option) {
    Boolean value = defaultOptions.get(option);
    return value != null && value;
  }

  public Map<String, ExtValidatorInstance<O>> getValidators() {
    return additionalValidators;
  }

  public ValidationContext<O> addValidator(String keyword, ExtValidatorInstance<O> validatorInstantiation) {
    additionalValidators.put(keyword, validatorInstantiation);
    return this;
  }
}
