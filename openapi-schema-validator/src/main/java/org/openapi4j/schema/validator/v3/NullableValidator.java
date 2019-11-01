package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NULLABLE;

/**
 * nullable keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 */
class NullableValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Null value is not allowed.";

  private final boolean nullable;

  NullableValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    nullable = schemaNode.isBoolean() && schemaNode.booleanValue();
  }

  @Override
  public void validate(JsonNode valueNode, ValidationResults results) {
    if (nullable) return;

    if (valueNode == null || valueNode.isNull()) {
      results.addError(ERR_MSG, NULLABLE);
    }
  }
}
