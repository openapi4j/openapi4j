package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINLENGTH;

/**
 * minLength keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-7" />
 */
class MinLengthValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Minimum is '%s', found '%s'.";

  private final int minLength;

  static MinLengthValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new MinLengthValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  MinLengthValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    minLength = (schemaNode != null && schemaNode.isIntegralNumber())
      ? schemaNode.intValue()
      : 0;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (!valueNode.isTextual()) {
      return;
    }

    String value = valueNode.textValue();
    int length = value.codePointCount(0, value.length());
    if (length < minLength) {
      results.addError(String.format(ERR_MSG, minLength, length), MINLENGTH);
    }
  }
}
