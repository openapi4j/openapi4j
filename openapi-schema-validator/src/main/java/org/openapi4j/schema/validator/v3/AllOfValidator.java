package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.ALLOF;
import static org.openapi4j.core.validation.ValidationSeverity.ERROR;

/**
 * allOf keyword validator.
 * <p/>
 * <a href="https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject" />
 * <p/>
 * <a href="https://tools.ietf.org/html/draft-wright-json-schema-validation-00#page-11" />
 */
class AllOfValidator<V> extends DiscriminatorValidator<V> {
  private static final ValidationResult ERR = new ValidationResult(ERROR, 1001, "Schema description is erroneous. allOf should have at least 1 element.");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(ALLOF, true);


  AllOfValidator(final ValidationContext<OAI3, V> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator<V> parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema, ALLOF);
  }

  @Override
  public void validateWithoutDiscriminator(final JsonNode valueNode, final ValidationData<V> validation) {
    if (schemas.isEmpty()) {
      validation.add(CRUMB_INFO, ERR);
      return;
    }

    validate(() -> {
      for (SchemaValidator<V> schema : schemas) {
        schema.validateWithContext(valueNode, validation);
      }
    });
  }
}
