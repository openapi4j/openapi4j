package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

public class MyEntityValidator<V> extends BaseJsonValidator<OAI3, V> {
  private static final ValidationResult ERR = new ValidationResult(ValidationSeverity.ERROR, 5, "Strings are not equal!");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo("x-myentity-val", true);

  public MyEntityValidator(ValidationContext<OAI3, V> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator<V> parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationData<V> validation) {
    if (!valueNode.isObject() || !valueNode.get("aString").equals(valueNode.get("bString"))) {
      validation.add(CRUMB_INFO, ERR);
    }

    return true;
  }
}
