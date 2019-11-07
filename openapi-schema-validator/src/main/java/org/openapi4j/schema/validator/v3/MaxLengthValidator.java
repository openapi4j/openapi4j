package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXLENGTH;

/**
 * maxLength keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-7" />
 */
class MaxLengthValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Max length is '%s', found '%s'.";

  private final int maxLength;

  static MaxLengthValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new MaxLengthValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  MaxLengthValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    maxLength = (schemaNode != null && schemaNode.isIntegralNumber())
      ? schemaNode.intValue()
      : Integer.MAX_VALUE;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (!valueNode.isTextual()) {
      return;
    }

    String value = valueNode.textValue();
    int length = value.codePointCount(0, value.length());
    if (length > maxLength) {
      results.addError(String.format(ERR_MSG, maxLength, length), MAXLENGTH);
    }
  }
}
