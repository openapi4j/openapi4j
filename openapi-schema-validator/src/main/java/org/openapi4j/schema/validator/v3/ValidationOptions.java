package org.openapi4j.schema.validator.v3;

/**
 * Options to change the behaviour of the default implementation.
 */
public final class ValidationOptions {
  /**
   * By default, in JSON schema v0, all schemas allow additional properties.
   * <p/>
   * Setting this to {@code true} invert the behaviour.
   */
  public static final byte ADDITIONAL_PROPS_RESTRICT = 1;
}
