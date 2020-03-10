package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NOT;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * not keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-12" />
 */
class NotValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1020, "Schema should not be valid.");

  private final SchemaValidator schema;

  static NotValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new NotValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private NotValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    schema = new SchemaValidator(context, NOT, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    try {
      schema.validate(valueNode);
      results.add(NOT, ERR);
    } catch (ValidationException ex) {
      // Succeed case
    }
  }
}
