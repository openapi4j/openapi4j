package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ONEOF;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * oneOf keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class OneOfValidator extends DiscriminatorValidator {
  private static final ValidationResult NO_VALID_SCHEMA_ERR = new ValidationResult(ERROR, 1022, "No valid schema.");
  private static final ValidationResult MANY_VALID_SCHEMA_ERR = new ValidationResult(ERROR, 1023, "More than 1 schema is valid.");

  static OneOfValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new OneOfValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private OneOfValidator(final ValidationContext<OAI3> context,
                         final JsonNode schemaNode,
                         final JsonNode schemaParentNode,
                         final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema, ONEOF);
  }

  @Override
  void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results) {
    final int schemasSize = schemas.size();
    int nbSchemasOnError = 0;
    // Copy crumbs from current result set
    ValidationResults tmpResults = new ValidationResults(results, false, false);

    for (SchemaValidator schema : schemas) {
      schema.validate(valueNode, tmpResults);
      if (!tmpResults.isValid()) {
        nbSchemasOnError++;
        if ((schemasSize - nbSchemasOnError) > 1) {
          results.add(ONEOF, MANY_VALID_SCHEMA_ERR);
          // Early exit, no need to continue with others.
          return;
        }
      }
    }

    if (nbSchemasOnError == schemasSize) {
      results.add(ONEOF, NO_VALID_SCHEMA_ERR);
    } else if ((schemasSize - nbSchemasOnError) > 1) {
      results.add(ONEOF, MANY_VALID_SCHEMA_ERR);
    } else {
      results.add(tmpResults);
    }
  }
}
