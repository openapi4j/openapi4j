package org.openapi4j.schema.validator;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.OAIContext;
import org.openapi4j.schema.validator.util.ExtValidatorInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Validation context and option bag.
 * @param <O> The Open API version type.
 */
public class ValidationContext<O extends OAI> {
  private final OAIContext<O> context;
  private final Map<String, JsonValidator> visitedRefs = new HashMap<>();
  private final Map<Byte, Boolean> defaultOptions = new HashMap<>();
  private final Map<String, ExtValidatorInstance> additionalValidators = new HashMap<>();

  public ValidationContext(OAIContext<O> context) {
    this.context = context;
  }

  public OAIContext<O> getContext() {
    return context;
  }

  /**
   * Add a reference to avoid looping.
   * This is internally used, you should not call this directly.
   *
   * @param ref       The reference expression.
   * @param validator The associated validator.
   */
  public void addReference(String ref, JsonValidator validator) {
    visitedRefs.put(ref, validator);
  }

  /**
   * Get a visited reference validator in any.
   * This is internally used, you should not call this directly.
   *
   * @param ref The reference expression.
   * @return The associated validator.
   */
  public JsonValidator getReference(String ref) {
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

  /**
   * Get the additional validators associated to the context.
   *
   * @return this.
   */
  public Map<String, ExtValidatorInstance> getValidators() {
    return additionalValidators;
  }

  /**
   * Add an additional validator as an override or a custom one.
   *
   * @param keyword                The keyword to match.
   * @param validatorInstantiation The instantiation to call when a validation should occur.
   * @return this.
   */
  public ValidationContext<O> addValidator(String keyword, ExtValidatorInstance validatorInstantiation) {
    additionalValidators.put(keyword, validatorInstantiation);
    return this;
  }
}
