package org.openapi4j.core.validation;

/**
 * The validation severity enumeration.
 */
public enum ValidationSeverity {
  NONE(0), INFO(1), WARNING(2), ERROR(3);

  private final byte value;

  ValidationSeverity(int value) {
    this.value = (byte) value;
  }

  public int getValue() {
    return value;
  }

  public boolean lt(ValidationSeverity other) {
    return value < other.value;
  }

  public boolean le(ValidationSeverity other) {
    return value <= other.value;
  }

  public boolean gt(ValidationSeverity other) {
    return value > other.value;
  }

  public boolean ge(ValidationSeverity other) {
    return value >= other.value;
  }
}
