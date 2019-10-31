package org.openapi4j.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;

public interface JsonValidator<O extends OAI> {
  void validate(final JsonNode valueNode, final ValidationResults results);
  void validate(final JsonNode valueNode) throws ValidationException;
}
