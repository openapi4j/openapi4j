package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;

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
  private static final ValidationResult NO_VALID_SCHEMA_ERR = new ValidationResult(ERROR, 1022, "Schema description is erroneous. oneOf should have at least 1 element.");
  private static final ValidationResult MANY_VALID_SCHEMA_ERR = new ValidationResult(ERROR, 1023, "More than 1 schema is valid.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(ONEOF, true);

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
    if (schemas.isEmpty()) {
      results.add(CRUMB_INFO, NO_VALID_SCHEMA_ERR);
      return;
    }

    List<ValidationResults> resultsOnError = new ArrayList<>();
    ValidationResults validResults = null;

    for (SchemaValidator schema : schemas) {
      ValidationResults schemaResults = new ValidationResults();
      schema.validate(valueNode, schemaResults);

      if (schemaResults.isValid()) {
        if (validResults != null) {
          results.add(CRUMB_INFO, MANY_VALID_SCHEMA_ERR);
          return;
        }

        validResults = schemaResults;
      }  else {
        resultsOnError.add(schemaResults);
      }
    }

    if (validResults != null) {
      // Append potential results from sub validation (INFO / WARN)
      results.add(results.crumbs(), validResults);
    } else {
      for (ValidationResults result : resultsOnError) {
        results.add(results.crumbs(), result);
      }
    }
  }
}
