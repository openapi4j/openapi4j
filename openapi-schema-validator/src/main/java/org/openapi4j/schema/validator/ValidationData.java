package org.openapi4j.schema.validator;

import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;

import java.util.Collection;

/**
 * This class represents the validation results for output.
 * As input, this includes user data {@code V} for delegation or variability in custom validators.
 *
 * @param <V> The type of user data object.
 *            Can be a simple boolean of complex object with custom methods.
 */
public final class ValidationData<V> {
  private final ValidationResults validationResults;
  private final V delegate;

  public ValidationData() {
    this(null);
  }

  public ValidationData(V delegate) {
    this.validationResults = new ValidationResults();
    this.delegate = delegate;
  }

  /**
   * Get the current validation results.
   *
   * @return The current results.
   */
  public ValidationResults results() {
    return validationResults;
  }

  /**
   * User object. Can be a simple boolean of complex object with custom methods.
   *
   * @return The delegate.
   */
  public V delegate() {
    return delegate;
  }

  /**
   * Add a result.
   *
   * @param result  validation result to append.
   * @param msgArgs message arguments to get formatted message.
   */
  public void add(ValidationResult result, Object... msgArgs) {
    validationResults.add(result, msgArgs);
  }

  /**
   * Add a result.
   *
   * @param crumbInfo path item to add the result.
   * @param result    validation result to append.
   * @param msgArgs   message arguments to get formatted message.
   */
  public void add(ValidationResults.CrumbInfo crumbInfo, ValidationResult result, Object... msgArgs) {
    validationResults.add(crumbInfo, result, msgArgs);

  }

  /**
   * Append other results to the current stack.
   *
   * @param results The stack to append. Must be non {@code null}.
   */
  public void add(ValidationResults results) {
    validationResults.add(results);
  }

  /**
   * Append other results to the current stack with given parent crumbs.
   *
   * @param parentCrumbs The given parent crumbs to insert. Must be non {@code null}.
   * @param results      The stack to append. Must be non {@code null}.
   */
  public void add(Collection<ValidationResults.CrumbInfo> parentCrumbs, ValidationResults results) {
    validationResults.add(parentCrumbs, results);
  }

  /**
   * Append other results to the current stack with given parent crumbs.
   *
   * @param parentCrumbs The given parent crumbs to insert. Must be non {@code null}.
   * @param resultItems  The stack to append. Must be non {@code null}.
   */
  public void add(Collection<ValidationResults.CrumbInfo> parentCrumbs, Collection<ValidationResults.ValidationItem> resultItems) {
    validationResults.add(parentCrumbs, resultItems);
  }

  /**
   * Check if the results are below the {@code ValidationSeverity.ERROR}
   *
   * @return {@code true} if the results are below {@code ValidationSeverity.ERROR}.
   */
  public boolean isValid() {
    return validationResults.isValid();
  }
}
