package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MINLENGTH;

/**
 * A string instance is valid against this keyword if its length is
 * greater than, or equal to, the value of this keyword.
 * <p>
 * The length of a string instance is defined as the number of its
 * characters as defined by RFC 7159 [RFC7159].
 * <p>
 * The value of this keyword MUST be an integer.  This integer MUST be
 * greater than, or equal to, 0.
 * <p>
 * "minLength", if absent, may be considered as being present with
 * integer value 0.
 */
class MinLengthValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Minimum is '%s', found '%s'.";

  private final int minLength;

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
