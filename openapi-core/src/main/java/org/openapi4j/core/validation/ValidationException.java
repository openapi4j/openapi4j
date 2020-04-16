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
   * @return The validation results.
   */
  public ValidationResults results() {
    return results;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    if (getMessage() != null) {
      builder.append(getMessage());
    }

    if (results != null) {
      if (getMessage() != null) {
        builder.append(String.format("%n"));
      }

      builder.append(results.toString());
    }

    return builder.toString();
  }
}
