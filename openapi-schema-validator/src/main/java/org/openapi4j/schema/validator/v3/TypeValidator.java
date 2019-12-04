package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_ARRAY;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_BOOLEAN;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_INTEGER;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_NUMBER;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_OBJECT;
import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE_STRING;

/**
 * type keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class TypeValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Type expected '%s', found '%s'.";
  // non-OAS type
  private static final String TYPE_NULL = "null";

  private final String type;

  static TypeValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new TypeValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private TypeValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    type = schemaNode.textValue();
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    String valueType = getTypeFromValue(valueNode);
    if (!valueType.equals(type)) {
      if (TYPE_NUMBER.equals(type) && TYPE_INTEGER.equals(valueType)) {
        // number includes integer
        // https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11
        return;
      }

      if (!TYPE_NULL.equals(valueType)) {
        results.addError(String.format(ERR_MSG, type, valueType), TYPE);
      }
    }
  }

  private String getTypeFromValue(JsonNode valueNode) {
    if (valueNode.isContainerNode()) {
      return valueNode.isObject() ? TYPE_OBJECT : TYPE_ARRAY;
    }

    if (valueNode.isTextual())
      return TYPE_STRING;
    else if (valueNode.isIntegralNumber())
      return TYPE_INTEGER;
    else if (valueNode.isNumber())
      return TYPE_NUMBER;
    else if (valueNode.isBoolean())
      return TYPE_BOOLEAN;
    else
      return TYPE_NULL;
  }
}
