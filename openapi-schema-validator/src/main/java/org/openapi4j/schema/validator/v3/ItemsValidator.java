package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ITEMS;

/**
 * items keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-7" />
 */
class ItemsValidator extends BaseJsonValidator<OAI3> {
  private final SchemaValidator schema;

  static ItemsValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new ItemsValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private ItemsValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    schema = new SchemaValidator(context, ITEMS, schemaNode, schemaParentNode, parentSchema, true);
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationResults results) {
    if (!valueNode.isArray()) {
      return false;
    }

    validate(() -> {
      for (JsonNode itemNode : valueNode) {
        schema.validateWithContext(itemNode, results);
      }
    });

    return false;
  }
}
