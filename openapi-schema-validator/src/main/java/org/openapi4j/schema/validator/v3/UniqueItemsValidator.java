package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.HashSet;
import java.util.Set;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.UNIQUEITEMS;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * uniqueItems keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-8" />
 */
class UniqueItemsValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1028, "Uniqueness is not respected '%s'.");

  private final boolean unique;

  static UniqueItemsValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new UniqueItemsValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private UniqueItemsValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    unique = schemaNode.isBoolean() && schemaNode.booleanValue();
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (!unique) {
      return;
    }

    Set<JsonNode> set = new HashSet<>();
    for (JsonNode n : valueNode) {
      if (!set.add(n)) {
        results.add(UNIQUEITEMS, ERR, n.asText());
      }
    }
  }
}
