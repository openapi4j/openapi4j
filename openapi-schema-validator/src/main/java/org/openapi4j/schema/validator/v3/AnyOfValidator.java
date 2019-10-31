package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ANYOF;

class AnyOfValidator extends DiscriminatorValidator {
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
