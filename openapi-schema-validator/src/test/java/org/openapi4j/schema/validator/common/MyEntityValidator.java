package org.openapi4j.schema.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;

public class MyEntityValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ValidationSeverity.ERROR, 5, "Strings are not equal!");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo("x-myentity-val", true);

  public MyEntityValidator(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public boolean validate(final JsonNode valueNode, final ValidationData<?> validation) {
    if (!valueNode.isObject() || !valueNode.get("aString").equals(valueNode.get("bString"))) {
      validation.add(CRUMB_INFO, ERR);
    }

    return true;
  }
}
