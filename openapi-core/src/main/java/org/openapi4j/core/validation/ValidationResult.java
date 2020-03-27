package org.openapi4j.core.validation;

public class ValidationResult {
  private final ValidationSeverity severity;
  private final Integer code;
  private final String message;

  /**
   * Should never be used.
   * Internal for serialization.
   */
  public ValidationResult() {
    this.severity = ValidationSeverity.NONE;
    this.code = null;
    this.message = null;
  }

  public ValidationResult(ValidationSeverity severity, Integer code, String message) {
    this.severity = severity;
    this.code = code;
    this.message = message;
  }

  public ValidationSeverity severity() {
    return severity;
  }

  public Integer code() {
    return code;
  }

  public String message() {
    return message;
  }
}
