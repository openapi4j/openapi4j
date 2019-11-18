package org.openapi4j.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;

/**
 * Representation of a validator.
 */
public interface JsonValidator {
  /**
   * Validate the given value from the validation setup.
   *
   * @param valueNode The given value to check.
   * @param results   The result stack to append any additional info from the validation.
   *                  All errors are returned from the stack.
   */
  void validate(final JsonNode valueNode, final ValidationResults results);

  /**
   * Validate the given value from the validation setup.
   * This method has fast fail behaviour.
   * {@link ValidationException} is thrown on first error(s) encountered.
   *
   * @param valueNode The given value to check.
   * @throws ValidationException The result stack info from the validation in case of error.
   */
  void validate(final JsonNode valueNode) throws ValidationException;
}
