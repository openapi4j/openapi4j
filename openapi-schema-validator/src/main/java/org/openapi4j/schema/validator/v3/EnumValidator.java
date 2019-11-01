package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ENUM;

/**
 * enum keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-10" />
 */
class EnumValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Value '%s' is not defined in the schema.";

  private final JsonNode schemaNode;

  EnumValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    this.schemaNode = schemaNode;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (schemaNode != null && schemaNode.isArray()) {
      for (JsonNode enumNode : schemaNode) {
        if (enumNode.equals(valueNode)) {
          return;
        }
      }
    }

    results.addError(String.format(ERR_MSG, valueNode.asText()), ENUM);
  }
}
