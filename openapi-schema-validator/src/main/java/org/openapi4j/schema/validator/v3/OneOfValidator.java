package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
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
    ValidationResults oneOfValidResults = null;

    for (SchemaValidator schema : schemas) {
      ValidationResults oneOfResults = new ValidationResults();
      schema.validate(valueNode, oneOfResults);

      if (oneOfResults.isValid()) {
        if (oneOfValidResults != null) {
          results.add(ONEOF, MANY_VALID_SCHEMA_ERR);
          return;
        } else {
          oneOfValidResults = oneOfResults;
        }
      }
    }

    if (oneOfValidResults != null) {
      // Append potential results from sub validation (INFO / WARN)
      results.add(oneOfValidResults);
    } else {
      results.add(ONEOF, NO_VALID_SCHEMA_ERR);
    }
  }
}
