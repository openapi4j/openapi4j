package org.openapi4j.core.validation;

/**
 * The validation severity enumeration.
 */
public enum ValidationSeverity {
  NONE, INFO, WARNING, ERROR;

  public boolean lt(ValidationSeverity other) {
    return compareTo(other) < 0;
  }

  public boolean le(ValidationSeverity other) {
    return compareTo(other) <= 0;
  }

  public boolean gt(ValidationSeverity other) {
    return compareTo(other) > 0;
  }

  public boolean ge(ValidationSeverity other) {
    return compareTo(other) >= 0;
  }
}
