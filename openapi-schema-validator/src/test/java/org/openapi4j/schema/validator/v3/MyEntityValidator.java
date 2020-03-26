package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

public class MyEntityValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult ERR = new ValidationResult(ValidationSeverity.ERROR, 5, "Strings are not equal!");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo("x-myentity-val", true);

  private MyEntityValidator(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationResults results) {
    if (!valueNode.isObject() || !valueNode.get("aString").equals(valueNode.get("bString"))) {
      results.add(CRUMB_INFO, ERR);
    }

    return true;
  }

  public static JsonValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new MyEntityValidator(context, schemaNode, schemaParentNode, parentSchema);
  }
}
