package org.openapi4j.core.validation;

/**
 * The validation severity enumeration.
 */
public enum ValidationSeverity {
  NONE(0), INFO(1), WARNING(2), ERROR(3);

  private final int value;

  ValidationSeverity(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

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
