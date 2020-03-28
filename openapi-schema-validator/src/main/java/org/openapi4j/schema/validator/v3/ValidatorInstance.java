package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

/**
 * Represents a function that creates a new instance of validator.
 */
@FunctionalInterface
public interface ValidatorInstance<V> {
  JsonValidator<V> apply(
    final ValidationContext<OAI3, V> context,
    final JsonNode schemaNode,
    final JsonNode schemaParentNode,
    final SchemaValidator<V> parentSchema);
}
