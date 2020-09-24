package org.openapi4j.schema.validator.draft4;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.common.BaseJsonValidator;
import org.openapi4j.schema.validator.common.ValidationContext;
import org.openapi4j.schema.validator.common.ValidationData;
import org.openapi4j.schema.validator.common.SchemaValidator;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXPROPERTIES;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * maxProperties keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-9" />
 */
class MaxPropertiesValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1013, "Maximum is '%s', found '%s'.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(MAXPROPERTIES, true);

  private final Integer max;

  MaxPropertiesValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    max
      = schemaNode.isIntegralNumber()
      ? schemaNode.intValue()
      : null;
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (max == null || !valueNode.isObject()) {
      return false;
    }

    if (valueNode.size() > max) {
      validation.add(CRUMB_INFO, ERR, max, valueNode.size());
    }

    return false;
  }
}
