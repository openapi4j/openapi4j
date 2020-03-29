package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NULLABLE;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * nullable keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 */
class NullableValidator<V> extends BaseJsonValidator<OAI3, V> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1021, "Null value is not allowed.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(NULLABLE, true);

  private final boolean nullable;

  NullableValidator(final ValidationContext<OAI3, V> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator<V> parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    nullable = schemaNode.isBoolean() && schemaNode.booleanValue();
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationData<V> validation) {
    if (!nullable && valueNode.isNull()) {
      validation.add(CRUMB_INFO, ERR);
    }

    return false;
  }
}
