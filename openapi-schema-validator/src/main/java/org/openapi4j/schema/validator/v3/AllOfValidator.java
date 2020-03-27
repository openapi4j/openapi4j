package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * allOf keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class AllOfValidator extends DiscriminatorValidator {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1001, "Schema description is erroneous. allOf should have at least 1 element.");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(ALLOF, true);

  static AllOfValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new AllOfValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private AllOfValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema, ALLOF);
  }

  @Override
  public void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationResults results) {
    if (schemas.isEmpty()) {
      results.add(CRUMB_INFO, ERR);
      return;
    }

    validate(() -> {
      for (SchemaValidator schema : schemas) {
        schema.validateWithContext(valueNode, results);
      }
    });
  }
}
