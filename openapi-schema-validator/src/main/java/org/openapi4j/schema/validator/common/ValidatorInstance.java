package org.openapi4j.schema.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;

/**
 * Represents a function that creates a new instance of validator.
 */
@FunctionalInterface
public interface ValidatorInstance {
  JsonValidator apply(
    final ValidationContext<OAI3> context,
    final JsonNode schemaNode,
    final JsonNode schemaParentNode,
    final SchemaValidator parentSchema);
}
