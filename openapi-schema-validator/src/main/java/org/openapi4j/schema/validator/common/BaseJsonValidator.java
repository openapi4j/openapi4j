package org.openapi4j.schema.validator.common;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.validation.ValidationCode;
import org.openapi4j.core.validation.ValidationException;

/**
 * The base class of all validators.
 */
public abstract class BaseJsonValidator implements JsonValidator {
  private static final String VALIDATION_ERR_MSG = "Schema validation failed";

  private final JsonNode schemaNode;
  private final JsonNode schemaParentNode;
  private final SchemaValidator parentSchema;
  protected final ValidationContext context;

  // Enforce the signature of validators
  protected BaseJsonValidator(final ValidationContext context,
                              final JsonNode schemaNode,
                              final JsonNode schemaParentNode,
                              final SchemaValidator parentSchema) {

    this.context = context;
    this.schemaNode = schemaNode;
    this.schemaParentNode = schemaParentNode;
    this.parentSchema = parentSchema;
  }

  @Override
  public void validate(final JsonNode valueNode) throws ValidationException {
    final ValidationData<?> validation = new ValidationData<>();

    validate(valueNode, validation);

    if (!validation.isValid()) {
      throw new ValidationException(VALIDATION_ERR_MSG, validation.results());
    }
  }

  protected void validate(ValidationCode code) {
    try {
      code.validate();
    } catch (ValidationException e) {
      // Results are already populated
    }
  }

  public JsonNode getSchemaNode() {
    return schemaNode;
  }

  public JsonNode getParentSchemaNode() {
    return schemaParentNode;
  }

  public SchemaValidator getParentSchema() {
    return parentSchema;
  }
}
