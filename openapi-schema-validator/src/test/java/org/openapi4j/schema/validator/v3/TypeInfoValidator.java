package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.JsonValidator;
import org.openapi4j.schema.validator.ValidationContext;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE;

public class TypeInfoValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult INFO = new ValidationResult(ValidationSeverity.INFO, null, "Given type is '%s'.");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(TYPE, true);

  private final String type;

  public static JsonValidator create(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    return new TypeInfoValidator(context, schemaNode, schemaParentNode, parentSchema);
  }

  private TypeInfoValidator(ValidationContext<OAI3> context, JsonNode schemaNode, JsonNode schemaParentNode, SchemaValidator parentSchema) {
    super(context, schemaNode, schemaParentNode, parentSchema);

    type = schemaNode.textValue();
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationResults results) {
    results.add(CRUMB_INFO, INFO, type);

    return true;
  }
}
