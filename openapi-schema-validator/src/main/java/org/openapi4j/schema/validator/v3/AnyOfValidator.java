package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ANYOF;

/**
 * anyOf keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class AnyOfValidator extends DiscriminatorValidator {
  static AnyOfValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new AnyOfValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  AnyOfValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema, ANYOF);
  }

  @Override
  void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results) {
    ValidationResults collectedResults = new ValidationResults();

    for (SchemaValidator schema : schemas) {
      ValidationResults singleResult = new ValidationResults();
      schema.validate(valueNode, singleResult);

      if (singleResult.size() == 0) {
        return;
      } else {
        collectedResults.add(singleResult);
      }
    }

    results.add(collectedResults);
  }
}
