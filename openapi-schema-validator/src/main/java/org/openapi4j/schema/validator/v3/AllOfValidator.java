package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;

class AllOfValidator extends DiscriminatorValidator {
  AllOfValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema, ALLOF);
  }

  @Override
  public void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results) {
    for (SchemaValidator schema : schemas) {
      schema.validate(valueNode, results);
    }
  }
}
