package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;

import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.MAXPROPERTIES;

class MaxPropertiesValidator extends BaseJsonValidator<OAI3> {
  private static final String ERR_MSG = "Max properties is less than '%s'.";

  private final int max;

  MaxPropertiesValidator(final ValidationContext<OAI3> context, final JsonNode schemaNode, final JsonNode schemaParentNode, final SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    max = (schemaNode.isIntegralNumber()) ? schemaNode.intValue() : Integer.MAX_VALUE;
  }

  @Override
  public void validate(final JsonNode valueNode, final ValidationResults results) {
    if (!valueNode.isObject()) {
      return;
    }

    if (valueNode.size() > max) {
      results.addError(String.format(ERR_MSG, valueNode.size()), MAXPROPERTIES);
    }
  }
}
