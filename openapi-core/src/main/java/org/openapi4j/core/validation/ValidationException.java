package org.openapi4j.core.validation;

public class ValidationException extends Exception {
  private ValidationResults results;

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(Throwable cause) {
    super(cause);
  }

  public ValidationException(String message, ValidationResults results) {
    super(message);

    this.results = results;
  }

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
