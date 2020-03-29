package org.openapi4j.schema.validator.v3;

import com.fasterxml.jackson.databind.JsonNode;
import org.openapi4j.core.model.v3.OAI3;
import org.openapi4j.core.validation.ValidationResult;
import org.openapi4j.core.validation.ValidationResults;
import org.openapi4j.core.validation.ValidationSeverity;
import org.openapi4j.schema.validator.BaseJsonValidator;
import org.openapi4j.schema.validator.ValidationContext;
import org.openapi4j.schema.validator.ValidationData;

import static org.openapi4j.core.model.v3.OAI3SchemaKeywords.TYPE;

public class TypeInfoValidator extends BaseJsonValidator<OAI3> {
  private static final ValidationResult INFO = new ValidationResult(ValidationSeverity.INFO, null, "Given type is '%s'.");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(TYPE, true);

  private final String type;

  public TypeInfoValidator(ValidationContext<OAI3> context,
                           JsonNode schemaNode,
                           JsonNode schemaParentNode,
                           SchemaValidator parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    type = schemaNode.textValue();
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationData<?> validation) {
    validation.add(CRUMB_INFO, INFO, type);

    if (validation.delegate() instanceof TypeInfoDelegate) {
      TypeInfoDelegate delegate = (TypeInfoDelegate) validation.delegate();

      if (delegate.isRequest) {
        delegate.log(validation, true);
      }
    }

    return true;
  }
}
