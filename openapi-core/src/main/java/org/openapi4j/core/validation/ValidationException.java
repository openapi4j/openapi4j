package org.openapi4j.core.validation;

/**
 * Representation of a validation error.
 */
public class ValidationException extends Exception {
  private final ValidationResults results;

  public ValidationException(String message) {
    super(message);
    results = null;
  }

  public ValidationException(Throwable cause) {
    super(cause);
    results = null;
  }

  public ValidationException(String message, ValidationResults results) {
    super(message);
    this.results = results;
  }

  /**
   * Get associated results from the validation.
   * @return
   */
  public ValidationResults getResults() {
    return results;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(getMessage());
    if (results != null) {
      builder.append(results.toString());
    }
    return builder.toString();
  }
}
