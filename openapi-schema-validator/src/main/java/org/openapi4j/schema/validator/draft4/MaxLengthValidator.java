package org.openapi4j.schema.validator.draft4;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.common.BaseJsonValidator;
import org.openapi4j.schema.validator.common.ValidationContext;
import org.openapi4j.schema.validator.common.ValidationData;
import org.openapi4j.schema.validator.common.SchemaValidator;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXLENGTH;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * maxLength keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-7" />
 */
class MaxLengthValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1012, "Max length is '%s', found '%s'.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(MAXLENGTH, true);

  private final Integer maxLength;

  MaxLengthValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    maxLength
      = schemaNode.isIntegralNumber()
      ? schemaNode.intValue()
      : null;
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (maxLength == null || !valueNode.isTextual()) {
      return false;
    }

    String value = valueNode.textValue();
    int length = value.codePointCount(0, value.length());
    if (length > maxLength) {
      validation.add(CRUMB_INFO, ERR, maxLength, length);
    }

    return false;
  }
}
