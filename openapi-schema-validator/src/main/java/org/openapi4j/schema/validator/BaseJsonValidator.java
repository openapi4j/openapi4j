package org.openapi4j.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.OAI;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.v3.SchemaValidator;

/**
 * The base class of all validators.
 */
public abstract class BaseJsonValidator<O extends OAI> implements JsonValidator {
  private static final String VALIDATION_ERR_MSG = "Schema validation failed";

  private final JsonNode schemaNode;
  private final JsonNode schemaParentNode;
  private final SchemaValidator parentSchema;
  protected ValidationContext<O> context;

  // Empty constructor to enforce the signature of validators
  public BaseJsonValidator(final ValidationContext<O> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    this.context = context;
    this.schemaNode = schemaNode;
    this.schemaParentNode = schemaParentNode;
    this.parentSchema = parentSchema;
  }

  @Override
  public void validate(final JsonNode valueNode) throws ValidationException {
    ValidationResults results = new ValidationResults();

    validate(valueNode, results);

    if (results.size() != 0) {
      throw new ValidationException(VALIDATION_ERR_MSG, results);
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
