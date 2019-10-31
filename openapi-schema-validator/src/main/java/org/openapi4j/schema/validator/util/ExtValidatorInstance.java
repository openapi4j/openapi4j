package org.openapi4j.schema.validator.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.v3.SchemaValidator;

/**
 * Represents a function that creates a new instance of validator extension.
 */
@FunctionalInterface
public interface ExtValidatorInstance<O extends OAI> {
  JsonValidator<O> apply(
    final ValidationContext<OAI3> context,
    final JsonNode schemaNode,
    final JsonNode schemaParentNode,
    final SchemaValidator parentSchema);
}
