package org.openapi4j.core.validation;

public class ValidationResult {
  private final ValidationSeverity severity;
  private final int code;
  private final String message;

  public ValidationResult() {
    this.severity = null;
    this.code = 0;
    this.message = null;
  }

  public ValidationResult(ValidationSeverity severity, int code, String message) {
    this.severity = severity;
    this.code = code;
    this.message = message;
  }

  public ValidationSeverity severity() {
    return severity;
  }

  public int code() {
    return code;
  }

  public String message() {
    return message;
  }
}
