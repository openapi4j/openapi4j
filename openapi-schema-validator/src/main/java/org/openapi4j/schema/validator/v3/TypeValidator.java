package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.*;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * type keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class TypeValidator<V> extends BaseJsonValidator<OAI3, V> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1027, "Type expected '%s', found '%s'.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(TYPE, true);

  // non-OAS type
  private static final String TYPE_NULL = "null";

  private final String type;

  TypeValidator(final ValidationContext<OAI3, V> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator<V> parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    type = schemaNode.textValue();
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<V> validation) {
    String valueType = getTypeFromValue(valueNode);
    if (!valueType.equals(type)) {
      if (TYPE_NUMBER.equals(type) && TYPE_INTEGER.equals(valueType)) {
        // number includes integer
        // https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11
        return false;
      }

      if (!TYPE_NULL.equals(valueType)) {
        validation.add(CRUMB_INFO, ERR, type, valueType);
      }
    }

    return false;
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
