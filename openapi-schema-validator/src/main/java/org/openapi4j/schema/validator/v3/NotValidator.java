package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.NOT;

class NotValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Schema should not be valid.";

  private final SchemaValidator schema;

  NotValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    schema = new SchemaValidator(context, NOT, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    ValidationResults singleResult = new ValidationResults();
    schema.validate(valueNode, singleResult);

    if (singleResult.size() == 0) {
      results.addError(ERR_MSG, NOT);
    }
  }
}
