package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NOT;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * not keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-12" />
 */
class NotValidator<V> extends BaseJsonValidator<OAI3, V> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1020, "Schema should not be valid.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(NOT, true);

  private final SchemaValidator<V> schema;

  NotValidator(final ValidationContext<OAI3, V> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator<V> parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    schema = new SchemaValidator<>(context, CRUMB_INFO, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<V> validation) {
    try {
      schema.validate(valueNode);
      validation.add(CRUMB_INFO, ERR);
    } catch (ValidationException ex) {
      // Succeed case
    }

    return false;
  }
}
