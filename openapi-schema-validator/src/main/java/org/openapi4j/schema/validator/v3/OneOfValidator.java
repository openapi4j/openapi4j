package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

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

  OneOfValidator(final ValidationContext<OAI3> context,
                         final JsonNode schemaNode,
                         final JsonNode schemaParentNode,
                         final SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema, ONEOF);
  }

  @Override
  void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationData<?> validation) {
    if (schemas.isEmpty()) {
      validation.add(CRUMB_INFO, NO_VALID_SCHEMA_ERR);
      return;
    }

    List<ValidationResults> resultsOnError = new ArrayList<>();
    ValidationResults validResults = null;

    for (SchemaValidator schema : schemas) {
      ValidationData<?> schemaValidation = new ValidationData<>(validation.delegate());
      schema.validate(valueNode, schemaValidation);

      if (schemaValidation.isValid()) {
        if (validResults != null) {
          validation.add(CRUMB_INFO, MANY_VALID_SCHEMA_ERR);
          return;
        }

        validResults = schemaValidation.results();
      }  else {
        resultsOnError.add(schemaValidation.results());
      }
    }

    if (validResults != null) {
      // Append potential results from sub validation (INFO / WARN)
      validation.add(validation.results().crumbs(), validResults);
    } else {
      for (ValidationResults result : resultsOnError) {
        validation.add(validation.results().crumbs(), result);
      }
    }
  }
}
