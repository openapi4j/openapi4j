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

public class TypeInfoValidator extends BaseJsonValidator<OAI3, TypeInfoDelegate> {
  private static final ValidationResult INFO = new ValidationResult(ValidationSeverity.INFO, null, "Given type is '%s'.");
  private static final ValidationResults.CrumbInfo CRUMB_INFO = new ValidationResults.CrumbInfo(TYPE, true);

  private final String type;

  public TypeInfoValidator(ValidationContext<OAI3, TypeInfoDelegate> context,
                           JsonNode schemaNode,
                           JsonNode schemaParentNode,
                           SchemaValidator<TypeInfoDelegate> parentSchema) {

    super(context, schemaNode, schemaParentNode, parentSchema);

    type = schemaNode.textValue();
  }

  @Override
  public boolean validate(JsonNode valueNode, ValidationData<TypeInfoDelegate> validation) {
    validation.add(CRUMB_INFO, INFO, type);

    TypeInfoDelegate delegate = validation.delegate();
    if (delegate != null) {
      delegate.log(validation, true);
    }

    return true;
  }
}
