package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ONEOF;

/**
 * oneOf keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class OneOfValidator extends DiscriminatorValidator {
  private static final String NO_VALID_SCHEMA_ERR_MSG = "No valid schema.";
  private static final String MANY_VALID_SCHEMA_ERR_MSG = "More than 1 schema is valid.";

  static OneOfValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new OneOfValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  OneOfValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema, ONEOF);
  }

  @Override
  void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results) {
    ValidationResults errorResults = new ValidationResults();
    final int schemasSize = schemas.size();
    final int nbSchemasOnError;

    for (SchemaValidator schema : schemas) {
      schema.validate(valueNode, errorResults);
    }

    nbSchemasOnError = errorResults.size();

    if (nbSchemasOnError == schemasSize) {
      results.add(errorResults);
      results.addError(NO_VALID_SCHEMA_ERR_MSG, ONEOF);
    } else if ((schemasSize - nbSchemasOnError) > 1) {
      results.addError(MANY_VALID_SCHEMA_ERR_MSG, ONEOF);
    }
  }
}
