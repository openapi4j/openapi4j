package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ANYOF;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * anyOf keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class AnyOfValidator extends DiscriminatorValidator {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1001, "No valid schema.");

  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(ANYOF, true);

  static AnyOfValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new AnyOfValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private AnyOfValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema, ANYOF);
  }

  @Override
  void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results) {
    for (SchemaValidator schema : schemas) {
      ValidationResults anyOfResults = new ValidationResults();
      schema.validate(valueNode, anyOfResults);

      if (anyOfResults.isValid()) {
        // Append potential results from sub validation (INFO / WARN)
        results.add(anyOfResults);
        return;
      }
    }

    results.add(CRUMB_INFO, ERR);
  }
}
